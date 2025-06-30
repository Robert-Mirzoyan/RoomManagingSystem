package com.example.project.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "\"user\"")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role")
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    protected String name;
    protected String email;

    @ManyToMany(mappedBy = "participants")
    private Set<Booking> bookings = new HashSet<>();

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings("unused")
    public void setEmail(String email) {
        this.email = email;
    }

    public abstract String getRole();

    @SuppressWarnings("unused")
    public Set<Booking> getBookings() {
        return bookings;
    }

    @SuppressWarnings("unused")
    public void setBookings(Set<Booking> bookings) {
        this.bookings = bookings;
    }
}
