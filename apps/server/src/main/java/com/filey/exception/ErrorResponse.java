package com.filey.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error response")
public record ErrorResponse(
        @Schema(description = "Error code identifier")
        ErrorCode code,
        @Schema(description = "Human-readable error message")
        String message,
        @Schema(description = "HTTP status code")
        int status
) {
    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(
                errorCode,
                message,
                errorCode.getStatus().value()
        );
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return of(errorCode, errorCode.getDefaultMessage());
    }
}
