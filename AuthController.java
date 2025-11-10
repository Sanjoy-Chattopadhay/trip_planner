package com.tripplanner.TripPlanner.controller;

import com.tripplanner.TripPlanner.dto.LoginRequest;
import com.tripplanner.TripPlanner.dto.RegisterRequest;
import com.tripplanner.TripPlanner.dto.UserResponse;
import com.tripplanner.TripPlanner.model.User;
import com.tripplanner.TripPlanner.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserService userService;

    // Register endpoint
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getGender()
            );

            UserResponse response = new UserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getGender().toString()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Stateless login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = userService.authenticateUser(request.getEmail(), request.getPassword());

            UserResponse response = new UserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getGender().toString()
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }
}
