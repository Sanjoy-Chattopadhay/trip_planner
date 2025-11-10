package com.tripplanner.TripPlanner.controller;

import com.tripplanner.TripPlanner.dto.*;
import com.tripplanner.TripPlanner.model.User;
import com.tripplanner.TripPlanner.service.TripService;
import com.tripplanner.TripPlanner.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TripController {

    private final TripService tripService;
    private final UserService userService;

    // Create new trip: pass credentials in body
    @PostMapping("/create")
    public ResponseEntity<?> createTrip(@Valid @RequestBody CreateTripRequest request,
                                        @RequestParam("email") String email,
                                        @RequestParam("password") String password) {
        try {
            User user = userService.authenticateUser(email, password);
            TripResponse trip = tripService.createTrip(request, user.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(trip);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // Get all open trips
    @GetMapping("/open")
    public ResponseEntity<List<TripResponse>> getAllOpenTrips() {
        return ResponseEntity.ok(tripService.getAllOpenTrips());
    }

    // All other endpoints: also accept email + password, and use
    //   User user = userService.authenticateUser(email, password);
    // same pattern as above for protected actions
}
