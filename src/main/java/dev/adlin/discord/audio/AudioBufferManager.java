package dev.adlin.discord.audio;

import dev.adlin.discord.VoiceUserState;
import dev.adlin.discord.listener.AudioBufferListener;
import jakarta.annotation.PreDestroy;
import net.dv8tion.jda.api.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class AudioBufferManager {

    private static final Logger log = LogManager.getLogger(AudioBufferManager.class);

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(
            Math.max(2, Runtime.getRuntime().availableProcessors() / 2));
    private final ConcurrentHashMap<User, VoiceUserState> users = new ConcurrentHashMap<>();

    private AudioBufferListener bufferListener;

    private static final double VOLUME_THRESHOLD = 0.017;
    private static final long SILENCE_DELAY_MS = 2000;


    public AudioBufferManager() {}

    public void processAudioPerUser(User user, byte[] data, double volume) {
        if (user == null || data == null || data.length == 0) return;

        VoiceUserState voiceUserState = users.computeIfAbsent(user, VoiceUserState::new);

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
                    bufferListener.onBufferReady(voiceUserState.getUser(), audioData);
                    log.info("Buffer is ready");
                }
            });

            voiceUserState.getBuffer().reset();
        }

        voiceUserState.setRecording(false);
        cancelPendingTask(voiceUserState);
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down AudioBufferManager");
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