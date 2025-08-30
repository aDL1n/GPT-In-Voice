package dev.adlin.tts.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.adlin.tts.TextToSpeech;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Piper implements TextToSpeech {

    private final HttpClient client = HttpClient
            .newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();
    private final String baseUrl = "http://localhost:5002";

    @Override
    public byte[] speech(String text) {
        try {
            return speechAsync(text).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<byte[]> speechAsync(String text) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/speech"))
                .header("Content-Type", "text/plain; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(text, StandardCharsets.UTF_8))
                .build();


        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenCompose(resp -> {
                    if (resp.statusCode() != 200) {
                        throw new RuntimeException("speech request failed: " + resp.body());
                    }
                    JsonObject json = JsonParser.parseString(resp.body()).getAsJsonObject();
                    String requestId = json.get("request_id").getAsString();

                    return pollUntilReady(requestId);
                });
    }

    private CompletableFuture<byte[]> pollUntilReady(String requestId) {
        HttpRequest pollReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/result/" + requestId))
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