package dev.adlin.commands;

import dev.adlin.commands.util.DiscordAbstractCommand;
import dev.adlin.manager.VoiceAudioManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;

public class JoinCommand extends DiscordAbstractCommand {

    private final MessageEmbed notInVoiceEmbed = new EmbedBuilder()
            .setTitle("Ты не в войс чате")
            .setColor(Color.RED)
            .build();

    private final MessageEmbed joinedToVoiceEmbed = new EmbedBuilder()
            .setTitle("Успешно подключился в голосовой канал")
            .setColor(Color.GREEN)
            .build();

    public JoinCommand() {
        super("join", "send join request to bot", new OptionData(OptionType.USER, "user", "to user"));
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

            VoiceAudioManager voiceAudioManager = new VoiceAudioManager();

            audioManager.setReceivingHandler(voiceAudioManager);
            audioManager.setSendingHandler(voiceAudioManager);

            audioManager.openAudioConnection(audioChannel);

            commandInteraction.replyEmbeds(joinedToVoiceEmbed).queue();
        } else {
            commandInteraction.replyEmbeds(notInVoiceEmbed).queue();
        }
    }
}
