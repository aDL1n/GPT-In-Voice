package dev.adlin.api.controller;

import dev.adlin.config.properties.ChatConfig;
import dev.adlin.service.MemoryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ai.chat.messages.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memory")
public class MemoryController {

    private static final Logger log = LogManager.getLogger(MemoryController.class);
    private final MemoryService memoryService;
    private final ChatConfig chatConfig;

    public MemoryController(MemoryService memoryService, ChatConfig chatConfig) {
        this.memoryService = memoryService;
        this.chatConfig = chatConfig;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Message>> getChatMemory() {
        log.info("REST request to get all chat memory");
        return ResponseEntity.ok(this.memoryService.getShortMemories());
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addMemory(@RequestBody String messageType,@RequestBody String messageText) {
        log.info("REST request to save memory message");
        try {
            MessageType messageTypeEnum = MessageType.valueOf(messageType);

            Message message = null;
            switch (messageTypeEnum) {
                case ASSISTANT -> message = new AssistantMessage(messageText);
                case USER -> message = new UserMessage(chatConfig.getOwnerName() + messageText);
                case SYSTEM -> message = new SystemMessage(messageText);
            }

            if (message != null) this.memoryService.addMemory(message);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteMemory(@RequestBody int messageIndex) {
        log.info("REST request to delete memory message");
        this.memoryService.removeMemory(messageIndex);
        return ResponseEntity.ok().build();
    }
}