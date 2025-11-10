package com.tripplanner.TripPlanner.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Entity
@Table(name = "trips")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(nullable = false)
    private String destination;

    private Double budget;

    @Column(nullable = false)
    private LocalDate goingDate;

    @Column(nullable = false)
    private LocalDate comebackDate;

    private Integer durationDays;

    private Integer maxMaleParticipants;

    private Integer maxFemaleParticipants;

    @Column(nullable = false)
    private Boolean allowFemale;

    @Enumerated(EnumType.STRING)
    private TripStatus status = TripStatus.OPEN;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<TripParticipant> participants;

    // Auto-calculate duration before saving
    @PrePersist
    @PreUpdate
    public void calculateDuration() {
        if (goingDate != null && comebackDate != null) {
            this.durationDays = (int) ChronoUnit.DAYS.between(goingDate, comebackDate) + 1;
        }
    }
}

