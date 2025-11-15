package dev.adlin.discord.listener;

import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class VoiceListener extends ListenerAdapter {
    private Consumer<GuildVoiceUpdateEvent> onUserLeave;
    private Consumer<GuildVoiceUpdateEvent> onUserJoin;

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        if (onUserLeave == null && onUserJoin == null) return;

        if (event.getGuild().getAudioManager() == null &&
                !event.getGuild().getAudioManager().isConnected()) return;

        AudioChannelUnion connectedChannel = event.getGuild().getAudioManager().getConnectedChannel();

        if (event.getChannelJoined() != null &&
                event.getChannelJoined().getId().equals(connectedChannel.getId())) {
            if (onUserJoin != null) onUserJoin.accept(event);
            return;
        }

        if (event.getChannelLeft() != null &&
                event.getChannelLeft().getId().equals(connectedChannel.getId())) {
            if (onUserLeave != null) onUserLeave.accept(event);
            return;
        }

    }

    public void OnUserLeave(Consumer<GuildVoiceUpdateEvent> event) {
        this.onUserLeave = event;
    }

    public void OnUserJoin(Consumer<GuildVoiceUpdateEvent> event) {
        this.onUserJoin = event;
    }
}
