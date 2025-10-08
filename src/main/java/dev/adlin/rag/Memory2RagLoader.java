package dev.adlin.rag;

import dev.adlin.service.MemoryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class Memory2RagLoader implements CommandLineRunner {

    private static final Logger log = LogManager.getLogger(Memory2RagLoader.class);

    private final VectorStore vectorStore;
    private final ChatMemoryRepository chatMemoryRepository;

    public Memory2RagLoader(VectorStore vectorStore, ChatMemoryRepository chatMemoryRepository) {
        this.vectorStore = vectorStore;
        this.chatMemoryRepository = chatMemoryRepository;
    }

    @Override
    public void run(String... args) {
        log.info("Loading memories to RAG");

        this.vectorStore.add(this.chatMemoryRepository.findByConversationId(MemoryService.CONVERSATION_ID)
                .stream()
                .map(message -> new Document(message.getText()))
                .collect(Collectors.toList())
        );
    }
}
