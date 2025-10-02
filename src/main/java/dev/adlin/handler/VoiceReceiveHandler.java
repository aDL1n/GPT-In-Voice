package dev.adlin.handler;

import dev.adlin.discord.audio.VoiceBufferManager;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.UserAudio;
import org.jetbrains.annotations.NotNull;

public class VoiceReceiveHandler implements AudioReceiveHandler {

    private final VoiceBufferManager bufferManager;

    public VoiceReceiveHandler(VoiceBufferManager bufferManager) {
        this.bufferManager = bufferManager;
    }

    @Override
    public void handleUserAudio(@NotNull UserAudio userAudio) {

        byte[] data = userAudio.getAudioData(1.0f);
        double volume = computeVolume(data);

        bufferManager.processAudioPerUser(userAudio.getUser().getId(), data, volume);
    }

    @Override
    public boolean canReceiveUser() {
        return true;
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
