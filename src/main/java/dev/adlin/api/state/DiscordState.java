package dev.adlin.api.state;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DiscordState {

    private AudioManager audioManager;
    private boolean ready = false;

    public DiscordState() {}

    public boolean isInVoiceChannel() {
        return this.audioManager.isConnected();
    }

    @Nullable
    public Optional<String> getVoiceChannelName() {
        if (this.audioManager.isConnected()) {
            return Optional.of(this.audioManager.getConnectedChannel().getName());
        }

        return Optional.empty();
    }

    @Nullable
    public Optional<List<String>> getVoiceUserNameList() {
        if (this.audioManager.isConnected()) {
            return Optional.ofNullable(audioManager.getConnectedChannel().asVoiceChannel().getMembers()
                    .stream()
                    .filter(member -> member.getUser().isBot())
                    .map(Member::getNickname)
                    .toList()
            );
        }

        return Optional.empty();
    }

    @Nullable
    public AudioManager getAudioManager() {
        return audioManager;
    }

    public void setAudioManager(AudioManager audioManager) {
        this.audioManager = audioManager;
        this.ready = true;
    }

    public boolean isReady() {
        return ready;
    }
}
