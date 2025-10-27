package dev.adlin.api.controller;

import dev.adlin.service.MemoryService;
import org.springframework.ai.chat.messages.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/memory")
public class MemoryController {

    private final MemoryService memoryService;

    public MemoryController(MemoryService memoryService) {
        this.memoryService = memoryService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Message>> getChatMemory() {
        return ResponseEntity.ok(this.memoryService.getMemories());
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addMemory(@RequestBody String messageType, String messageText) {
        try {
            MessageType messageTypeEnum = MessageType.valueOf(messageType);

            Message message = null;
            switch (messageTypeEnum) {
                case ASSISTANT -> message = new AssistantMessage(messageText);
                case USER -> message = new UserMessage("aDL1n: " + messageText);
                case SYSTEM -> message = new SystemMessage(messageText);
            }

            if (message != null) this.memoryService.addMemory(message);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMemory(@RequestBody int messageIndex) {
        this.memoryService.removeMemory(messageIndex);
        return ResponseEntity.ok().build();
    }
}