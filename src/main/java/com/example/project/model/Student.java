package com.example.project.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("Student")
@Getter @Setter @NoArgsConstructor
public class Student extends User {

    public Student(String name, String email) {
        super(name, email);
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
