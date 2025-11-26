package dev.adlin.service;

import dev.adlin.model.attention.SimpleAttention;
import dev.adlin.model.tool.DiscordTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class ModelService {

    private static final Logger log = LogManager.getLogger(ModelService.class);

    private final MemoryService memoryService;
    private final RagService ragService;
    private final DiscordTools discordTools;
    private final SimpleAttention attention;

    private final ChatClient chatClient;
    private final AtomicBoolean processing = new AtomicBoolean(false);

    public ModelService(
            ChatClient chatClient,
            MemoryService memoryService,
            RagService ragService,
            DiscordTools discordTools,
            SimpleAttention attention
    ) {
        this.memoryService = memoryService;
        this.ragService = ragService;
        this.discordTools = discordTools;
        this.attention = attention;
        this.chatClient = chatClient;

        log.info("Model service initialized");
    }

    public AssistantMessage ask(Message message) {
        log.info("Asking model");
        processing.set(true);

        if (message.getText() == null || message.getText().isEmpty()) return new AssistantMessage("");
        this.memoryService.addMemory(message);
        this.ragService.addMessages(message);

        if (!attention.check(message)) return new AssistantMessage("");

        List<Message> messages = new ArrayList<>();

        String ragResult = this.ragService.searchInMemory(message.getText());
        if (ragResult != null) messages.add(new SystemMessage(ragResult));

        messages.addAll(this.memoryService.getShortMemories());

        ToolCallback[] toolCallback = ToolCallbacks.from(discordTools);

        Prompt prompt = Prompt.builder()
                .messages(messages)
                .build();

        ChatResponse chatResponse = chatClient
                .prompt(prompt)
                .toolCallbacks(toolCallback)
                .call()
                .chatResponse();

        if (chatResponse != null)
            this.ragService.addChatResponse(chatResponse);

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

        processing.set(false);
        return assistantMessage;
    }

    public AtomicBoolean getProcessing() {
        return processing;
    }
}
