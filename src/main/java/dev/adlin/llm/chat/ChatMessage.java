package dev.adlin.llm.chat;

import dev.adlin.llm.adapter.Role;

public record ChatMessage(Role role, String userName, String content) {
}
