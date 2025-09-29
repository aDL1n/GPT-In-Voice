package dev.adlin.api.states;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DiscordState {

    private final AudioManager audioManager;

    public DiscordState(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    public boolean isInVoiceChannel() {
        return this.audioManager.isConnected();
    }

    @Nullable
    public String getVoiceChannelName() {
        if (this.audioManager.isConnected()) {
            return this.audioManager.getConnectedChannel().getName();
        }

        return null;
    }

    @Nullable
    public List<String> getVoiceUserNameList() {
        if (this.audioManager.isConnected()) {
            audioManager.getConnectedChannel().asVoiceChannel().getMembers()
                    .stream()
                    .map(Member::getNickname)
                    .toList()
            ;
        }

        return null;
    }
}
