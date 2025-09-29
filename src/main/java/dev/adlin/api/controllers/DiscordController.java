package dev.adlin.api.controllers;

import dev.adlin.api.states.DiscordState;
import dev.adlin.utils.chat.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/discord")
public class DiscordController {

    private static final Logger log = LoggerFactory.getLogger(DiscordController.class);

    private boolean isConnected = false;

    private DiscordState discordState;

    public DiscordController(DiscordState discordState) {
        this.discordState = discordState;
    }

}
