package dev.adlin.api.controller;

import dev.adlin.api.state.SelectedModelsState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/models")
public class ModelsController {

    private static final Logger log = LoggerFactory.getLogger(ModelsController.class);

    private final SelectedModelsState selectedModelsState;

    public ModelsController(SelectedModelsState selectedModelsState) {
        this.selectedModelsState = selectedModelsState;
    }

    @GetMapping
    public ResponseEntity<SelectedModelsState> getSelectedModelsState() {
        log.info("REST request to get selectedModelsState");
        return new ResponseEntity<>(selectedModelsState, HttpStatus.OK);
    }
}
