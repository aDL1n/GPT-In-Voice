package dev.adlin.llm.adapters;

public interface LlmAdapter {
    String sendMessage(Role role, String message);
}
