package com.example.project.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Admin")
public class Admin extends User {
    public Admin(int id, String name, String email) {
        super(id, name, email);
    }

    public Admin() {
        super();
    }

    @Override
    public String getRole() {
        return "Admin";
    }

    @Override
    public String toString() {
        return "Admin{id=" + getId() + ", name='" + getName() + "', email='" + getEmail() + "'}";
    }
}
