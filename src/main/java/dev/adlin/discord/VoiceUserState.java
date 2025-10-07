package dev.adlin.discord;

import net.dv8tion.jda.api.entities.User;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ScheduledFuture;

public class VoiceUserState {
    private final User user;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    private volatile boolean recording;
    private volatile long lastSoundTime;
    private volatile ScheduledFuture<?> pendingTask;

    private final Object lock = new Object();

    public VoiceUserState(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public ByteArrayOutputStream getBuffer() {
        return buffer;
    }

    public Object getLock() {
        return lock;
    }

    public boolean isRecording() {
        return recording;
    }

    public void setRecording(boolean recording) {
        this.recording = recording;
    }

    public long getLastSoundTime() {
        return lastSoundTime;
    }

    public void setLastSoundTime(long lastSoundTime) {
        this.lastSoundTime = lastSoundTime;
    }

    public ScheduledFuture<?> getPendingTask() {
        return pendingTask;
    }

    public void setPendingTask(ScheduledFuture<?> pendingTask) {
        this.pendingTask = pendingTask;
    }
}
