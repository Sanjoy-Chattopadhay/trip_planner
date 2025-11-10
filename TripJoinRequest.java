package com.tripplanner.TripPlanner.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "trip_join_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripJoinRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(length = 500)
    private String message; // Optional message from requester

    @Column(length = 500)
    private String rejectReason; // Reason for rejection (if denied)

    @CreationTimestamp
    private LocalDateTime requestedAt;

    private LocalDateTime respondedAt; // When approved/denied
}

