package dev.adlin.speech.synthesis;

import dev.adlin.config.SpeechSynthesisConfig;

import java.util.concurrent.CompletableFuture;

public abstract class SpeechSynthesisAbstract implements SpeechSynthesis {

    protected final String baseUrl;
    private final String name;

    public SpeechSynthesisAbstract(SpeechSynthesisConfig config, String name) {
        baseUrl = config.getUrl();
        this.name = name.toLowerCase();
    }

    @Override
    public abstract byte[] speech(String text);

    @Override
    public abstract CompletableFuture<byte[]> speechAsync(String text);

    @Override
    public String getName() {
        return this.name;
    }
}
