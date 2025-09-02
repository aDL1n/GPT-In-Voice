package dev.adlin.llm.adapters;

public interface EmbeddingAdapter {
    float[] embed(String message);
}
