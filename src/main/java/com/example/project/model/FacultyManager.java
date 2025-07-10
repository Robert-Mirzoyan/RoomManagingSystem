package com.example.project.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("FacultyManager")
@Getter @Setter @NoArgsConstructor
public class FacultyManager extends User {
    public FacultyManager(String name, String email) {
        super(name, email);
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
