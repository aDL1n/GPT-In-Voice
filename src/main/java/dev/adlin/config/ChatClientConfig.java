package dev.adlin.config;

import dev.adlin.memory.SystemPromptLoader;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    private final ChatModel chatModel;
    private final SystemPromptLoader systemPromptLoader;

    public ChatClientConfig(ChatModel chatModel, SystemPromptLoader systemPromptLoader) {
        this.chatModel = chatModel;
        this.systemPromptLoader = systemPromptLoader;
    }

    @Bean
    public ChatOptions chatOptions() {
        return ChatOptions.builder()
                .maxTokens(16384)
                .build();
    }

    @Bean
    public ChatClient chatClient() {
        System.out.println(chatOptions().getTemperature());

        return ChatClient.builder(chatModel)
                .defaultOptions(chatOptions())
                .defaultSystem(systemMessage().getText())
                .build();
    }

    public SystemMessage systemMessage() {
        return (SystemMessage) systemPromptLoader.load();
    }
}
