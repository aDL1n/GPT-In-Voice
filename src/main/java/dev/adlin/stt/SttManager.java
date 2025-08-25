package dev.adlin.stt;

import dev.adlin.stt.util.ISttClient;

public class SttManager {
    private ISttClient currentClient = new Whisper();

    public SttManager() {

    }

    public SttManager setCurrentClient(ISttClient currentClient) {
        this.currentClient = currentClient;
        return this;
    }

    public ISttClient getCurrentClient() {
        return currentClient;
    }
}
