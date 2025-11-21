package dev.adlin.speech.recognition;

import java.util.concurrent.CompletableFuture;

public interface SpeechRecognition {
    String transcribe(byte[] data);
    CompletableFuture<String> transcribeAsync(byte[] audio);
    String getName();
}
