package dev.adlin.manager;

import dev.adlin.utils.AudioBufferListener;
import dev.adlin.utils.UserState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.*;

public class VoiceBufferManager {

    private static final Logger log = LogManager.getLogger(VoiceBufferManager.class);

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(
            Math.max(2, Runtime.getRuntime().availableProcessors() / 2));
    private final ConcurrentHashMap<String, UserState> users = new ConcurrentHashMap<>();

    private AudioBufferListener bufferListener;

    private static final double VOLUME_THRESHOLD = 0.017;
    private static final long SILENCE_DELAY_MS = 2000;


    public VoiceBufferManager() {

    }

    public void processAudioPerUser(String userId, byte[] data, double volume) {
        if (userId == null || data == null || data.length == 0) return;

        UserState userState = users.computeIfAbsent(userId, UserState::new);

        synchronized (userState.getLock()) {
            if (volume > VOLUME_THRESHOLD) {
                userState.setLastSoundTime(System.currentTimeMillis());
                if (!userState.isRecording()) userState.setRecording(true);
                cancelPendingTask(userState);
            }

            if (userState.isRecording()) {
                writeToBuffer(userState.getBuffer(), data);

                if (volume <= VOLUME_THRESHOLD && userState.getPendingTask() == null)
                    scheduleSendTask(userState);
            }
        }
    }

    private void writeToBuffer(ByteArrayOutputStream buffer, byte[] data) {
        try {
            buffer.write(data);
        } catch (IOException e) {
            log.error("Failed to write to buffer", e);
        }
    }

    private void cancelPendingTask(UserState userState) {
        if (userState.getPendingTask() != null) {
            userState.getPendingTask().cancel(false);
            userState.setPendingTask(null);
        }
    }

    private void scheduleSendTask(UserState userState) {
        userState.setPendingTask(
                scheduler.schedule(() -> {
                    synchronized (userState.getLock()) {
                        if (userState.isRecording() && System.currentTimeMillis() - userState.getLastSoundTime() >= SILENCE_DELAY_MS) {
                            flushBuffer(userState);
                        }
                    }
                }, SILENCE_DELAY_MS, TimeUnit.MILLISECONDS)
        );
    }

    private void flushBuffer(UserState userState) {
        if (userState.getBuffer().size() > 0) {
            byte[] audioData = userState.getBuffer().toByteArray();

            scheduler.execute(() -> {
                if (bufferListener != null) {
                    bufferListener.onBufferReady(userState.getId(), audioData);
                    log.info("Buffer is ready");
                }
            });

            userState.getBuffer().reset();
        }

        userState.setRecording(false);
        cancelPendingTask(userState);
    }

    public void shutdown() {
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
