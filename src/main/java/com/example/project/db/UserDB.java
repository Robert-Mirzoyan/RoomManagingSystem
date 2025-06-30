package com.example.project.db;

import com.example.project.model.Admin;
import com.example.project.model.FacultyManager;
import com.example.project.model.Student;
import com.example.project.model.User;
import com.example.project.util.JdbcUtil;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Repository
@SuppressWarnings("unused")
public class UserDB {

    private static final Function<ResultSet, User> USER_MAPPER = rs -> {
        try {
            String role = rs.getString("role");
            int id = rs.getInt("id");
            String name = rs.getString("name");
            String email = rs.getString("email");

            return switch (role) {
                case "Student" -> new Student(name, email);
                case "Admin" -> new Admin(name, email);
                case "FacultyManager" -> new FacultyManager(name, email);
                default -> throw new IllegalArgumentException("Unknown role: " + role);
            };
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping user", e);
        }
    };

    public Optional<User> findByEmail(String email) {
        String query = "SELECT * FROM \"user\" WHERE email = ?";
        return Optional.ofNullable(JdbcUtil.findOne(query, USER_MAPPER, email));
    }

    public Student findByStudentId(int id) {
        String query = "SELECT * FROM \"user\" WHERE id = ? AND role = 'Student'";
        User user = JdbcUtil.findOne(query, USER_MAPPER, id);
        if (user instanceof Student student) {
            return student;
        } else {
            return null;
        }
    }

    public List<Student> findAllStudents() {
        String query = "SELECT * FROM \"user\" WHERE role = 'Student'";
        return JdbcUtil.findMany(query, USER_MAPPER).stream()
                .map(user -> (Student) user)
                .toList();
    }

    @SuppressWarnings("unused")
    public void save(User user) {
        String query = "INSERT INTO \"user\" (name, email, role) VALUES (?, ?, ?)";
        JdbcUtil.execute(query, user.getName(), user.getEmail(), user.getRole());
    }
}