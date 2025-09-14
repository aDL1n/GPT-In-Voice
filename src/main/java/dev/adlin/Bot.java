package dev.adlin;

import dev.adlin.commands.JoinCommand;
import dev.adlin.commands.LeaveCommand;
import dev.adlin.database.impl.SQLite;
import dev.adlin.handlers.VoiceReceiveHandler;
import dev.adlin.handlers.VoiceSendingHandler;
import dev.adlin.listeners.DiscordVoiceListener;
import dev.adlin.llm.adapters.Role;
import dev.adlin.llm.adapters.impl.NomicEmbedding;
import dev.adlin.llm.adapters.impl.OllamaAdapter;
import dev.adlin.llm.memory.LongTermMemoryData;
import dev.adlin.llm.memory.MemoryManager;
import dev.adlin.llm.rag.InMemoryVectorStore;
import dev.adlin.llm.rag.RagService;
import dev.adlin.llm.rag.ScoredChunk;
import dev.adlin.manager.ChatManager;
import dev.adlin.manager.DiscordCommandManager;
import dev.adlin.manager.VoiceBufferManager;
import dev.adlin.stt.impl.Whisper;
import dev.adlin.tts.impl.Piper;
import dev.adlin.utils.AudioProvider;
import dev.adlin.utils.chat.ChatMessage;
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
    private static final Logger LOGGER = LogManager.getLogger(Bot.class);

    public Bot(String token, String guildId) {
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

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        SQLite sqLite = new SQLite();
        sqLite.load();

        MemoryManager memoryManager = new MemoryManager(sqLite);
        memoryManager.initializeLongTermMemory();

        Piper piper = new Piper();
        Whisper whisper = new Whisper();
        OllamaAdapter ollamaAdapter = new OllamaAdapter("gemma3:1b");

        InMemoryVectorStore vectorScore = new InMemoryVectorStore();
        NomicEmbedding embedding = new NomicEmbedding();
        RagService rag = new RagService(vectorScore, embedding);

        ChatManager chatManager = new ChatManager(ollamaAdapter);

        try {
            List<LongTermMemoryData> memories = sqLite.getLongTermMemories(100).get();
            rag.addDocuments(
                    memories.stream().map(data ->
                            data.role + " " + data.message
                    ).toList(),
                    "bootstrap",
                    "longterm"
            );
            LOGGER.info("Memories loaded from database");
        } catch (Exception e) {
            LOGGER.error("Failed to load memory", e);
        }

        VoiceBufferManager bufferManager = new VoiceBufferManager();
        AudioProvider audioProvider = new AudioProvider();

        VoiceReceiveHandler voiceReceiveHandler = new VoiceReceiveHandler(bufferManager);
        VoiceSendingHandler voiceSendingHandler = new VoiceSendingHandler(audioProvider);

        Guild guild = jda.getGuildById(guildId);
        AudioManager audioManager = guild.getAudioManager();

        audioManager.setReceivingHandler(voiceReceiveHandler);
        audioManager.setSendingHandler(voiceSendingHandler);

        jda.addEventListener(new DiscordVoiceListener(event -> {
            AudioChannelUnion joinedChannel = event.getChannelJoined();
            AudioChannelUnion leftChannel = event.getChannelLeft();

            if (joinedChannel != null &&
                    event.getEntity().getUser() != event.getJDA().getSelfUser() &&
                    guild.getAudioManager().getConnectedChannel().equals(joinedChannel)
            ) {
                chatManager.sendMessage(
                        new ChatMessage(Role.TOOL, "discord", event.getEntity().getUser().getName() + " has joined to your voice channel")
                );
            }

            if (leftChannel != null &&
                    event.getEntity().getUser() != event.getJDA().getSelfUser() &&
                    guild.getAudioManager().getConnectedChannel().equals(leftChannel)
            ) {
                chatManager.sendMessage(
                        new ChatMessage(Role.TOOL, "discord", event.getEntity().getUser().getName() + " has leaved from your voice channel")
                );
            }
        }));

        DiscordCommandManager discordCommandManager = new DiscordCommandManager(jda);

        discordCommandManager.addDiscordCommands(
                new JoinCommand(), new LeaveCommand()
        );

        discordCommandManager.registerCommands();

        bufferManager.setBufferListener((userId, data) -> {
            User user = guild.getMemberById(userId).getUser();

            String transcription = whisper.transcriptAudio(data);

            memoryManager.addToLongTermMemory(new LongTermMemoryData(Role.USER, Date.from(Instant.now()), user.getName(), transcription));

            List<ScoredChunk> hits = rag.search(transcription, 8, "longterm");
            String ragContext = RagService.formatChunks(hits);

            chatManager.sendMessage(new ChatMessage(Role.TOOL, "longtermmemory", "Подсказки из чата: " + ragContext));
            String result = chatManager.sendMessage(new ChatMessage(Role.USER, user.getName(), transcription));
            rag.addDocuments(Collections.singletonList(transcription), user.getName(), "longterm");

            memoryManager.addToLongTermMemory(new LongTermMemoryData(Role.ASSISTANT, Date.from(Instant.now()), result));
            rag.addDocuments(Collections.singletonList(result), "assistant", "longterm");

            byte[] speech = piper.speech(result);

            audioProvider.addAudio(speech);
        });
    }

    public JDA getJda() {
        return jda;
    }
}
