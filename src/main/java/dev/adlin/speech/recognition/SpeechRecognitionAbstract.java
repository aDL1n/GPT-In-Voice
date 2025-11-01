package dev.adlin.speech.recognition;

import dev.adlin.config.SpeechRecognitionConfig;

import java.util.concurrent.CompletableFuture;


public abstract class SpeechRecognitionAbstract implements SpeechRecognition {

    protected final String baseUrl;
    private final String name;

    public SpeechRecognitionAbstract(SpeechRecognitionConfig config, String name) {
        baseUrl = config.getUrl();
        this.name = name.toLowerCase();
    }

    @Override
    public abstract String transcriptAudio(byte[] data);

    @Override
    public abstract CompletableFuture<String> transcriptAudioAsync(byte[] audio);

    @Override
    public String getName() {
        return this.name;
    }
}
