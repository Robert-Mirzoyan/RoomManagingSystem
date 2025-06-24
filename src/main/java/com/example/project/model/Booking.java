package com.example.project.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
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


    public Booking(int id, Room room, LocalDateTime startTime, LocalDateTime endTime, Student student, Status status) {
        this.id = id;
        this.room = room;
        this.startTime = startTime;
        this.endTime = endTime;
        this.student = student;
        this.status = status;
    }

    public Booking() {
    }

    public boolean overlaps(LocalDateTime startTime, LocalDateTime endTime) {
        return this.startTime.isBefore(endTime) && this.endTime.isAfter(startTime);
    }

    public boolean isEditable() {
        return status != Status.REJECTED && status != Status.CANCELLED;
    }

    public int getId() {
        return id;
    }

    public Room getRoom() {
        return room;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Student getStudent() {
        return student;
    }

    public Status getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    public void setRoom(Room room) {
        this.room = room;
    }

    @SuppressWarnings("unused")
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    @SuppressWarnings("unused")
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @SuppressWarnings("unused")
    public void setStudent(Student student) {
        this.student = student;
    }

    @SuppressWarnings("unused")
    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<Student> getParticipants() {
        return participants;
    }

    @SuppressWarnings("unused")
    public void setParticipants(Set<Student> participants) {
        this.participants = participants;
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
