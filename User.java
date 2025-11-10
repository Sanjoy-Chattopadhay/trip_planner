package com.tripplanner.TripPlanner.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // One user can create multiple trips
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    private List<Trip> createdTrips;

    // One user can join multiple trips
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TripParticipant> joinedTrips;
}

