package dev.adlin.service;

import jakarta.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemoryService {

    private static final Logger log = LogManager.getLogger(MemoryService.class);
    private final JdbcChatMemoryRepository chatMemoryRepository;
    private final ChatMemory chatMemory;

    public MemoryService(JdbcChatMemoryRepository chatMemoryRepository) {
        this.chatMemoryRepository = chatMemoryRepository;
        this.chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(100)
                .build()
        ;

        log.info("Memory service initialized");
    }

    public void addMemory(Message message) {
        log.info("Added memory");
        this.chatMemory.add("1", message);
        this.chatMemoryRepository.saveAll("1", getMemories());
    }

    public List<Message> getMemories() {
        return this.chatMemory.get("1");
    }

    @PreDestroy
    private void saveAll() {
        log.info("Saving all memories");
        this.chatMemoryRepository.saveAll("1", getMemories());
    }
}
