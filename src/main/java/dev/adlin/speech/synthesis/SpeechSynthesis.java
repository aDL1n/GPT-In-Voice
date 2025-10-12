package dev.adlin.speech.synthesis;

import java.util.concurrent.CompletableFuture;

public interface SpeechSynthesis {
    byte[] speech(String text);
    CompletableFuture<byte[]> speechAsync(String text);
    String getName();
}
