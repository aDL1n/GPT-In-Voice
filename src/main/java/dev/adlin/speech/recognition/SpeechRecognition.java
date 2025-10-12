package dev.adlin.speech.recognition;

import java.util.concurrent.CompletableFuture;

public interface SpeechRecognition {
    String transcriptAudio(byte[] data);
    CompletableFuture<String> transcriptAudioAsync(byte[] audio);
    String getName();

}
