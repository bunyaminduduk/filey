package com.filey.controller;

import com.filey.dto.HealthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api")
@Tag(name = "Health", description = "Health check endpoints")
public class HealthController {

    private final Instant startTime = Instant.now();

    @GetMapping("/health")
    @Operation(summary = "Check API health", description = "Returns the current health status of the API")
    public HealthResponse health() {
        long uptime = Instant.now().getEpochSecond() - startTime.getEpochSecond();
        return new HealthResponse("healthy", "0.0.1", uptime);
    }
}
