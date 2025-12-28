package com.filey.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordServiceTest {

    private PasswordService passwordService;

    @BeforeEach
    void setUp() {
        passwordService = new PasswordService(new BCryptPasswordEncoder(12));
    }

    @Test
    void shouldReturnHashedPassword_whenHashingRawPassword() {
        String rawPassword = "mySecretPassword";

        String hashedPassword = passwordService.hash(rawPassword);

        assertThat(hashedPassword).isNotEqualTo(rawPassword);
        assertThat(hashedPassword).startsWith("$2a$12$");
    }

    @Test
    void shouldReturnTrue_whenPasswordMatches() {
        String rawPassword = "mySecretPassword";
        String hashedPassword = passwordService.hash(rawPassword);

        boolean matches = passwordService.matches(rawPassword, hashedPassword);

        assertThat(matches).isTrue();
    }

    @Test
    void shouldReturnFalse_whenPasswordDoesNotMatch() {
        String rawPassword = "mySecretPassword";
        String hashedPassword = passwordService.hash(rawPassword);

        boolean matches = passwordService.matches("wrongPassword", hashedPassword);

        assertThat(matches).isFalse();
    }
}
