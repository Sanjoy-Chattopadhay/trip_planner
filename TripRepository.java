package com.tripplanner.TripPlanner.repository;

import com.tripplanner.TripPlanner.model.Trip;
import com.tripplanner.TripPlanner.model.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByCreatorId(Long creatorId);
    List<Trip> findByStatus(TripStatus status);
    List<Trip> findByStatusOrderByCreatedAtDesc(TripStatus status);
}

