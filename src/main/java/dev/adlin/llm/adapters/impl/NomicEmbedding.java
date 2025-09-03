package dev.adlin.llm.adapters.impl;

import dev.adlin.llm.adapters.EmbeddingAdapter;
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.response.Model;

import java.util.Collections;
import java.util.List;

public class NomicEmbedding implements EmbeddingAdapter {

    private final OllamaAPI ollamaAPI;
    private static final String MODEL_NAME = "nomic-embed-text:v1.5";

    public NomicEmbedding() {
        this.ollamaAPI = new OllamaAPI();

        loadModel();
    }

    @Override
    public float[] embed(String message) {
        try {
            List<List<Double>> result = ollamaAPI.embed(MODEL_NAME, Collections.singletonList(message)).getEmbeddings();
            if (result == null || result.isEmpty()) {
                throw new RuntimeException("Empty embeddings result from Ollama");
            }

            List<Double> vec = result.getFirst();
            float[] out = new float[vec.size()];

            for (int i = 0; i < vec.size(); i++)
                out[i] = vec.get(i).floatValue();

            return out;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get embedding", e);
        }
    }

    private void loadModel() {
        try {
            List<String> models = this.ollamaAPI.listModels().stream().map(Model::getModelName).toList();
            if (!models.contains(MODEL_NAME)) this.ollamaAPI.pullModel(MODEL_NAME);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
