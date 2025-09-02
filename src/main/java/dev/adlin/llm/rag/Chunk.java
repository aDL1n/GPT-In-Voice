package dev.adlin.llm.rag;

import java.util.Map;

public record Chunk(String id, String text, Map<String, String> meta, float[] embedding) {

}
