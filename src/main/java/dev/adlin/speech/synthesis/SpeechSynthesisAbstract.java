package dev.adlin.speech.synthesis;

import dev.adlin.config.SpeechSynthesisConfig;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;

public abstract class SpeechSynthesisAbstract implements SpeechSynthesis {

    protected final String baseUrl;

    public SpeechSynthesisAbstract(SpeechSynthesisConfig config) {
        baseUrl = config.getUrl();
    }

    @Override
    public abstract byte[] speech(String text);

    @Override
    public abstract CompletableFuture<byte[]> speechAsync(String text);
}
