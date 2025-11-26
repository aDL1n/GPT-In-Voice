package dev.adlin.config;

import dev.adlin.memory.SystemPromptLoader;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    private final ChatModel chatModel;
    private final SystemPromptLoader systemPromptLoader;

    private SystemMessage systemMessage;

    public ChatClientConfig(
            ChatModel chatModel,
            SystemPromptLoader systemPromptLoader
    ) {
        this.chatModel = chatModel;
        this.systemPromptLoader = systemPromptLoader;
    }

    @Bean
    public ChatClient chatClient() {
        return ChatClient.builder(chatModel)
                .defaultOptions(chatModel.getDefaultOptions())
                .defaultSystem(systemMessage().getText())
                .build();
    }

    public String modelName() {
        String modelName = chatModel.getDefaultOptions().getModel();
        return modelName != null ? modelName : "model not loaded";
    }

    public SystemMessage systemMessage() {
        if (systemMessage == null) systemMessage = (SystemMessage) systemPromptLoader.load();
        return systemMessage;
    }
}
