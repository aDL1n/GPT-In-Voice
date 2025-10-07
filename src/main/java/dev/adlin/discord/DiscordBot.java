package dev.adlin.discord;

import dev.adlin.config.DiscordConfig;
import dev.adlin.discord.command.JoinCommand;
import dev.adlin.discord.command.LeaveCommand;
import dev.adlin.discord.handler.VoiceReceiveHandler;
import dev.adlin.discord.handler.VoiceSendingHandler;
import dev.adlin.discord.listener.VoiceListener;
import dev.adlin.service.ModelService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.awt.*;
import java.util.EnumSet;

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

        final MessageEmbed notInVoiceEmbed = new EmbedBuilder()
                .setTitle("You not in voice chat")
                .setColor(Color.RED)
                .build();

        final MessageEmbed joinedToVoiceEmbed = new EmbedBuilder()
                .setTitle("Successfully connected to the voice channel")
                .setColor(Color.GREEN)
                .build();

        commandManager.addDiscordCommands(
            new JoinCommand(event -> {
                Member member = event.getMember();

                Guild guild = event.getGuild();
                if (guild == null) return;

                OptionMapping option = event.getOption("user");
                if (option != null) member = option.getAsMember();

                GuildVoiceState voiceState = member.getVoiceState();
                AudioChannel audioChannel = voiceState.getChannel();

                if (audioChannel != null) {

                    audioManager.openAudioConnection(audioChannel);

                    event.getJDA().getPresence().setActivity(Activity.listening("you in " + audioChannel.getName()));
                    event.getJDA().getPresence().setStatus(OnlineStatus.ONLINE);

                    event.replyEmbeds(joinedToVoiceEmbed).queue();
                } else {
                    event.replyEmbeds(notInVoiceEmbed).queue();
                }
            }),
            new LeaveCommand(event -> {

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
