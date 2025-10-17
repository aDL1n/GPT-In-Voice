package dev.adlin.api.controller;

import dev.adlin.service.MemoryService;
import org.springframework.ai.chat.messages.Message;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/memory")
public class MemoryController {

    private static final Duration EVENT_INTERVAL = Duration.ofSeconds(5);

    private final MemoryService memoryService;

    public MemoryController(MemoryService memoryService) {
        this.memoryService = memoryService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Message>> getChatMemory() {
        return ResponseEntity.ok(this.memoryService.getMemories());
    }
}