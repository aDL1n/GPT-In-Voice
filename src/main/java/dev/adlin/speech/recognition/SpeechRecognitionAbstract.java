package dev.adlin.speech.recognition;

import dev.adlin.config.SpeechRecognitionConfig;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;


public abstract class SpeechRecognitionAbstract implements SpeechRecognition {

    protected final String baseUrl;

    public SpeechRecognitionAbstract(SpeechRecognitionConfig config) {
        baseUrl = config.getUrl();
    }

    @Override
    public abstract String transcriptAudio(byte[] data);

    @Override
    public abstract CompletableFuture<String> transcriptAudioAsync(byte[] audio);
}
