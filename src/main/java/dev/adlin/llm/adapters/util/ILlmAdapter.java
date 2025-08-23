package dev.adlin.llm.adapters.util;

public interface ILlmAdapter {
    
    void sendMessage(Role role, String message);
    void startChat();
}
