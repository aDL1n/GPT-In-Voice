package dev.adlin.api.controller;

import dev.adlin.api.state.DiscordState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/discord")
public class DiscordController {

    private static final Logger log = LoggerFactory.getLogger(DiscordController.class);

    private final DiscordState discordState;

    public DiscordController(DiscordState discordState) {
        this.discordState = discordState;
    }

    @GetMapping("/voice/users")
    public ResponseEntity<List<String>> getVoiceUsers() {
        if (!this.discordState.isReady()) return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);

        log.info("REST request to get users in voice");
        return new ResponseEntity<>(
                discordState.getVoiceUserNameList().orElse(Collections.emptyList()),
                HttpStatus.OK
        );
    }

    @GetMapping("/voice/name")
    public ResponseEntity<String> getVoiceChannelName() {
        if (!this.discordState.isReady()) return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);

        log.info("REST request to get voice channel name");
        return new ResponseEntity<>(
                discordState.getVoiceChannelName().orElse(""),
                HttpStatus.OK
        );
    }

    @GetMapping("voice/status")
    public ResponseEntity<Boolean> isConnectedToVoiceChannel() {
        if (!this.discordState.isReady()) return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);

        log.info("REST request to get voice channel status");
        return new ResponseEntity<>(
                this.discordState.isInVoiceChannel(),
                HttpStatus.OK
        );
    }

}
