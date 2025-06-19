package com.example.project.Model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String type;
    private int capacity;

    @OneToMany(mappedBy = "room",  cascade = CascadeType.ALL,  orphanRemoval = true,  fetch = FetchType.EAGER)
    List<Booking> bookings;

    public Room(int id, String name, String type, int capacity) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.capacity = capacity;
    }

    public Room() {
    }

    public void updateDetails(String name, String type, int capacity) {
        this.name = name;
        this.type = type;
        this.capacity = capacity;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setId(int id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings("unused")
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    @SuppressWarnings("unused")
    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
}
