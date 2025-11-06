package dev.adlin.api.controller;

import dev.adlin.service.ModelService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/model")
public class ModelController {

    private static final Logger log = LogManager.getLogger(ModelController.class);
    private final ModelService modelService;

    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @GetMapping("/ask")
    public ResponseEntity<String> ask(@RequestParam String message, @RequestParam String username) {
        log.info("REST request to ask for memory message");
        return ResponseEntity.ok(this.modelService.ask(new UserMessage(username + ": " + message)).getText());
    }

    @GetMapping("")
    public ResponseEntity<String> getModelName() {
        log.info("REST request to get model name");
        return new ResponseEntity<>(this.modelService.getModelName().orElse("Model not loaded"), HttpStatus.OK);
    }

    @GetMapping("/systemPrompt")
    public ResponseEntity<String> getSystemPrompt() {
        log.info("REST request to get system prompt");
        return ResponseEntity.ok(this.modelService.getSystemMessage().getText());
    }

}