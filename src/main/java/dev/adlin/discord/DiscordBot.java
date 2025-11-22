package dev.adlin.discord;

import dev.adlin.discord.command.JoinCommand;
import dev.adlin.discord.command.LeaveCommand;
import dev.adlin.discord.handler.VoiceReceiveHandler;
import dev.adlin.discord.handler.VoiceSendingHandler;
import dev.adlin.discord.listener.VoiceListener;
import dev.adlin.service.ModelService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.stereotype.Component;

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

    public DiscordBot(
            JDA jda,
            Guild guild,
            VoiceListener voiceListener,
            ModelService modelService,
            VoiceReceiveHandler receiveHandler,
            VoiceSendingHandler sendingHandler
    ) {
        this.voiceListener = voiceListener;
        this.modelService = modelService;
        this.receiveHandler = receiveHandler;
        this.sendingHandler = sendingHandler;

        jda.addEventListener(voiceListener);

        this.jda = jda;
        this.guild = guild;
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

        this.voiceListener.OnUserJoin(event -> {
            CompletableFuture.runAsync(() -> {
               SystemMessage message = new SystemMessage(
                       "Пользователь %s присоединился к голосовому чату. Поприветствуй его"
                       .formatted(event.getEntity().getEffectiveName())
               );
               this.modelService.ask(message);
            });
        });

        this.voiceListener.OnUserLeave(event -> {
            SystemMessage message = new SystemMessage(
                    "Пользователь %s покинул голосовой чат"
                            .formatted(event.getEntity().getEffectiveName())
            );
            this.modelService.ask(message);
        });

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
}
