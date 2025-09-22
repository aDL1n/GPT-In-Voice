package dev.adlin.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiStateController {

    @GetMapping
    public String getAlive() {
        return "OK";
    }
}
