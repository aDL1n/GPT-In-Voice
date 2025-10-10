package dev.adlin.api.controller;

import dev.adlin.service.ModelService;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/model")
public class ModelController {

    private final ModelService modelService;

    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @GetMapping("/ask")
    public ResponseEntity<String> ask(@RequestParam String message, @RequestParam String username) {
        return ResponseEntity.ok(this.modelService.ask(new UserMessage(username + ": " + message)).getText());
    }
}
