package dev.adlin.service;

import jakarta.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemoryService {

    private static final Logger log = LogManager.getLogger(MemoryService.class);
    private final JdbcChatMemoryRepository longChatMemoryRepository;
    private final InMemoryChatMemoryRepository shortChatMemoryRepository;
    private final ChatMemory chatMemory;

    public static final String CONVERSATION_ID = "1";


    public MemoryService(JdbcChatMemoryRepository longChatMemoryRepository) {
        this.longChatMemoryRepository = longChatMemoryRepository;
        this.shortChatMemoryRepository = new InMemoryChatMemoryRepository();
        this.chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(shortChatMemoryRepository)
                .maxMessages(30)
                .build()
        ;

        this.shortChatMemoryRepository.saveAll(CONVERSATION_ID, this.getLongMemories());
        log.info("Memory service initialized");
    }

    public void addMemory(Message message) {
        log.info("Added memory");
        this.chatMemory.add(CONVERSATION_ID, message);
    }

    public List<Message> getMemories() {
        return this.chatMemory.get(CONVERSATION_ID);
    }

    public List<Message> getLongMemories() {
        return this.longChatMemoryRepository.findByConversationId(CONVERSATION_ID);
    }

    @PreDestroy
    private void saveAll() {
        log.info("Saving all memories");
        this.longChatMemoryRepository.saveAll(CONVERSATION_ID, getMemories());
    }
}
