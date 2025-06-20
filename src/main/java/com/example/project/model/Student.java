package com.example.project.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Student")
public class Student extends User {

    public Student(int id, String name, String email) {
        super(id, name, email);
    }

    public Student() {
        super();
    }

    @Override
    public String getRole() {
        return "Student";
    }

    @Override
    public String toString() {
        return "Student{id=" + getId() + ", name='" + getName() + "', email='" + getEmail() + "'}";
    }
}
