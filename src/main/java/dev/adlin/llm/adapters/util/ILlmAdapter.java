package dev.adlin.llm.adapters.util;

public interface ILlmAdapter {
    
    String sendMessage(Role role, String message);
    void startChat();
}
