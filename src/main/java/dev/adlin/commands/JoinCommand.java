package dev.adlin.commands;

import dev.adlin.commands.util.DiscordAbstractCommand;
import dev.adlin.utils.BotState;
import dev.adlin.utils.BotStatus;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;

public class JoinCommand extends DiscordAbstractCommand {

    private final MessageEmbed notInVoiceEmbed = new EmbedBuilder()
            .setTitle("You not in voice chat")
            .setColor(Color.RED)
            .build();

    private final MessageEmbed joinedToVoiceEmbed = new EmbedBuilder()
            .setTitle("Successfully connected to the voice channel")
            .setColor(Color.GREEN)
            .build();

    private final BotState botState;

    public JoinCommand(BotState botState) {
        super("join", "send join request to bot", new OptionData(OptionType.USER, "user", "to user"));
        this.botState = botState;
    }

    @Override
    public void execute(SlashCommandInteractionEvent commandInteraction) {
        Member member = commandInteraction.getMember();

        Guild guild = commandInteraction.getGuild();
        if (guild == null) return;

        OptionMapping option = commandInteraction.getOption("user");
        if (option != null) member = option.getAsMember();

        GuildVoiceState voiceState = member.getVoiceState();
        AudioChannel audioChannel = voiceState.getChannel();

        if (audioChannel != null) {
            AudioManager audioManager = guild.getAudioManager();

            audioManager.openAudioConnection(audioChannel);

            commandInteraction.getJDA().getPresence().setActivity(Activity.listening("you in " + audioChannel.getName()));
            commandInteraction.getJDA().getPresence().setStatus(OnlineStatus.ONLINE);

            botState.setStatus(BotStatus.IN_VOICE);

            commandInteraction.replyEmbeds(joinedToVoiceEmbed).queue();
        } else {
            commandInteraction.replyEmbeds(notInVoiceEmbed).queue();
        }
    }
}
