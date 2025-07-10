package com.example.project.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Student student;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "booking_participant",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<Student> participants = new HashSet<>();


    public Booking(Room room, LocalDateTime startTime, LocalDateTime endTime, Student student, Status status) {
        this.room = room;
        this.startTime = startTime;
        this.endTime = endTime;
        this.student = student;
        this.status = status;
    }

    public boolean overlaps(LocalDateTime startTime, LocalDateTime endTime) {
        return this.startTime.isBefore(endTime) && this.endTime.isAfter(startTime);
    }

    public boolean isEditable() {
        return status != Status.REJECTED && status != Status.CANCELLED;
    }

    @Override
    public String toString() {
        return String.format(
                "Booking ID: %d, Room: %s (ID: %d), Booking status: %s, Requester ID: %d, Time Slot: %s - %s",
                this.getId(),
                this.getRoom().getName(),
                this.getRoom().getId(),
                this.getStatus(),
                this.getStudent().getId(),
                this.getStartTime(),
                this.getEndTime()
        );
    }
}
