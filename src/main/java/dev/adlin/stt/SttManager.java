package dev.adlin.stt;

import dev.adlin.stt.impl.Whisper;

public class SttManager {
    private ISpeechToText currentClient = new Whisper();

    public SttManager() {

    }

    public SttManager setCurrentClient(ISpeechToText currentClient) {
        this.currentClient = currentClient;
        return this;
    }

    public ISpeechToText getCurrentClient() {
        return currentClient;
    }
}
