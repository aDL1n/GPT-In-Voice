package dev.adlin.llm.rag;

import dev.adlin.llm.adapters.EmbeddingAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RagService {
    private final VectorStore vectorStore;
    private final EmbeddingAdapter embedding;

    public RagService(VectorStore vectorStore, EmbeddingAdapter embeddingAdapter) {
        this.vectorStore = vectorStore;
        this.embedding = embeddingAdapter;
    }

    public synchronized List<ScoredChunk> search(String query, int topK, String collection) {
        try {
            float[] embed = embedding.embed(query);
            Map<String,String> filter = collection == null ? null : Map.of("collection", collection);

            return vectorStore.search(embed, topK, filter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addDocuments(List<String> texts, String source, String collection) {
        List<Chunk> chunks = new ArrayList<>();

        for (int i = 0; i < texts.size(); i++) {
            String text = texts.get(i);
            for (String ch : split(text, 500, 200)) {
                try {
                    float[] emb = embedding.embed(ch);
                    chunks.add(new Chunk(source + "#" + (i++), ch, Map.of(
                            "source", source, "collection", collection
                    ), emb));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        vectorStore.add(chunks);
        System.out.println("[RAG] store size: " + vectorStore.size()); // сделай size() если нет

    }

    public static List<String> split(String s, int size, int overlap) {
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
