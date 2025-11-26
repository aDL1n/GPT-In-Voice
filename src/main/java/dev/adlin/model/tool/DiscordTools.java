package dev.adlin.model.tool;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.managers.AudioManager;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DiscordTools {

    private final Guild guild;

    public DiscordTools(Guild guild) {
        this.guild = guild;
    }

    @Tool(description = "Список участников в голосовом канале дискорда")
    public String getUsersInVoiceChannel() {
        if (guild.getSelfMember().getVoiceState().getChannel() == null)
            return "Ты не в голосовом канале";

        AudioChannelUnion channel = guild.getSelfMember().getVoiceState().getChannel();

        if (channel.getMembers().size() == 1) return "Ты один в этом голосовом канале";

        return channel.getMembers().stream()
                .filter(member -> member != guild.getSelfMember())
                .map(Member::getEffectiveName)
                .collect(Collectors.joining(", "));
    }

    @Tool(description = "Список голосовых каналов этого дискорд сервера")
    public String getVoiceChannels() {
        List<VoiceChannel> voiceChannels = guild.getVoiceChannels();
        if (voiceChannels.isEmpty()) return "На этом дискорд сервере нет голосовых каналов";

        return guild.getVoiceChannels().stream()
                .map(VoiceChannel::getName)
                .collect(Collectors.joining(", "));
    }

    @Tool(description = "Присоединится к голосовому каналу или перейти в другой")
    public String connectToVoiceChannel(@ToolParam(description = "Название канала из списка доступных") String voiceChannelName) {
        if (!getVoiceChannels().contains(voiceChannelName)) return "Голосового канала с таким названием нет";

        AudioManager audioManager = guild.getAudioManager();

        if (audioManager.isConnected()) audioManager.closeAudioConnection();

        try {
            audioManager.openAudioConnection(
                    guild.getVoiceChannels().stream()
                            .filter(voiceChannel -> voiceChannel.getName().equals(voiceChannelName))
                            .findFirst().orElse(null)
            );
            return "Ты подключился к голосовому каналу " + voiceChannelName;
        } catch (IllegalArgumentException ignored){
            // ¯\_(ツ)_/¯
            return "Голосового канала с таким названием нет";
        }

    }
}
