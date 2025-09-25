package dev.adlin.commands;

import dev.adlin.commands.util.DiscordAbstractCommand;
import dev.adlin.api.states.BotState;
import dev.adlin.api.states.util.BotStatus;
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
            .setTitle("Why did you kick me out")
            .setColor(Color.GREEN)
            .build();

    private final MessageEmbed notInVoice = new EmbedBuilder()
            .setTitle("I'm not in the voice channel")
            .setColor(Color.YELLOW)
            .build();
    private final BotState botState;

    public LeaveCommand(BotState botState) {
        super("leave", "send request to leave from voice channel");
        this.botState = botState;
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

            botState.setStatus(BotStatus.READY);

            commandInteraction.replyEmbeds(leaveSuccesses).queue();
            return;
        }

        commandInteraction.replyEmbeds(notInVoice).queue();
    }
}
