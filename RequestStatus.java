package com.tripplanner.TripPlanner.model;

public enum RequestStatus {
    PENDING,    // Waiting for creator's approval
    APPROVED,   // Creator approved - user added to trip
    DENIED      // Creator rejected the request
}

