package dev.adlin.llm.rag;

import dev.adlin.llm.adapter.EmbeddingAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RagService {

    private static final Logger log = LogManager.getLogger(RagService.class);

    private final VectorStore vectorStore;
    private final EmbeddingAdapter embedding;

    public RagService(VectorStore vectorStore, EmbeddingAdapter embeddingAdapter) {
        this.vectorStore = vectorStore;
        this.embedding = embeddingAdapter;
    }

    @Nullable
    public List<ScoredChunk> search(String query, int topK, String collection) {
        if (!embedding.isConnected()) {
            log.error("Embedding is not connected");
            return null;
        }

        try {
            float[] embed = embedding.embed(query);
            Map<String,String> filter = collection == null ? null : Map.of("collection", collection);

            return vectorStore.search(embed, topK, filter);
        } catch (Exception e) {
            log.error("Could not find information", e);
        }

         return null;
    }

    public void addDocuments(List<String> texts, String source, String collection) {
        if (!embedding.isConnected()) {
            log.error("Embedding is not connected");
            return;
        }

        List<Chunk> chunks = new ArrayList<>();

        for (int i = 0; i < texts.size(); i++) {
            String text = texts.get(i);
            int chunkIndex = 0;

            for (String ch : split(text, 500, 200)) {
                try {
                    float[] emb = embedding.embed(ch);
                    chunks.add(new Chunk(source + "#" + (chunkIndex++), ch, Map.of(
                            "source", source, "collection", collection
                    ), emb));
                } catch (Exception e) {
                    log.error("Failed to add chunk to storage", e);
                }
            }
        }
        vectorStore.add(chunks);
    }


    public static List<String> split(String s, int size, int overlap) {
        if (s != null && s.length() < size) throw new IllegalArgumentException("Length exceeded");

        List<String> res = new ArrayList<>();

        for (int start = 0; start < s.length(); start += size - overlap) {
            int end = Math.min(s.length(), start + size);
            res.add(s.substring(start, end));

            if (end == s.length()) break;
        }

        return res;
    }

    public static String formatChunks(List<ScoredChunk> hits) {
        return hits.stream()
                .map(h -> "- " + h.chunk().text())
                .collect(Collectors.joining("\n"));
    }

}
