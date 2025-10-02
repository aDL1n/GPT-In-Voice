package dev.adlin.llm.adapter;

public interface EmbeddingAdapter {
    float[] embed(String message);
    boolean isConnected();
}
