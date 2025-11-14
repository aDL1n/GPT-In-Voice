package dev.adlin.discord;

import dev.adlin.config.properties.DiscordConfig;
import dev.adlin.discord.command.JoinCommand;
import dev.adlin.discord.command.LeaveCommand;
import dev.adlin.discord.handler.VoiceReceiveHandler;
import dev.adlin.discord.handler.VoiceSendingHandler;
import dev.adlin.discord.listener.VoiceListener;
import dev.adlin.service.ModelService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;

@Component
public class DiscordBot {

    private static final Logger log = LogManager.getLogger(DiscordBot.class);

    private final JDA jda;
    private final Guild guild;
    private final VoiceListener voiceListener;
    private final ModelService modelService;
    private final VoiceReceiveHandler receiveHandler;
    private final VoiceSendingHandler sendingHandler;

    public DiscordBot(DiscordConfig discordConfig,
                      VoiceListener voiceListener,
                      ModelService modelService,
                      VoiceReceiveHandler receiveHandler,
                      VoiceSendingHandler sendingHandler
    ) {
        this.voiceListener = voiceListener;
        this.modelService = modelService;
        this.receiveHandler = receiveHandler;
        this.sendingHandler = sendingHandler;
        this.jda = JDABuilder.create(
                discordConfig.getToken(),
                EnumSet.of(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.MESSAGE_CONTENT
                )
        ).addEventListeners(
                voiceListener
                )
                .setActivity(Activity.customStatus("Waiting for you"))
                .setStatus(OnlineStatus.IDLE)
                .enableCache(CacheFlag.VOICE_STATE)
                .build();

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Assert.notNull(discordConfig.getGuildId(),  "Guild Id cannot be null");
        this.guild = this.jda.getGuildById(discordConfig.getGuildId());
    }

    @PostConstruct
    public void start() {
        AudioManager audioManager = this.guild.getAudioManager();

        audioManager.setSendingHandler(this.sendingHandler);
        audioManager.setReceivingHandler(this.receiveHandler);

        DiscordCommandManager commandManager = new DiscordCommandManager(jda);

        commandManager.addDiscordCommands(
            new JoinCommand(member -> {
                //run without block command thread
                CompletableFuture.runAsync(() -> {
                    SystemMessage systemMessage = new SystemMessage(member.getEffectiveName() + " пригласил тебя к себе в войс-чат");
                    this.modelService.ask(systemMessage);
                });
            }),
            new LeaveCommand(member -> {
                CompletableFuture.runAsync(() -> {
                    SystemMessage systemMessage = new SystemMessage(member.getEffectiveName() + " выгнал тебя из войс-чата");
                    this.modelService.ask(systemMessage);
                });
            })
        );

        commandManager.registerCommands();

    }

    @PreDestroy
    private void shutdown() {
        log.info("Shutting down DiscordBot");
        try {
            this.jda.shutdown();
            this.jda.awaitShutdown();
            log.info("DiscordBot has been shutdown");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public JDA getJda() {
        return jda;
    }

    public Guild getGuild() {
        return guild;
    }
}
