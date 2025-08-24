package dev.adlin.stt;

import dev.adlin.stt.util.ISttClient;

public class SttManager {
    private ISttClient currentClient = new Whisper();

    public SttManager() {

    }

    public void setCurrentClient(ISttClient currentClient) {
        this.currentClient = currentClient;
    }

    public ISttClient getCurrentClient() {
        return currentClient;
    }
}
