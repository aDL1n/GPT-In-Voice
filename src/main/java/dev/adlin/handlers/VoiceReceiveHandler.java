package dev.adlin.handlers;

import dev.adlin.stt.WhisperClient;
import dev.adlin.utils.WhisperClientFactory;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class VoiceReceiveHandler implements AudioReceiveHandler {

    private final Logger LOGGER = Logger.getLogger(VoiceReceiveHandler.class.getName());

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final ByteArrayOutputStream audioBuffer = new ByteArrayOutputStream();
    private final WhisperClient whisperClient;

    private ScheduledFuture<?> pendingTask = null;
    private boolean isRecording = false;
    private long lastSoundTime = 0;

    private final Object lock = new Object();

    private static final double VOLUME_THRESHOLD = 0.017;
    private static final long SILENCE_DELAY_MS = 2000;

    public VoiceReceiveHandler() {
        whisperClient = WhisperClientFactory.getInstance();
    }

    @Override
    public boolean canReceiveCombined() {
        return true;
    }

    @Override
    public void handleCombinedAudio(@NotNull CombinedAudio combinedAudio) {
        if (combinedAudio.getUsers().isEmpty()) return;

        byte[] data = combinedAudio.getAudioData(1.0f);
        double volume = computeVolume(data);

        synchronized (lock) {
            if (volume > VOLUME_THRESHOLD) {
                lastSoundTime = System.currentTimeMillis();

                if (!isRecording) isRecording = true;

                cancelPendingTask();
            }

            if (isRecording) {
                writeToBuffer(data);

                if (volume <= VOLUME_THRESHOLD && pendingTask == null)
                    scheduleSendTask();

            }
        }
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

    private void writeToBuffer(byte[] data) {
        try {
            audioBuffer.write(data);
        } catch (IOException e) {
            LOGGER.throwing(VoiceReceiveHandler.class.getName(), "writeToBuffer", e);
        }
    }

    private void cancelPendingTask() {
        if (pendingTask != null) {
            pendingTask.cancel(false);
            pendingTask = null;
        }
    }

    private void scheduleSendTask() {
        pendingTask = scheduler.schedule(() -> {
            synchronized (lock) {
                if (isRecording && System.currentTimeMillis() - lastSoundTime >= SILENCE_DELAY_MS) {
                    flushBuffer();
                }
            }
        }, SILENCE_DELAY_MS, TimeUnit.MILLISECONDS);
    }

    private void flushBuffer() {
        if (audioBuffer.size() > 0) {
            byte[] audioData = audioBuffer.toByteArray();
            whisperClient.sendBatch(audioData);
            audioBuffer.reset();
        }
        isRecording = false;
        cancelPendingTask();
    }

    public void shutdown() {
        synchronized (lock) {
            flushBuffer();
        }

        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS))
                scheduler.shutdownNow();
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}