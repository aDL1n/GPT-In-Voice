package dev.adlin.manager;

import dev.adlin.Bot;
import dev.adlin.utils.AudioBufferListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.*;

public class VoiceBufferManager {

    private static final Logger LOGGER = LogManager.getLogger(VoiceBufferManager.class);

    private AudioBufferListener bufferListener;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final ByteArrayOutputStream audioBuffer = new ByteArrayOutputStream();

    private final Object lock = new Object();

    private static final double VOLUME_THRESHOLD = 0.017;
    private static final long SILENCE_DELAY_MS = 2000;

    private boolean isRecording = false;
    private long lastSoundTime = 0;
    private ScheduledFuture<?> pendingTask;


    public void processAudio(byte[] data, double volume) {
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

    private void writeToBuffer(byte[] data) {
        try {
            audioBuffer.write(data);
        } catch (IOException e) {
            LOGGER.error("Failed to write to buffer", e);
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

            scheduler.execute(() -> {
                if (bufferListener != null) {
                    bufferListener.onBufferReady(audioData);
                    LOGGER.info("Buffer has ready");
                }
            });

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

    public void setBufferListener(AudioBufferListener listener) {
        this.bufferListener = listener;
    }
}
