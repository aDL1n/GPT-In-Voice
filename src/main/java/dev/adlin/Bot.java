package dev.adlin;

import dev.adlin.api.state.DiscordState;
import dev.adlin.api.state.SelectedModelsState;
import dev.adlin.discord.command.JoinCommand;
import dev.adlin.discord.command.LeaveCommand;
import dev.adlin.handler.VoiceReceiveHandler;
import dev.adlin.handler.VoiceSendingHandler;
import dev.adlin.listener.DiscordVoiceListener;
import dev.adlin.llm.adapter.Role;
import dev.adlin.llm.adapter.impl.NomicEmbedding;
import dev.adlin.llm.adapter.impl.OllamaAdapter;
import dev.adlin.llm.memory.entity.LongTermMemoryData;
import dev.adlin.llm.rag.InMemoryVectorStore;
import dev.adlin.llm.rag.RagService;
import dev.adlin.llm.rag.ScoredChunk;
import dev.adlin.llm.chat.ChatManager;
import dev.adlin.discord.DiscordCommandManager;
import dev.adlin.discord.audio.VoiceBufferManager;
import dev.adlin.llm.memory.LongTermMemoryService;
import dev.adlin.stt.SpeechToText;
import dev.adlin.stt.impl.Whisper;
import dev.adlin.tts.TextToSpeech;
import dev.adlin.tts.impl.Piper;
import dev.adlin.discord.audio.AudioProvider;
import dev.adlin.api.state.BotState;
import dev.adlin.api.state.util.BotStatus;
import dev.adlin.llm.chat.ChatMessage;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.*;

public class Bot {

    private final JDA jda;
    private final BotState botState;
    private final DiscordState discordState;
    private final SelectedModelsState selectedModelsState;
    private final String guildId;
    private final LongTermMemoryService longTermMemoryService;

    private static final Logger log = LogManager.getLogger(Bot.class);

    public Bot(
            String token,
            String guildId,
            LongTermMemoryService longTermMemoryService,
            BotState botState,
            DiscordState discordState,
            SelectedModelsState selectedModelsState) {
        this.guildId = guildId;
        this.longTermMemoryService = longTermMemoryService;
        this.jda = JDABuilder.create(
            token,
            EnumSet.of(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.MESSAGE_CONTENT
            )
        ).addEventListeners()
                .setActivity(Activity.customStatus("Waiting for you"))
                .setStatus(OnlineStatus.IDLE)
                .enableCache(CacheFlag.VOICE_STATE)
                .build();
        this.botState = botState;
        this.discordState = discordState;
        this.selectedModelsState = selectedModelsState;
    }

    @PostConstruct
    public void start() {
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Guild guild = jda.getGuildById(guildId);
        AudioManager audioManager = guild.getAudioManager();

        this.discordState.setAudioManager(audioManager);

        TextToSpeech piper = new Piper();
        SpeechToText whisper = new Whisper();
        OllamaAdapter ollamaAdapter = new OllamaAdapter("gemma3:1b");

        selectedModelsState.setAll(ollamaAdapter, whisper, piper);

        InMemoryVectorStore vectorScore = new InMemoryVectorStore();
        NomicEmbedding embedding = new NomicEmbedding();
        RagService rag = new RagService(vectorScore, embedding);

        ChatManager chatManager = new ChatManager(ollamaAdapter);

        try {
            List<LongTermMemoryData> memories = longTermMemoryService.getLongTermMemories(100);
            rag.addDocuments(
                    memories.stream().map(data ->
                            data.role + " " + data.content
                    ).toList(),
                    "bootstrap",
                    "longterm"
            );
            log.info("Memories loaded from database");
        } catch (Exception e) {
            log.error("Failed to load memory", e);
        }

        VoiceBufferManager bufferManager = new VoiceBufferManager();
        AudioProvider audioProvider = new AudioProvider();

        VoiceReceiveHandler voiceReceiveHandler = new VoiceReceiveHandler(bufferManager);
        VoiceSendingHandler voiceSendingHandler = new VoiceSendingHandler(audioProvider);

        audioManager.setReceivingHandler(voiceReceiveHandler);
        audioManager.setSendingHandler(voiceSendingHandler);

        jda.addEventListener(new DiscordVoiceListener(event -> {
            AudioChannelUnion joinedChannel = event.getChannelJoined();
            AudioChannelUnion leftChannel = event.getChannelLeft();

            if (joinedChannel != null &&
                    event.getEntity().getUser() != event.getJDA().getSelfUser() &&
                    guild.getAudioManager().getConnectedChannel().equals(joinedChannel) &&
                    guild.getAudioManager().isConnected()
            ) {
                chatManager.sendMessage(
                        new ChatMessage(Role.TOOL, "discord", event.getEntity().getUser().getName() + " has joined to your voice channel")
                );
            }

            if (leftChannel != null &&
                    event.getEntity().getUser() != event.getJDA().getSelfUser() &&
                    guild.getAudioManager().getConnectedChannel().equals(leftChannel) &&
                    guild.getAudioManager().isConnected()
            ) {
                chatManager.sendMessage(
                        new ChatMessage(Role.TOOL, "discord", event.getEntity().getUser().getName() + " has leaved from your voice channel")
                );
            }
        }));

        DiscordCommandManager discordCommandManager = new DiscordCommandManager(jda);

        discordCommandManager.addDiscordCommands(
                new JoinCommand(botState), new LeaveCommand(botState)
        );

        discordCommandManager.registerCommands();

        bufferManager.setBufferListener((userId, data) -> {
            User user = guild.getMemberById(userId).getUser();

            String transcription = whisper.transcriptAudio(data);

            longTermMemoryService.saveLongTermMemory(new LongTermMemoryData(Role.USER, Date.from(Instant.now()), user.getName(), transcription));
            rag.addDocuments(Collections.singletonList(transcription), user.getName(), "longterm");

            List<ScoredChunk> hits = rag.search(transcription, 8, "longterm");
            String ragContext = RagService.formatChunks(hits);

            botState.setCurrentPromptRequest(new ChatMessage(Role.USER, user.getName(), transcription));
            chatManager.sendMessage(new ChatMessage(Role.TOOL, "longtermmemory", "Подсказки из чата: " + ragContext));
            String result = chatManager.sendMessage(new ChatMessage(Role.USER, user.getName(), transcription));

            longTermMemoryService.saveLongTermMemory(new LongTermMemoryData(Role.USER, Date.from(Instant.now()), "assistant", result));
            rag.addDocuments(Collections.singletonList(result), "assistant", "longterm");

            byte[] speech = piper.speech(result);

            audioProvider.addAudio(speech);
        });

        botState.setStatus(BotStatus.READY);
    }

    public JDA getJda() {
        return jda;
    }
}
