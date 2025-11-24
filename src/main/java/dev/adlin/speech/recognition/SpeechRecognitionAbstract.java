package dev.adlin.speech.recognition;

import dev.adlin.config.properties.SpeechRecognitionProperties;

import java.util.concurrent.CompletableFuture;


public abstract class SpeechRecognitionAbstract implements SpeechRecognition {

    protected final String baseUrl;
    private final String name;

    public SpeechRecognitionAbstract(SpeechRecognitionProperties properties, String name) {
        baseUrl = properties.getUrl();
        this.name = name.toLowerCase();
    }

    @Override
    public abstract String transcribe(byte[] data);

    @Override
    public abstract CompletableFuture<String> transcribeAsync(byte[] audio);

    @Override
    public String getName() {
        return this.name;
    }
}
