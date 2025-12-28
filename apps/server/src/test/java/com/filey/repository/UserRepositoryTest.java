package com.filey.repository;

import com.filey.model.Role;
import com.filey.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindUserByUsername() {
        User user = new User("john", "hashedPassword", Role.USER);
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("john");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("john");
        assertThat(found.get().getRole()).isEqualTo(Role.USER);
    }

    @Test
    void shouldReturnEmpty_whenUserDoesNotExist() {
        Optional<User> found = userRepository.findByUsername("nonexistent");

        assertThat(found).isEmpty();
    }

    @Test
    void shouldReturnTrue_whenUsernameExists() {
        User user = new User("john", "hashedPassword", Role.USER);
        userRepository.save(user);

        boolean exists = userRepository.existsByUsername("john");

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalse_whenUsernameDoesNotExist() {
        boolean exists = userRepository.existsByUsername("nonexistent");

        assertThat(exists).isFalse();
    }

    @Test
    void shouldReturnTrue_whenRoleExists() {
        User admin = new User("admin", "hashedPassword", Role.ADMIN);
        userRepository.save(admin);

        boolean exists = userRepository.existsByRole(Role.ADMIN);

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalse_whenRoleDoesNotExist() {
        User user = new User("john", "hashedPassword", Role.USER);
        userRepository.save(user);

        boolean exists = userRepository.existsByRole(Role.ADMIN);

        assertThat(exists).isFalse();
    }

    @Test
    void shouldPersistTimestamps() {
        User user = new User("john", "hashedPassword", Role.USER);
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("john");

        assertThat(found).isPresent();
        assertThat(found.get().getCreatedAt()).isNotNull();
        assertThat(found.get().getUpdatedAt()).isNotNull();
    }
}
