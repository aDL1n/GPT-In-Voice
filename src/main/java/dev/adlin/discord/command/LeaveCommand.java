package dev.adlin.discord.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.util.function.Consumer;

public class LeaveCommand extends DiscordAbstractCommand {

    private final Consumer<Member> onKick;

    final MessageEmbed notInVoiceEmbed = new EmbedBuilder()
            .setTitle("You not in voice chat")
            .setColor(Color.RED)
            .build();

    final MessageEmbed leaveFromVoiceEmbed = new EmbedBuilder()
            .setTitle("Successfully diconnected from voice channel")
            .setColor(Color.GREEN)
            .build();


    public LeaveCommand(Consumer<Member> onKick) {
        super("leave", "send request to leave from voice channel");
        this.onKick = onKick;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();

        if (member == null) return;

        Guild guild = event.getGuild();
        if (guild == null) return;

        OptionMapping option = event.getOption("user");
        if (option != null) member = option.getAsMember();

        AudioManager audioManager = guild.getAudioManager();
        AudioChannel memberAudioChannel = member.getVoiceState().getChannel();

        if (memberAudioChannel != null) {

            audioManager.closeAudioConnection();

            event.getJDA().getPresence().setActivity(Activity.customStatus("Waiting for you"));
            event.getJDA().getPresence().setStatus(OnlineStatus.IDLE);

            onKick.accept(member);

            event.replyEmbeds(leaveFromVoiceEmbed).queue();
        } else {
            event.replyEmbeds(notInVoiceEmbed).queue();
        }
    }
}
