package dev.adlin.stt.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.adlin.stt.SpeechToText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Whisper implements SpeechToText {

    private static final Logger log = LogManager.getLogger(Whisper.class);

    private final Gson gson = new Gson();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final String serverUrl;

    public Whisper(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public Whisper() {
        this("http://localhost:5000");
    }

    @Nullable
    @Override
    public String transcriptAudio(byte[] data) {
        try {
            String requestId = sendToServer(data);
            if (requestId != null) {
                return waitForResult(requestId).get();
            }
        } catch (Exception e) {
            log.error("Failed to transcript audio", e);
        }
        return null;
    }

    @Nullable
    private String sendToServer(byte[] audio) {
        try {
            URL url = URI.create(serverUrl + "/stream").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/octet-stream");

            try (OutputStream out = conn.getOutputStream()) {
                out.write(audio);
                out.flush();
                log.info("Audio batch sent");
            }

            try (InputStream in = conn.getInputStream()) {
                JsonObject json = gson.fromJson(new InputStreamReader(in), JsonObject.class);
                return json.get("request_id").getAsString();
            }

        } catch (IOException e) {
            log.error("Failed to send audio to server", e);
        }

        return null;
    }

    private CompletableFuture<String> waitForResult(String requestId) {
        CompletableFuture<String> future = new CompletableFuture<>();

        scheduler.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = URI.create(serverUrl + "/result/" + requestId).toURL();
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    try (InputStream in = conn.getInputStream()) {
                        JsonObject json = gson.fromJson(new InputStreamReader(in), JsonObject.class);

                        if ("done".equals(json.get("status").getAsString())) {
                            String text = json.get("text").getAsString();
                            log.info("Translated text: {}", text);
                            future.complete(text);
                            return;
                        }

                        scheduler.schedule(this, 1, TimeUnit.SECONDS);
                    }

                } catch (Exception e) {
                    log.error("Failed to get result from server", e);
                    future.completeExceptionally(e);
                }
            }
        });

        return future;
    }
}
