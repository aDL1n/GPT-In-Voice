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

    public static final String CONVERSATION_ID = "1";

    public MemoryService(JdbcChatMemoryRepository chatMemoryRepository) {
        this.chatMemoryRepository = chatMemoryRepository;
        this.chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(30)
                .build()
        ;

        log.info("Memory service initialized");
    }

    public void addMemory(Message message) {
        log.info("Added memory");
        this.chatMemory.add(CONVERSATION_ID, message);
        this.chatMemoryRepository.saveAll(CONVERSATION_ID, getMemories());
    }

    public List<Message> getMemories() {
        return this.chatMemory.get(CONVERSATION_ID);
    }

    @PreDestroy
    private void saveAll() {
        log.info("Saving all memories");
        this.chatMemoryRepository.saveAll(CONVERSATION_ID, getMemories());
    }
}
