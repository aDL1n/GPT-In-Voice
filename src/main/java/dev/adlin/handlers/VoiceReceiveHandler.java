package dev.adlin.handlers;

import dev.adlin.manager.VoiceBufferManager;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public class VoiceReceiveHandler implements AudioReceiveHandler {

    private final VoiceBufferManager bufferManager;

    public VoiceReceiveHandler(VoiceBufferManager bufferManager) {
        this.bufferManager = bufferManager;
    }

    @Override
    public boolean canReceiveCombined() {
        return true;
    }

    @Override
    public boolean includeUserInCombinedAudio(@NotNull User user) {
        return !user.isBot();
    }

    @Override
    public void handleCombinedAudio(@NotNull CombinedAudio combinedAudio) {
        if (combinedAudio.getUsers().isEmpty()) return;

        byte[] data = combinedAudio.getAudioData(1.0f);
        double volume = computeVolume(data);

        bufferManager.processAudio(data, volume);
    }

    private double computeVolume(byte[] audioData) {
        long sum = 0;
        for (int i = 0; i < audioData.length; i += 2) {
            int sample = (audioData[i] << 8) | (audioData[i + 1] & 0xFF);
            sum += Math.abs(sample);
        }
        double average = sum / (audioData.length / 2.0);
        return average / 32768.0;
    }
}
