package dev.adlin.utils;

public interface AudioBufferListener {
    void onBufferReady(String userId, byte[] data);
}
