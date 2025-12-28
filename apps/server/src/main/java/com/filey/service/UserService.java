package com.filey.service;

import com.filey.exception.ApiException;
import com.filey.exception.ErrorCode;
import com.filey.model.Role;
import com.filey.model.User;
import com.filey.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public UserService(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    public boolean hasAdmin() {
        return userRepository.existsByRole(Role.ADMIN);
    }

    public User createUser(String username, String password, Role role) {
        if (userRepository.existsByUsername(username)) {
            throw new ApiException(ErrorCode.USER_ALREADY_EXISTS);
        }

        String hashedPassword = passwordService.hash(password);
        User user = new User(username, hashedPassword, role);

        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }
}