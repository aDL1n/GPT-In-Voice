package dev.adlin.api.controller;

import dev.adlin.api.state.ApiState;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final ApiState state;

    public ApiController(ApiState state) {
        this.state = state;
    }

    @GetMapping()
    public ResponseEntity<ApiState> getApiStatus() {
        return ResponseEntity.ok(this.state);
    }
}
