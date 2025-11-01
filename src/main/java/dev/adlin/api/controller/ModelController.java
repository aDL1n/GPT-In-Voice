package dev.adlin.api.controller;

import dev.adlin.service.ModelService;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/model")
public class ModelController {

    private final ModelService modelService;

    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @GetMapping("/ask")
    public ResponseEntity<String> ask(@RequestParam String message, @RequestParam String username) {
        return ResponseEntity.ok(this.modelService.ask(new UserMessage(username + ": " + message)).getText());
    }

    @GetMapping("")
    public ResponseEntity<String> getModelName() {
        return new ResponseEntity<>(this.modelService.getModelName().orElse("Model not loaded"), HttpStatus.OK);
    }

    @PostMapping("/changeSystemPrompt")
    public ResponseEntity<String> changeSystemPrompt(@RequestParam String newSystemPrompt) {
        this.modelService.changeSystemMessage(newSystemPrompt);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/systemPrompt")
    public ResponseEntity<String> getSystemPrompt() {
        return ResponseEntity.ok(this.modelService.getSystemMessage().getText());
    }

}