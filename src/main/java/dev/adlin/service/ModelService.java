package dev.adlin.service;

import dev.adlin.memory.SystemPromptLoader;
import dev.adlin.rag.Memory2RagLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class ModelService {

    private static final Logger log = LogManager.getLogger(ModelService.class);

    private final ChatModel chatModel;
    private final MemoryService memoryService;
    private final RagService ragService;

    private SystemMessage systemMessage;
    private final Memory2RagLoader memory2RagLoader;

    private final ChatClient chatClient;
    private final AtomicBoolean processing = new AtomicBoolean(false);

    public ModelService(
            ChatModel chatModel,
            MemoryService memoryService,
            RagService ragService,
            SystemPromptLoader systemPromptLoader,
            Memory2RagLoader memory2RagLoader
    ) {
        this.chatModel = chatModel;
        this.memoryService = memoryService;
        this.ragService = ragService;

        this.chatClient = ChatClient.builder(chatModel)
                .build();

        systemMessage = (SystemMessage) systemPromptLoader.load().orElse(null);
        this.memory2RagLoader = memory2RagLoader;
        log.info("Model service initialized");
    }

    public AssistantMessage ask(Message message) {
        log.info("Asking model");
        processing.set(true);

        if (message.getText() == null && message.getText().isEmpty()) return new AssistantMessage("");
        this.memoryService.addMemory(message);

        List<Message> messages = new ArrayList<>();
        messages.add(systemMessage);

        String ragResult = this.ragService.searchInMemory(message.getText());
        if (ragResult != null) messages.add(new SystemMessage(ragResult));

        messages.addAll(this.memoryService.getShortMemories());

        Prompt prompt = Prompt.builder()
                .chatOptions(ChatOptions.builder()
                        .maxTokens(8192)
                        .build()
                ).messages(messages)
                .build();

        ChatResponse chatResponse = chatClient.prompt(prompt)
                .call()
                .chatResponse();

        if (chatResponse != null)
            this.ragService.add(chatResponse);

        System.out.println("Prompt: \n" + prompt.getInstructions().stream()
                .map(Message::getText)
                .collect(Collectors.joining("\n"))
        );

        AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
        this.memoryService.addMemory(assistantMessage);

        System.out.println("AI: " + chatResponse.getResults().stream()
                .map(gen ->
                        gen.getOutput().getText())
                .collect(Collectors.joining("\n")));

        log.info("Model response received");

        memory2RagLoader.add(message, assistantMessage);

        processing.set(false);
        return assistantMessage;
    }

    public SystemMessage getSystemMessage() {
        return this.systemMessage;
    }

    public Optional<String> getModelName() {
        return Optional.ofNullable(chatModel.getDefaultOptions().getModel());
    }

    public AtomicBoolean getProcessing() {
        return processing;
    }
}
