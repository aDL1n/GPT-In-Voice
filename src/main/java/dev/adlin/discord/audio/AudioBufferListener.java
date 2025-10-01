package dev.adlin.discord.audio;

public interface AudioBufferListener {
    void onBufferReady(String userId, byte[] data);
}
