package dev.adlin.llm.adapters;

public interface ILlmAdapter {
    String sendMessage(Role role, String message);
    void startChat();
}
