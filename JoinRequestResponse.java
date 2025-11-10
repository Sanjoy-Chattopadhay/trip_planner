package com.tripplanner.TripPlanner.dto;

import com.tripplanner.TripPlanner.model.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinRequestResponse {
    private Long id;
    private Long tripId;
    private String tripDestination;
    private String requesterUsername;
    private String requesterEmail;
    private String requesterGender;
    private String message;
    private RequestStatus status;
    private String rejectReason;
    private LocalDateTime requestedAt;
    private LocalDateTime respondedAt;
}

