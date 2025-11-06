package dev.adlin.api.controller;

import dev.adlin.api.state.ApiState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    private static final Logger log = LogManager.getLogger(ApiController.class);
    private final ApiState state;

    public ApiController(ApiState state) {
        this.state = state;
    }

    @GetMapping()
    public ResponseEntity<ApiState> getApiStatus() {
        log.info("REST request to get api status");
        return ResponseEntity.ok(this.state);
    }
}
