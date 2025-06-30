package com.example.project.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FacultyManager")
public class FacultyManager extends User {
    public FacultyManager(String name, String email) {
        super(name, email);
    }

    public FacultyManager() {
        super();
    }

    @Override
    public String getRole() {
        return "FacultyManager";
    }

    @Override
    public String toString() {
        return "FacultyManager{id=" + getId() + ", name='" + getName() + "', email='" + getEmail() + "'}";
    }
}
