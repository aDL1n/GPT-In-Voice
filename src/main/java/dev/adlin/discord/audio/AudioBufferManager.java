package dev.adlin.discord.audio;

import dev.adlin.discord.listener.AudioBufferListener;
import jakarta.annotation.PreDestroy;
import net.dv8tion.jda.api.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.*;

@Component
public class AudioBufferManager {

    private static final Logger log = LogManager.getLogger(AudioBufferManager.class);

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private final ConcurrentHashMap<User, UserAudioBuffer> userBuffers = new ConcurrentHashMap<>();
    private AudioBufferListener bufferListener;

    private static final double VOLUME_THRESHOLD = 0.017;
    private static final long SILENCE_DELAY_MS = 2000;

    public void processAudioPerUser(User user, byte[] data, double volume) {
        if (user == null || data == null || data.length == 0) return;

        UserAudioBuffer buffer = userBuffers.computeIfAbsent(user, UserAudioBuffer::new);

        synchronized (buffer) {
            if (volume > VOLUME_THRESHOLD) {
                buffer.lastSoundTime = System.currentTimeMillis();
                if (!buffer.recording) buffer.recording = true;

                cancelTask(buffer);
            }

            if (buffer.recording) {
                writeToBuffer(buffer.data, data);

                if (volume <= VOLUME_THRESHOLD && buffer.pendingTask == null)
                    buffer.pendingTask = scheduler.schedule(() -> {
                        if (buffer.recording
                                && System.currentTimeMillis() - buffer.lastSoundTime >= SILENCE_DELAY_MS
                        ) flushBuffer(buffer);
                    }, SILENCE_DELAY_MS, TimeUnit.MILLISECONDS);
            }
        }
    }

    private void writeToBuffer(ByteArrayOutputStream buffer, byte[] data) {
        try {
            buffer.write(data);
        } catch (IOException e) {
            log.error("Failed to add to buffer", e);
        }
    }

    private void cancelTask(UserAudioBuffer buffer) {
        if (buffer.pendingTask != null) {
            buffer.pendingTask.cancel(false);
            buffer.pendingTask = null;
        }
    }

    private void flushBuffer(UserAudioBuffer buffer) {
        if (buffer.data.size() > 0) {
            byte[] audioData = buffer.data.toByteArray();

            scheduler.execute(() -> {
                if (bufferListener != null) {
                    bufferListener.onBufferReady(buffer.user, audioData);
                    log.info("Buffer is ready");
                }
            });

            buffer.data.reset();
        }

        buffer.recording = false;
        cancelTask(buffer);
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down AudioBufferManager");
        scheduler.shutdown();
    }

    public void setBufferListener(AudioBufferListener listener) {
        this.bufferListener = listener;
    }

    private static class UserAudioBuffer {
        final User user;
        final ByteArrayOutputStream data = new ByteArrayOutputStream();
        boolean recording;
        long lastSoundTime;
        ScheduledFuture<?> pendingTask;

        UserAudioBuffer(User user) {
            this.user = user;
        }
    }
}