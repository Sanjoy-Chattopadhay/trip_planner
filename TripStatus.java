package com.tripplanner.TripPlanner.model;

public enum TripStatus {
    OPEN,       // Trip is accepting participants
    CLOSED,     // Trip is full, not accepting more
    COMPLETED,  // Trip has finished
    CANCELLED   // Trip was cancelled
}

