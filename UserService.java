package com.tripplanner.TripPlanner.service;

import com.tripplanner.TripPlanner.model.Gender;
import com.tripplanner.TripPlanner.model.User;
import com.tripplanner.TripPlanner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Register new user
    public User registerUser(String username, String email, String password, String gender) {
        // Check if username already exists
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Encrypt password
        user.setGender(Gender.valueOf(gender.toUpperCase()));

        return userRepository.save(user);
    }

    // Authenticate user (login)
    public User authenticateUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }

        User user = userOpt.get();

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return user;
    }

    // Find user by ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Find user by username
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
