package dev.adlin.service;

import dev.adlin.config.properties.ChatConfig;
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
    private final ChatMemory chatMemory;

    public static final String CONVERSATION_ID = "1";

    public MemoryService(JdbcChatMemoryRepository longChatMemoryRepository, ChatConfig config) {
        this.longChatMemoryRepository = longChatMemoryRepository;
        this.chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(config.getShortMemorySize())
                .build()
        ;

        this.chatMemory.add(CONVERSATION_ID, this.getLongMemories());
        log.info("Memory service initialized");
    }

    public void addMemory(Message message) {
        log.info("Added memory");
        this.chatMemory.add(CONVERSATION_ID, message);
    }

    public void removeMemory(int messageIndex) {
        //Get memories
        List<Message> memories = this.chatMemory.get(CONVERSATION_ID);
        memories.remove(messageIndex);
        //Clear all memories
        this.chatMemory.clear(CONVERSATION_ID);
        //Save updated memories
        this.chatMemory.add(CONVERSATION_ID, memories);
    }

    public List<Message> getShortMemories() {
        return this.chatMemory.get(CONVERSATION_ID);
    }

    public List<Message> getLongMemories() {
        return this.longChatMemoryRepository.findByConversationId(CONVERSATION_ID);
    }

    @PreDestroy
    private void saveAll() {
        log.info("Saving all memories");
        // Get all long memories
        List<Message> longMemories = this.getLongMemories();
        //Get and check short memories
        for (Message shortMemory : this.getShortMemories()) {
            if (!longMemories.contains(shortMemory)) {
                longMemories.add(shortMemory);
            }
        }
        // Update long memories
        this.longChatMemoryRepository.saveAll(CONVERSATION_ID, longMemories);
    }

    public ChatMemory getChatMemory() {
        return chatMemory;
    }
}