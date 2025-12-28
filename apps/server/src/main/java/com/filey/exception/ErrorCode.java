package com.filey.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // General errors
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "An internal error occurred"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation failed"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"),

    // Authentication errors
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Authentication required"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid credentials"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Access denied"),

    // User errors
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "User already exists"),

    // File errors
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "File not found"),
    FILE_ALREADY_EXISTS(HttpStatus.CONFLICT, "File already exists"),
    FILE_TOO_LARGE(HttpStatus.CONTENT_TOO_LARGE, "File exceeds maximum size"),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "Invalid file type");

    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
