package com.tripplanner.TripPlanner.dto;


import com.tripplanner.TripPlanner.model.TripStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripResponse {
    private Long id;
    private String creatorUsername;
    private String destination;
    private Double budget;
    private LocalDate goingDate;
    private LocalDate comebackDate;
    private Integer durationDays;
    private Integer maxMaleParticipants;
    private Integer maxFemaleParticipants;
    private Boolean allowFemale;
    private TripStatus status;
    private Integer currentParticipants;
    private LocalDateTime createdAt;
}

