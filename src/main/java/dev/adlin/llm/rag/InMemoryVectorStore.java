package dev.adlin.llm.rag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryVectorStore implements VectorStore {

    private static final Logger log = LogManager.getLogger(InMemoryVectorStore.class);

    private final List<EmbedEntry> data = new ArrayList<>();

    @Override
    public void add(List<Chunk> chunks) {
        if (chunks == null || chunks.isEmpty()) return;

        for (Chunk chunk : chunks) {
            float[] embed = chunk.embedding();
            if (embed == null || embed.length == 0) continue;
            float[] normEmbed = normalize(embed);

            data.add(new EmbedEntry(chunk, normEmbed));
        }
    }

    @Override
    public synchronized List<ScoredChunk> search(float[] queryEmbedding, int topK, Map<String, String> filter) {
        if (queryEmbedding == null) throw new IllegalArgumentException("queryEmbedding is null");
        if (topK <= 0) return List.of();

        float[] embeddingNorm = normalize(queryEmbedding);

        PriorityQueue<ScoredChunk> heap = new PriorityQueue<>(Comparator.comparingDouble(ScoredChunk::score));

        for (EmbedEntry embedEntry : data) {
            if (!matchesFilter(embedEntry.chunk(), filter)) continue;

            double score = dot(embeddingNorm, embedEntry.normEmbed());

            if (heap.size() < topK) {
                heap.offer(new ScoredChunk(embedEntry.chunk(), score));
            } else if (score > heap.peek().score()) {
                heap.poll();
                heap.offer(new ScoredChunk(embedEntry.chunk(), score));
            }
        }

        ArrayList<ScoredChunk> result = new ArrayList<>(heap);

        result.sort(Comparator.comparingDouble(ScoredChunk::score).reversed());

        String debugOutput = result.stream().map(s -> s.score() + " " + s.chunk().text()).collect(Collectors.joining("\n"));
        log.debug(debugOutput);

        return result;
    }

    @Override
    public int size() {
        return data.size();
    }

    private boolean matchesFilter(Chunk chunk, Map<String, String> filter) {
        Map<String, String> meta = chunk.meta();
        if (meta == null || meta.isEmpty()) {
            return false;
        }

        for (var entry : filter.entrySet()) {
            String v1 = meta.get(entry.getKey());
            String v2 = entry.getValue();
            if (v1 == null) return false;
            if (!v1.equalsIgnoreCase(v2.trim())) return false;
        }
        return true;
    }


    private float[] normalize(float[] v) {
        if (v == null || v.length == 0) return null;

        float n2 = 0f;
        for (float x : v) {
            n2 += x * x;
        }
        if (n2 == 0f) return null;

        float inv = (float) (1.0 / Math.sqrt(n2));
        float[] out = new float[v.length];
        for (int i = 0; i < v.length; i++) {
            out[i] = v[i] * inv;
        }
        return out;
    }

    private static double dot(float[] a, float[] b) {
        int minLength = Math.min(a.length, b.length);

        double s = 0.0;
        for (int i = 0; i < minLength; i++) s += (double) a[i] * b[i];
        return s;
    }


}
