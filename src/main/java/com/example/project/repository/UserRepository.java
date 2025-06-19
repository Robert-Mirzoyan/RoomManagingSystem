package com.example.project.repository;

import com.example.project.model.Student;
import com.example.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

//    @Query("SELECT u FROM \"user\" u WHERE u.role = 'Student'")
    @Query("SELECT u FROM Student u")
    List<Student> findAllStudents();
}
