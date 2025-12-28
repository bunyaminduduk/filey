package com.filey.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Health check response")
public record HealthResponse(
        @Schema(description = "Current health status", example = "healthy")
        String status,
        @Schema(description = "API version", example = "0.0.1")
        String version,
        @Schema(description = "Server uptime in seconds", example = "3600")
        long uptime
) {}