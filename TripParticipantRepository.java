package com.tripplanner.TripPlanner.repository;

import com.tripplanner.TripPlanner.model.TripParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripParticipantRepository extends JpaRepository<TripParticipant, Long> {
    List<TripParticipant> findByUserId(Long userId);
    List<TripParticipant> findByTripId(Long tripId);
    boolean existsByTripIdAndUserId(Long tripId, Long userId);
}

