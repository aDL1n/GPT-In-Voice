package dev.adlin.speech.recognition.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.adlin.config.properties.SpeechRecognitionProperties;
import dev.adlin.speech.recognition.SpeechRecognitionAbstract;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
public class WhisperSpeechRecognition extends SpeechRecognitionAbstract {

    private static final Logger log = LogManager.getLogger(WhisperSpeechRecognition.class);

    private final HttpClient client = HttpClient.newBuilder()
            .build();

    public WhisperSpeechRecognition(SpeechRecognitionProperties properties) {
        super(properties, "whisper");
    }

    @Nullable
    @Override
    public String transcribe(byte[] data) {
        return transcribeAsync(data).join();
    }

    @Override
    public CompletableFuture<String> transcribeAsync(byte[] audio) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.baseUrl + "/stream"))
                .header("Content-Type", "application/octet-stream")
                .POST(HttpRequest.BodyPublishers.ofByteArray(audio))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenCompose(response -> {
                    if (response.statusCode() != 200)
                        throw new RuntimeException("Transcript request failed: " + response.body());

                    JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                    String requestId = json.get("request_id").getAsString();

                    return pollUntilDone(requestId);
                }).exceptionallyAsync(throwable -> {
                    log.error("Transcription failed", throwable);
                    throw new RuntimeException("Transcription service unavailable", throwable);
                });
    }

    private CompletableFuture<String> pollUntilDone(String requestId) {
        HttpRequest pollRequest = HttpRequest.newBuilder()
                .uri(URI.create(this.baseUrl + "/result/" + requestId))
                .GET().build();

        return client.sendAsync(pollRequest, HttpResponse.BodyHandlers.ofString())
                .thenCompose(response -> {
                    if (response.statusCode() != 200)
                        throw new RuntimeException("Polling request failed with status: " + response.statusCode());

                    JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                    String status = json.get("status").getAsString();

                    if ("done".equals(status)) {
                        String text = json.get("text").getAsString();
                        log.info("Transcription completed: {}", text);
                        return CompletableFuture.completedFuture(text);

                    } else return CompletableFuture.supplyAsync(
                            () -> null,
                            CompletableFuture.delayedExecutor(250, TimeUnit.MILLISECONDS)
                    ).thenCompose(ignored -> pollUntilDone(requestId));
                });
    }

}