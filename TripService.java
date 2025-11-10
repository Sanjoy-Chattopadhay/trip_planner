package com.tripplanner.TripPlanner.service;

import com.tripplanner.TripPlanner.dto.CreateTripRequest;
import com.tripplanner.TripPlanner.dto.JoinRequestResponse;
import com.tripplanner.TripPlanner.dto.TripResponse;
import com.tripplanner.TripPlanner.model.*;
import com.tripplanner.TripPlanner.repository.TripJoinRequestRepository;
import com.tripplanner.TripPlanner.repository.TripParticipantRepository;
import com.tripplanner.TripPlanner.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final TripParticipantRepository participantRepository;
    private final TripJoinRequestRepository joinRequestRepository;
    private final UserService userService;

    // Create new trip
    @Transactional
    public TripResponse createTrip(CreateTripRequest request, Long creatorId) {
        User creator = userService.getUserById(creatorId);

        Trip trip = new Trip();
        trip.setCreator(creator);
        trip.setDestination(request.getDestination());
        trip.setBudget(request.getBudget());
        trip.setGoingDate(request.getGoingDate());
        trip.setComebackDate(request.getComebackDate());
        trip.setMaxMaleParticipants(request.getMaxMaleParticipants());
        trip.setMaxFemaleParticipants(request.getMaxFemaleParticipants());
        trip.setAllowFemale(request.getAllowFemale());
        trip.setStatus(TripStatus.OPEN);

        // Duration is auto-calculated by @PrePersist in Trip entity

        Trip savedTrip = tripRepository.save(trip);

        // Auto-add creator as first participant
        TripParticipant creatorParticipant = new TripParticipant();
        creatorParticipant.setTrip(savedTrip);
        creatorParticipant.setUser(creator);
        participantRepository.save(creatorParticipant);

        return convertToResponse(savedTrip);
    }

    // Get all open trips (available to join)
    public List<TripResponse> getAllOpenTrips() {
        return tripRepository.findByStatusOrderByCreatedAtDesc(TripStatus.OPEN)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get trips created by a user
    public List<TripResponse> getTripsCreatedByUser(Long userId) {
        return tripRepository.findByCreatorId(userId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get trips joined by a user
    public List<TripResponse> getTripsJoinedByUser(Long userId) {
        List<TripParticipant> participants = participantRepository.findByUserId(userId);
        return participants.stream()
                .map(TripParticipant::getTrip)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Request to join a trip
    @Transactional
    public String requestToJoinTrip(Long tripId, Long userId, String message) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        User user = userService.getUserById(userId);

        // Check if user is the trip creator
        if (trip.getCreator().getId().equals(userId)) {
            throw new RuntimeException("You cannot join your own trip");
        }

        // Check if already has a pending or approved request
        boolean hasExistingRequest = joinRequestRepository.existsByTripIdAndUserIdAndStatusIn(
                tripId, userId, Arrays.asList(RequestStatus.PENDING, RequestStatus.APPROVED)
        );

        if (hasExistingRequest) {
            throw new RuntimeException("You already have a pending or approved request for this trip");
        }

        // Check if trip is open
        if (trip.getStatus() != TripStatus.OPEN) {
            throw new RuntimeException("This trip is not accepting requests");
        }

        // Check gender restrictions
        if (!trip.getAllowFemale() && user.getGender() == Gender.FEMALE) {
            throw new RuntimeException("This trip does not allow female participants");
        }

        // Create join request
        TripJoinRequest request = new TripJoinRequest();
        request.setTrip(trip);
        request.setUser(user);
        request.setMessage(message);
        request.setStatus(RequestStatus.PENDING);

        joinRequestRepository.save(request);

        return "Join request sent successfully. Waiting for creator's approval.";
    }

    // Get all pending requests for a trip (for creator to review)
    public List<JoinRequestResponse> getPendingRequestsForTrip(Long tripId, Long creatorId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        // Verify the user is the trip creator
        if (!trip.getCreator().getId().equals(creatorId)) {
            throw new RuntimeException("Only the trip creator can view join requests");
        }

        List<TripJoinRequest> requests = joinRequestRepository.findByTripIdAndStatus(
                tripId, RequestStatus.PENDING
        );

        return requests.stream()
                .map(this::convertRequestToResponse)
                .collect(Collectors.toList());
    }

    // Approve a join request
    @Transactional
    public String approveJoinRequest(Long requestId, Long creatorId) {
        TripJoinRequest request = joinRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        // Verify the user is the trip creator
        if (!request.getTrip().getCreator().getId().equals(creatorId)) {
            throw new RuntimeException("Only the trip creator can approve requests");
        }

        // Check if already processed
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("This request has already been processed");
        }

        Trip trip = request.getTrip();

        // Check capacity
        int currentParticipants = participantRepository.findByTripId(trip.getId()).size();
        int maxCapacity = (trip.getMaxMaleParticipants() != null ? trip.getMaxMaleParticipants() : 0) +
                (trip.getMaxFemaleParticipants() != null ? trip.getMaxFemaleParticipants() : 0);

        if (currentParticipants >= maxCapacity) {
            throw new RuntimeException("Trip is already full");
        }

        // Approve request
        request.setStatus(RequestStatus.APPROVED);
        request.setRespondedAt(LocalDateTime.now());
        joinRequestRepository.save(request);

        // Add user to trip participants
        TripParticipant participant = new TripParticipant();
        participant.setTrip(trip);
        participant.setUser(request.getUser());
        participantRepository.save(participant);

        // Check if trip should be closed
        if (currentParticipants + 1 >= maxCapacity) {
            trip.setStatus(TripStatus.CLOSED);
            tripRepository.save(trip);
        }

        return "Request approved successfully";
    }

    // Deny a join request
    @Transactional
    public String denyJoinRequest(Long requestId, Long creatorId, String rejectReason) {
        TripJoinRequest request = joinRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        // Verify the user is the trip creator
        if (!request.getTrip().getCreator().getId().equals(creatorId)) {
            throw new RuntimeException("Only the trip creator can deny requests");
        }

        // Check if already processed
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("This request has already been processed");
        }

        // Deny request
        request.setStatus(RequestStatus.DENIED);
        request.setRejectReason(rejectReason);
        request.setRespondedAt(LocalDateTime.now());
        joinRequestRepository.save(request);

        return "Request denied";
    }

    // Get user's own join requests (to see status)
    public List<JoinRequestResponse> getMyJoinRequests(Long userId) {
        List<TripJoinRequest> requests = joinRequestRepository.findByUserId(userId);

        return requests.stream()
                .map(this::convertRequestToResponse)
                .collect(Collectors.toList());
    }

    // Helper method to convert Trip entity to TripResponse DTO
    private TripResponse convertToResponse(Trip trip) {
        int participantCount = participantRepository.findByTripId(trip.getId()).size();

        return new TripResponse(
                trip.getId(),
                trip.getCreator().getUsername(),
                trip.getDestination(),
                trip.getBudget(),
                trip.getGoingDate(),
                trip.getComebackDate(),
                trip.getDurationDays(),
                trip.getMaxMaleParticipants(),
                trip.getMaxFemaleParticipants(),
                trip.getAllowFemale(),
                trip.getStatus(),
                participantCount,
                trip.getCreatedAt()
        );
    }

    // Helper method to convert TripJoinRequest entity to JoinRequestResponse DTO
    private JoinRequestResponse convertRequestToResponse(TripJoinRequest request) {
        return new JoinRequestResponse(
                request.getId(),
                request.getTrip().getId(),
                request.getTrip().getDestination(),
                request.getUser().getUsername(),
                request.getUser().getEmail(),
                request.getUser().getGender().toString(),
                request.getMessage(),
                request.getStatus(),
                request.getRejectReason(),
                request.getRequestedAt(),
                request.getRespondedAt()
        );
    }
}
