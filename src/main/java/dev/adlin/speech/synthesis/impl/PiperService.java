package dev.adlin.speech.synthesis.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.adlin.config.properties.SpeechSynthesisConfig;
import dev.adlin.speech.synthesis.SpeechSynthesisAbstract;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class PiperService extends SpeechSynthesisAbstract {

    private static final Logger log = LogManager.getLogger(PiperService.class);

    private final HttpClient client = HttpClient
            .newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();

    public PiperService(SpeechSynthesisConfig config) {
        super(config, "piper");
    }

    @Nullable
    @Override
    public byte[] speech(String text) {
        try {
            log.info("The answer has been successfully translated into speech");
            return speechAsync(text).get();
        } catch (Exception e) {
            log.error("Failed to convert text to speech", e);
        }

        return null;
    }

    public CompletableFuture<byte[]> speechAsync(String text) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.baseUrl + "/speech"))
                .header("Content-Type", "text/plain; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(text, StandardCharsets.UTF_8))
                .build();


        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenCompose(resp -> {
                    if (resp.statusCode() != 200) {
                        throw new RuntimeException("Speech request failed: " + resp.body());
                    }
                    JsonObject json = JsonParser.parseString(resp.body()).getAsJsonObject();
                    String requestId = json.get("request_id").getAsString();

                    return pollUntilReady(requestId);
                });
    }

    private CompletableFuture<byte[]> pollUntilReady(String requestId) {
        HttpRequest pollReq = HttpRequest.newBuilder()
                .uri(URI.create(this.baseUrl + "/result/" + requestId))
                .GET()
                .build();

        return client.sendAsync(pollReq, HttpResponse.BodyHandlers.ofByteArray())
                .thenCompose(resp -> {
                    String contentType = resp.headers()
                            .firstValue("content-type").orElse("");

                    if (contentType.startsWith("audio/")) {
                        return CompletableFuture.completedFuture(resp.body());
                    } else {
                        return CompletableFuture.supplyAsync(
                                () -> null,
                                CompletableFuture.delayedExecutor(500, TimeUnit.MILLISECONDS)
                        ).thenCompose(x -> pollUntilReady(requestId));
                    }
                });
    }
}
