package dev.adlin.utils;

import dev.adlin.llm.adapters.Role;
import dev.adlin.llm.memory.LongTermMemoryData;
import io.github.ollama4j.models.chat.OllamaChatMessage;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PromptBuilder {

    public static OllamaChatRequest buildWithMemory(@NotNull OllamaChatRequestBuilder builder,
                                                    @NotNull Role role,
                                                    @NotNull String content,
                                                    @Nullable String userName,
                                                    @NotNull List<LongTermMemoryData> memories)
    {
        return builder.withMessages(memories
                .stream()
                .map(data ->
                        new OllamaChatMessage(translateRole(data.role), data.userName != null
                                ? data.userName + ":" + data.message
                                : data.message
                        )
                )
                .toList()
        ).withMessage(translateRole(role), userName != null ? userName + ":" + content : content).build();
    }

    public static OllamaChatMessageRole translateRole(Role role) {
        return switch (role) {
            case TOOL -> OllamaChatMessageRole.TOOL;
            case SYSTEM -> OllamaChatMessageRole.SYSTEM;
            case ASSISTANT -> OllamaChatMessageRole.ASSISTANT;
            case USER -> OllamaChatMessageRole.USER;
        };
    }
}
