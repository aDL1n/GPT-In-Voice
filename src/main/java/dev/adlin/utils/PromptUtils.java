package dev.adlin.utils;

import dev.adlin.llm.adapters.Role;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;

public class PromptUtils {

    public static OllamaChatMessageRole translateRole(Role role) {
        return switch (role) {
            case TOOL -> OllamaChatMessageRole.TOOL;
            case SYSTEM -> OllamaChatMessageRole.SYSTEM;
            case ASSISTANT -> OllamaChatMessageRole.ASSISTANT;
            case USER -> OllamaChatMessageRole.USER;
        };
    }
}
