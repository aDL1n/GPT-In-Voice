package dev.adlin.llm.adapter;

import dev.adlin.llm.chat.ChatMessage;

import java.util.List;

public interface LlmAdapter {
    String sendMessages(List<ChatMessage> messages);
    boolean isConnected();
}
