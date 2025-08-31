package dev.adlin;

import dev.adlin.commands.JoinCommand;
import dev.adlin.commands.LeaveCommand;
import dev.adlin.database.impl.SQLite;
import dev.adlin.handlers.VoiceReceiveHandler;
import dev.adlin.handlers.VoiceSendingHandler;
import dev.adlin.llm.adapters.Role;
import dev.adlin.llm.adapters.impl.OllamaAdapter;
import dev.adlin.llm.memory.LongTermMemoryData;
import dev.adlin.llm.memory.MemoryManager;
import dev.adlin.manager.DiscordCommandManager;
import dev.adlin.manager.VoiceBufferManager;
import dev.adlin.stt.impl.Whisper;
import dev.adlin.tts.impl.Piper;
import dev.adlin.utils.AudioProvider;
import dev.adlin.utils.PromptBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.time.Instant;
import java.util.Date;
import java.util.EnumSet;

public class Bot {
    private final JDA jda;

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
        MemoryManager memoryManager = new MemoryManager(sqLite);
        memoryManager.initializeLongTermMemory();

        Piper piper = new Piper();
        Whisper whisper = new Whisper();
        OllamaAdapter ollamaAdapter = new OllamaAdapter("gemma3:1b", memoryManager);
        ollamaAdapter.startChat();

        VoiceBufferManager bufferManager = new VoiceBufferManager();
        AudioProvider audioProvider = new AudioProvider();

        VoiceReceiveHandler voiceReceiveHandler = new VoiceReceiveHandler(bufferManager);
        VoiceSendingHandler voiceSendingHandler = new VoiceSendingHandler(audioProvider);

        Guild guild = jda.getGuildById(guildId);
        AudioManager audioManager = guild.getAudioManager();

        audioManager.setReceivingHandler(voiceReceiveHandler);
        audioManager.setSendingHandler(voiceSendingHandler);

        DiscordCommandManager discordCommandManager = new DiscordCommandManager(jda);

        discordCommandManager.addDiscordCommands(
                new JoinCommand(), new LeaveCommand()
        );

        discordCommandManager.registerCommands();

        bufferManager.setBufferListener(data -> {
            String transcription = whisper.transcriptAudio(data);

            memoryManager.addToLongTermMemory(new LongTermMemoryData(Role.USER, Date.from(Instant.now()), transcription));
            String result = ollamaAdapter.sendMessage(Role.USER, transcription);
            memoryManager.addToLongTermMemory(new LongTermMemoryData(Role.ASSISTANT, Date.from(Instant.now()), result));

            byte[] speech = piper.speech(PromptBuilder.clearString(result));

            audioProvider.addAudio(speech);
        });
    }

    public JDA getJda() {
        return jda;
    }
}
