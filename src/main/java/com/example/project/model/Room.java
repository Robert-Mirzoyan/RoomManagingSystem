package com.example.project.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Room")
@Getter @Setter @NoArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String type;
    private int capacity;

    @OneToMany(mappedBy = "room",  cascade = CascadeType.ALL,  orphanRemoval = true,  fetch = FetchType.EAGER)
    List<Booking> bookings;

    public Room(String name, String type, int capacity) {
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.bookings = new ArrayList<>();
    }

    public void updateDetails(String name, String type, int capacity) {
        this.name = name;
        this.type = type;
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return name + " (" + type + ", " + capacity + " capacity), ID: " + id;
    }
}
