package dev.adlin.llm.adapters;

import dev.adlin.utils.chat.ChatMessage;

import java.util.List;

public interface LlmAdapter {
    String sendMessages(List<ChatMessage> messages);
    boolean isConnected();
}
