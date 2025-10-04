package dev.adlin.api.controller;

import dev.adlin.api.state.RagState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rag")
public class RagController {

    private static final Logger log = LoggerFactory.getLogger(RagController.class);

    private final RagState ragState;

    public RagController(RagState ragState) {
        this.ragState = ragState;
    }
}
