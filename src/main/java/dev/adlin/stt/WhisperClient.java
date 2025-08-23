package dev.adlin.stt;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class WhisperClient {

    private final Logger LOGGER = Logger.getLogger(WhisperClient.class.getName());

    private final Gson gson = new Gson();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final String serverURL;

    public WhisperClient(String url) {
        this.serverURL = url;
    }

    public void sendBatch(byte[] audio) {
        executor.submit(() -> {
            LOGGER.info("Sending audio batch");
            String requestId = sendToServer(audio);
            if (requestId != null) {
                pollForResult(requestId);
            }
        });
    }

    private String sendToServer(byte[] audio) {
        try {
            URL url = URI.create(serverURL + "/stream").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/octet-stream");

            try (OutputStream out = conn.getOutputStream()) {
                out.write(audio);
                out.flush();
            }

            LOGGER.info("Audio batch sent");
            try (InputStream in = conn.getInputStream()) {
                JsonObject json = gson.fromJson(new InputStreamReader(in), JsonObject.class);
                return json.get("request_id").getAsString();
            }
        } catch (IOException e) {
            LOGGER.throwing(WhisperClient.class.getName(), "sendToServer", e);
            return null;
        }
    }

    private void pollForResult(String requestId) {
        try {
            while (true) {
                Thread.sleep(1000);
                URL url = URI.create(serverURL + "/result/" + requestId).toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                try (InputStream in = conn.getInputStream()) {
                    JsonObject json = gson.fromJson(new InputStreamReader(in), JsonObject.class);

                    if (json.get("status").getAsString().equals("done")) {
                        String text = json.get("text").getAsString();
                        System.out.println("Распознанный текст: " + text);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.throwing(WhisperClient.class.getName(), "pollForResult", e);
            System.err.println(e.getMessage());
        }
    }
}
