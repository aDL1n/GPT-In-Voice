package dev.adlin.api.controllers;

import dev.adlin.utils.BotState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bot-state")
public class BotStateController {

    private static final Logger log = LoggerFactory.getLogger(BotStateController.class);

    private final BotState state;

    public BotStateController(BotState state) {
        this.state = state;
    }

    @GetMapping()
    public BotState getState() {
        log.info("REST request to get the state");

        return state;
    }

}
