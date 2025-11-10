package com.tripplanner.TripPlanner.repository;

import com.tripplanner.TripPlanner.model.RequestStatus;
import com.tripplanner.TripPlanner.model.TripJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripJoinRequestRepository extends JpaRepository<TripJoinRequest, Long> {

    // Find all requests for a specific trip
    List<TripJoinRequest> findByTripId(Long tripId);

    // Find all pending requests for a trip (for creator to review)
    List<TripJoinRequest> findByTripIdAndStatus(Long tripId, RequestStatus status);

    // Find all requests by a user
    List<TripJoinRequest> findByUserId(Long userId);

    // Check if user already has a pending/approved request for this trip
    boolean existsByTripIdAndUserIdAndStatusIn(Long tripId, Long userId, List<RequestStatus> statuses);

    // Find specific request
    Optional<TripJoinRequest> findByTripIdAndUserId(Long tripId, Long userId);
}

