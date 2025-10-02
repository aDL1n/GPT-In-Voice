package dev.adlin.llm.adapter.impl;

import dev.adlin.llm.adapter.EmbeddingAdapter;
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.response.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class NomicEmbedding implements EmbeddingAdapter {

    private static final Logger log = LogManager.getLogger(NomicEmbedding.class);

    private final OllamaAPI ollamaAPI;
    private static final String MODEL_NAME = "nomic-embed-text:v1.5";

    private boolean connected = false;

    public NomicEmbedding() {
        this.ollamaAPI = new OllamaAPI();

        loadModel();
    }

    @Nullable
    @Override
    public float[] embed(String message) {
        try {
            List<List<Double>> result = ollamaAPI.embed(MODEL_NAME, Collections.singletonList(message)).getEmbeddings();
            if (result == null || result.isEmpty()) {
                throw new RuntimeException("Empty embeddings result from Ollama");
            }

            List<Double> vec = result.get(0);
            float[] out = new float[vec.size()];

            for (int i = 0; i < vec.size(); i++)
                out[i] = vec.get(i).floatValue();

            return out;
        } catch (Exception e) {
            log.error("Failed to get embedding", e);
        }

        return null;
    }

    @Override
    public boolean isConnected() {
        return this.connected;
    }

    private void loadModel() {
        try {
            List<String> models = this.ollamaAPI.listModels().stream().map(Model::getModelName).toList();
            if (!models.contains(MODEL_NAME)) this.ollamaAPI.pullModel(MODEL_NAME);
            log.info("Model loaded successful!");
            this.connected = true;
        } catch (Exception e) {
            log.error("Model not loaded");
        }
    }
}
