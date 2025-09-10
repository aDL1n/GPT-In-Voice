package dev.adlin.utils.chat;

import dev.adlin.llm.adapters.Role;

public record ChatMessage(Role role, String userName, String content) {
}
