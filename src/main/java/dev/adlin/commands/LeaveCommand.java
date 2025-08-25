package dev.adlin.commands;

import dev.adlin.commands.util.DiscordAbstractCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;

public class LeaveCommand extends DiscordAbstractCommand {

    private final MessageEmbed leaveSuccesses = new EmbedBuilder()
            .setTitle("Та за шо ты меня выгнал")
            .setColor(Color.GREEN)
            .build();

    private final MessageEmbed notInVoice = new EmbedBuilder()
            .setTitle("Да от куда мне выходить по твоему")
            .setColor(Color.YELLOW)
            .build();

    public LeaveCommand() {
        super("leave", "send request to leave from voice channel");
    }

    @Override
    public void execute(SlashCommandInteractionEvent commandInteraction) {
        Guild guild = commandInteraction.getGuild();
        if (guild == null) return;

        AudioManager audioManager = guild.getAudioManager();

        if (audioManager.getConnectionStatus() == ConnectionStatus.CONNECTED){
            audioManager.closeAudioConnection();

            JDA jda = commandInteraction.getJDA();
            jda.getPresence().setActivity(Activity.customStatus("Waiting for you"));
            jda.getPresence().setStatus(OnlineStatus.IDLE);

            commandInteraction.replyEmbeds(leaveSuccesses).queue();
            return;
        }

        commandInteraction.replyEmbeds(notInVoice).queue();
    }
}
