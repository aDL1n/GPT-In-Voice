package dev.adlin.api.controller;

import dev.adlin.manager.ModelsManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/models")
public class ModelsController {

    private final ModelsManager modelsManager;

    public ModelsController(ModelsManager modelsManager) {
        this.modelsManager = modelsManager;
    }

    @PostMapping("/stt/enable")
    public ResponseEntity<Void> enableRecognitionModel(@RequestParam boolean enable) {
        this.modelsManager.getRecognitionModelState().setEnabled(enable);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/tts/enable")
    public ResponseEntity<Void> enableSynthesisModel(@RequestParam boolean enable) {
        this.modelsManager.getSynthesisModelState().setEnabled(enable);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/stt/list")
    public ResponseEntity<List<String>> getRecognitionModels() {
        return new ResponseEntity<>(this.modelsManager.getRecognitionModelNames(), HttpStatus.OK);
    }

    @GetMapping("tts/list")
    public ResponseEntity<List<String>> getSynthesisModels() {
        return new ResponseEntity<>(this.modelsManager.getSynthesisModelNames(), HttpStatus.OK);
    }

    @PostMapping("/stt/change")
    public ResponseEntity<Void> changeRecognitionModel(@RequestParam String modelName) {
        try {
            this.modelsManager.setCurrentRecognitionModel(modelName);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("tts/change")
    public ResponseEntity<Void> changeSynthesisModel(@RequestParam String modelName) {
        try {
            this.modelsManager.setCurrentSynthesisModel(modelName);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
