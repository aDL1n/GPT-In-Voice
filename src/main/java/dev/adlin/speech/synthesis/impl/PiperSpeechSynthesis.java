package dev.adlin.speech.synthesis.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.adlin.config.properties.SpeechSynthesisProperties;
import dev.adlin.speech.synthesis.SpeechSynthesisAbstract;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
public class PiperSpeechSynthesis extends SpeechSynthesisAbstract {

    private static final Logger log = LogManager.getLogger(PiperSpeechSynthesis.class);

    private final HttpClient client = HttpClient
            .newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();

    public PiperSpeechSynthesis(SpeechSynthesisProperties properties) {
        super(properties, "piper");
    }

    @Nullable
    @Override
    public byte[] synthesize(String text) {
        return this.synthesizeAsync(text).join();
    }

    public CompletableFuture<byte[]> synthesizeAsync(String text) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.baseUrl + "/speech"))
                .header("Content-Type", "text/plain; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(text, StandardCharsets.UTF_8))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenCompose(response -> {
                    if (response.statusCode() != 200)
                        throw new RuntimeException("Speech request failed: " + response.body());

                    JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                    return pollUntilDone(json.get("request_id").getAsString());
                }).exceptionallyAsync(throwable -> {
                    log.error("Text speech failed", throwable);
                    throw new RuntimeException("Speech   service unavailable", throwable);
                });
    }

    private CompletableFuture<byte[]> pollUntilDone(String requestId) {
        HttpRequest pollReq = HttpRequest.newBuilder()
                .uri(URI.create(this.baseUrl + "/result/" + requestId))
                .GET().build();

        return client.sendAsync(pollReq, HttpResponse.BodyHandlers.ofByteArray())
                .thenCompose(response -> {
                    String contentType = response.headers()
                            .firstValue("content-type").orElse("");

                    if (contentType.startsWith("audio/")) {
                        log.info("Text to speech completed");
                        return CompletableFuture.completedFuture(response.body());
                    }
                    else return CompletableFuture.supplyAsync(
                            () -> null,
                            CompletableFuture.delayedExecutor(250, TimeUnit.MILLISECONDS)
                    ).thenCompose(future -> pollUntilDone(requestId));
                });
    }
}
