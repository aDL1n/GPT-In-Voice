package dev.adlin.rag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class Memory2RagLoader {

    private static final Logger log = LogManager.getLogger(Memory2RagLoader.class);

    private final VectorStore vectorStore;
    private final ChatMemoryRepository chatMemoryRepository;

    public Memory2RagLoader(VectorStore vectorStore, ChatMemoryRepository chatMemoryRepository) {
        this.vectorStore = vectorStore;
        this.chatMemoryRepository = chatMemoryRepository;
    }

    public void add(Message... messages) {
        CompletableFuture.runAsync(() -> {
            log.info("Adding messages to rag...");
            this.vectorStore.add(Arrays.stream(messages)
                    .map(message ->
                            new Document(message.getText(), message.getMetadata()))
                    .collect(Collectors.toList())
            );
            log.info("Messages added to rag");
        });
    }
}
