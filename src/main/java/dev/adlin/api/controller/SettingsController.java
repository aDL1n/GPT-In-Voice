package dev.adlin.api.controller;

import dev.adlin.manager.ModelsManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private static final Logger log = LogManager.getLogger(SettingsController.class);
    private final ModelsManager modelsManager;

    public SettingsController(ModelsManager modelsManager) {
        this.modelsManager = modelsManager;
    }

    @PostMapping("/stt/enable")
    public ResponseEntity<Void> enableRecognitionModel(@RequestBody boolean enable) {
        this.modelsManager.getRecognitionModelState().setEnabled(enable);
        log.info("Setting recognition model enable={}", enable);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tts/enable")
    public ResponseEntity<Void> enableSynthesisModel(@RequestBody boolean enable) {
        this.modelsManager.getSynthesisModelState().setEnabled(enable);
        log.info("Setting synthesis model enable={}", enable);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stt/list")
    public ResponseEntity<List<String>> getRecognitionModels() {
        return new ResponseEntity<>(this.modelsManager.getRecognitionModelNames(), HttpStatus.OK);
    }

    @GetMapping("/tts/list")
    public ResponseEntity<List<String>> getSynthesisModels() {
        return new ResponseEntity<>(this.modelsManager.getSynthesisModelNames(), HttpStatus.OK);
    }

    @PostMapping("/stt/change")
    public ResponseEntity<Void> changeRecognitionModel(@RequestBody String modelName) {
        try {
            this.modelsManager.setCurrentRecognitionModel(modelName);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/tts/change")
    public ResponseEntity<Void> changeSynthesisModel(@RequestBody String modelName) {
        try {
            this.modelsManager.setCurrentSynthesisModel(modelName);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
