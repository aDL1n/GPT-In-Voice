package dev.adlin.api.controllers;

import dev.adlin.api.states.BotState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bot")
public class BotController {

    private static final Logger log = LoggerFactory.getLogger(BotController.class);

    private final BotState state;

    public BotController(BotState state) {
        this.state = state;
    }

    @GetMapping("/state")
    public BotState getState() {
        log.info("REST request to get the state");

        return state;
    }

}
