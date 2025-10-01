package dev.adlin.discord.audio;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.*;

public class VoiceBufferManager {

    private static final Logger log = LogManager.getLogger(VoiceBufferManager.class);

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(
            Math.max(2, Runtime.getRuntime().availableProcessors() / 2));
    private final ConcurrentHashMap<String, VoiceUserState> users = new ConcurrentHashMap<>();

    private AudioBufferListener bufferListener;

    private static final double VOLUME_THRESHOLD = 0.017;
    private static final long SILENCE_DELAY_MS = 2000;


    public VoiceBufferManager() {

    }

    public void processAudioPerUser(String userId, byte[] data, double volume) {
        if (userId == null || data == null || data.length == 0) return;

        VoiceUserState voiceUserState = users.computeIfAbsent(userId, VoiceUserState::new);

        synchronized (voiceUserState.getLock()) {
            if (volume > VOLUME_THRESHOLD) {
                voiceUserState.setLastSoundTime(System.currentTimeMillis());
                if (!voiceUserState.isRecording()) voiceUserState.setRecording(true);
                cancelPendingTask(voiceUserState);
            }

            if (voiceUserState.isRecording()) {
                writeToBuffer(voiceUserState.getBuffer(), data);

                if (volume <= VOLUME_THRESHOLD && voiceUserState.getPendingTask() == null)
                    scheduleSendTask(voiceUserState);
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

    private void cancelPendingTask(VoiceUserState voiceUserState) {
        if (voiceUserState.getPendingTask() != null) {
            voiceUserState.getPendingTask().cancel(false);
            voiceUserState.setPendingTask(null);
        }
    }

    private void scheduleSendTask(VoiceUserState voiceUserState) {
        voiceUserState.setPendingTask(
                scheduler.schedule(() -> {
                    synchronized (voiceUserState.getLock()) {
                        if (voiceUserState.isRecording() && System.currentTimeMillis() - voiceUserState.getLastSoundTime() >= SILENCE_DELAY_MS) {
                            flushBuffer(voiceUserState);
                        }
                    }
                }, SILENCE_DELAY_MS, TimeUnit.MILLISECONDS)
        );
    }

    private void flushBuffer(VoiceUserState voiceUserState) {
        if (voiceUserState.getBuffer().size() > 0) {
            byte[] audioData = voiceUserState.getBuffer().toByteArray();

            scheduler.execute(() -> {
                if (bufferListener != null) {
                    bufferListener.onBufferReady(voiceUserState.getId(), audioData);
                    log.info("Buffer is ready");
                }
            });

            voiceUserState.getBuffer().reset();
        }

        voiceUserState.setRecording(false);
        cancelPendingTask(voiceUserState);
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
