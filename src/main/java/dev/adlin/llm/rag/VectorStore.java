package dev.adlin.llm.rag;

import java.util.List;
import java.util.Map;

public interface VectorStore {
    void add(List<Chunk> chunks);
    List<ScoredChunk> search(float[] queryEmbedding, int topK, Map<String, String> filter);
}
