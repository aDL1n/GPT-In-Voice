package dev.adlin.stt.impl;

import com.google.gson.JsonObject;
import dev.adlin.stt.SpeechToText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonParser;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Whisper implements SpeechToText {

    private static final Logger log = LogManager.getLogger(Whisper.class);

    private final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();

    private final String baseUrl;

    public Whisper(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Whisper() {
        this("http://localhost:5000");
    }

    @Nullable
    @Override
    public String transcriptAudio(byte[] data) {
        try {
            log.info("Sending audio for transcription");
            return transcriptAudioAsync(data).get();
        } catch (Exception e) {
            log.error("Failed to transcript audio", e);
            return null;
        }
    }

    public CompletableFuture<String> transcriptAudioAsync(byte[] audio) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/stream"))
                .header("Content-Type", "application/octet-stream")
                .POST(HttpRequest.BodyPublishers.ofByteArray(audio))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenCompose(response -> {
                    if (response.statusCode() != 200) {
                        throw new RuntimeException("sendToServer request failed: " + response.body());
                    }
                    JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                    String requestId = json.get("request_id").getAsString();
                    log.info("Audio batch sent, received request_id: {}", requestId);
                    return pollUntilDone(requestId);
                });
    }

    private CompletableFuture<String> pollUntilDone(String requestId) {
        HttpRequest pollRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/result/" + requestId))
                .GET()
                .build();

        return client.sendAsync(pollRequest, HttpResponse.BodyHandlers.ofString())
                .thenCompose(response -> {
                    if (response.statusCode() != 200) {
                        throw new RuntimeException("polling request failed with status: " + response.statusCode());
                    }

                    JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                    String status = json.get("status").getAsString();

                    if ("done".equals(status)) {
                        String text = json.get("text").getAsString();
                        log.info("Transcription completed: {}", text);
                        return CompletableFuture.completedFuture(text);
                    } else {
                        return CompletableFuture.supplyAsync(
                                () -> null,
                                CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS)
                        ).thenCompose(ignored -> pollUntilDone(requestId));
                    }
                });
    }
}

