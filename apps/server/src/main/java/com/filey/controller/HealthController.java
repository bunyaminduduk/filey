package com.filey.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    private final Instant startTime = Instant.now();

    @GetMapping("/health")
    public Map<String, Object> health() {
        long uptime = Instant.now().getEpochSecond() - startTime.getEpochSecond();

        return Map.of(
            "status", "healthy",
            "version", "0.0.1",
            "uptime", uptime
        );
    }
}
