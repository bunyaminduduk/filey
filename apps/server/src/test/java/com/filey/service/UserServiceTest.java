package com.filey.service;

import com.filey.exception.ApiException;
import com.filey.exception.ErrorCode;
import com.filey.model.Role;
import com.filey.model.User;
import com.filey.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordService passwordService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordService);
    }

    @Test
    void shouldReturnTrue_whenAdminExists() {
        when(userRepository.existsByRole(Role.ADMIN)).thenReturn(true);

        boolean hasAdmin = userService.hasAdmin();

        assertThat(hasAdmin).isTrue();
    }

    @Test
    void shouldReturnFalse_whenNoAdminExists() {
        when(userRepository.existsByRole(Role.ADMIN)).thenReturn(false);

        boolean hasAdmin = userService.hasAdmin();

        assertThat(hasAdmin).isFalse();
    }

    @Test
    void shouldCreateUser_whenUsernameIsUnique() {
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(passwordService.hash("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = userService.createUser("john", "password123", Role.USER);

        assertThat(user.getUsername()).isEqualTo("john");
        assertThat(user.getPassword()).isEqualTo("hashedPassword");
        assertThat(user.getRole()).isEqualTo(Role.USER);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowException_whenUsernameAlreadyExists() {
        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser("john", "password123", Role.USER))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getErrorCode()).isEqualTo(ErrorCode.USER_ALREADY_EXISTS));
    }

    @Test
    void shouldReturnUser_whenUserExistsByUsername() {
        User expectedUser = new User("john", "hashedPassword", Role.USER);
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(expectedUser));

        Optional<User> user = userService.findByUsername("john");

        assertThat(user).isPresent();
        assertThat(user.get().getUsername()).isEqualTo("john");
    }

    @Test
    void shouldReturnEmpty_whenUserDoesNotExistByUsername() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<User> user = userService.findByUsername("unknown");

        assertThat(user).isEmpty();
    }

    @Test
    void shouldReturnUser_whenUserExistsById() {
        User expectedUser = new User("john", "hashedPassword", Role.USER);
        when(userRepository.findById(anyString())).thenReturn(Optional.of(expectedUser));

        Optional<User> user = userService.findById("some-id");

        assertThat(user).isPresent();
    }

    @Test
    void shouldReturnEmpty_whenUserDoesNotExistById() {
        when(userRepository.findById("unknown-id")).thenReturn(Optional.empty());

        Optional<User> user = userService.findById("unknown-id");

        assertThat(user).isEmpty();
    }
}
