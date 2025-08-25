package dev.adlin;

import dev.adlin.commands.JoinCommand;
import dev.adlin.commands.LeaveCommand;
import dev.adlin.handlers.VoiceReceiveHandler;
import dev.adlin.handlers.VoiceSendingHandler;
import dev.adlin.llm.LlmManager;
import dev.adlin.manager.DiscordCommandManager;
import dev.adlin.manager.VoiceBufferManager;
import dev.adlin.stt.SttManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.EnumSet;

public class Bot {
    private final JDA jda;

    public Bot(String token) {
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

        SttManager sttManager = new SttManager();
        LlmManager llmManager = new LlmManager();

        VoiceBufferManager bufferManager = new VoiceBufferManager(sttManager.getCurrentClient(), llmManager);
        VoiceReceiveHandler voiceReceiveHandler = new VoiceReceiveHandler(bufferManager);
        VoiceSendingHandler voiceSendingHandler = new VoiceSendingHandler();

        Guild guild = jda.getGuildById("1317147191822909450");
        AudioManager audioManager = guild.getAudioManager();

        audioManager.setReceivingHandler(voiceReceiveHandler);
        audioManager.setSendingHandler(voiceSendingHandler);

        DiscordCommandManager discordCommandManager = new DiscordCommandManager(jda);

        discordCommandManager.addDiscordCommands(
                new JoinCommand(), new LeaveCommand()
        );

        discordCommandManager.registerCommands();
    }

    public JDA getJda() {
        return jda;
    }
}
