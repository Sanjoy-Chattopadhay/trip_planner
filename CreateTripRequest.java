package com.tripplanner.TripPlanner.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateTripRequest {

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotNull(message = "Budget is required")
    @Positive(message = "Budget must be positive")
    private Double budget;

    @NotNull(message = "Going date is required")
    @FutureOrPresent(message = "Going date must be today or future")
    private LocalDate goingDate;

    @NotNull(message = "Comeback date is required")
    private LocalDate comebackDate;

    @Min(value = 0, message = "Max male participants cannot be negative")
    private Integer maxMaleParticipants;

    @Min(value = 0, message = "Max female participants cannot be negative")
    private Integer maxFemaleParticipants;

    @NotNull(message = "Allow female field is required")
    private Boolean allowFemale;
}

