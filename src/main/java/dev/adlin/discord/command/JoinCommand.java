package dev.adlin.discord.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;
import org.springframework.ai.chat.messages.SystemMessage;

import java.awt.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class JoinCommand extends DiscordAbstractCommand {

    private final Consumer<Member> onJoin;

    final MessageEmbed notInVoiceEmbed = new EmbedBuilder()
            .setTitle("You not in voice chat")
            .setColor(Color.RED)
            .build();

    final MessageEmbed joinedToVoiceEmbed = new EmbedBuilder()
            .setTitle("Successfully connected to the voice channel")
            .setColor(Color.GREEN)
            .build();

    public JoinCommand(Consumer<Member> onJoin) {
        super("join", "send join request to bot", new OptionData(OptionType.USER, "user", "to user"));
        this.onJoin = onJoin;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();

        Guild guild = event.getGuild();
        if (guild == null) return;

        OptionMapping option = event.getOption("user");
        if (option != null) member = option.getAsMember();

        AudioChannel memberAudioChannel = member.getVoiceState().getChannel();
        AudioManager audioManager = guild.getAudioManager();

        if (memberAudioChannel != null) {

            audioManager.openAudioConnection(memberAudioChannel);

            event.getJDA().getPresence().setActivity(Activity.listening("you in " + memberAudioChannel.getName()));
            event.getJDA().getPresence().setStatus(OnlineStatus.ONLINE);

            SystemMessage systemMessage = new SystemMessage(member.getEffectiveName() + " пригласил тебя к себе в войс-чат");
            this.onJoin.accept(null);

            event.replyEmbeds(joinedToVoiceEmbed).timeout(12, TimeUnit.SECONDS).queue();
        } else {
            event.replyEmbeds(notInVoiceEmbed).queue();
        }
    }
}
