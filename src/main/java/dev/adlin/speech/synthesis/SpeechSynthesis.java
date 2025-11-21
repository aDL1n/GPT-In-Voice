package dev.adlin.speech.synthesis;

import java.util.concurrent.CompletableFuture;

public interface SpeechSynthesis {
    byte[] synthesize(String text);
    CompletableFuture<byte[]> synthesizeAsync(String text);
    String getName();
}
