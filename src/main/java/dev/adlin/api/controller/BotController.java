package dev.adlin.api.controller;

import dev.adlin.api.state.BotState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<BotState> getState() {
        log.info("REST request to get the state");
        return new ResponseEntity<>(state, HttpStatus.OK);
    }

}
