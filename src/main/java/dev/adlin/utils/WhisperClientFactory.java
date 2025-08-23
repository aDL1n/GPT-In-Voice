package dev.adlin.utils;

import dev.adlin.stt.WhisperClient;

public class WhisperClientFactory {
    private static WhisperClient instance;

    public static WhisperClient getInstance() {
        if (instance == null) {
            instance = new WhisperClient("http://localhost:5000");
        }
        return instance;
    }

    public static void setInstance(WhisperClient client) {
        instance = client;
    }
}
