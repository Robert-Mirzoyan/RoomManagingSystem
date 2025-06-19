import com.example.project.model.Admin;
import com.example.project.model.FacultyManager;
import com.example.project.model.Student;
import com.example.project.model.User;
import com.example.project.util.JdbcUtil;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class BookingTest {
    @Test
    void testFindOneUserByEmail() {
        String email = "robert_mirzoyan@edu.aua.am";
        String query = "SELECT * FROM \"user\" WHERE email = ?";

        Function<ResultSet, User> mapper = rs -> {
            try {
                String role = rs.getString("role");
                return switch (role) {
                    case "Student" -> new Student(rs.getInt("id"), rs.getString("name"), rs.getString("email"));
                    case "Admin" -> new Admin(rs.getInt("id"), rs.getString("name"), rs.getString("email"));
                    case "FacultyManager" -> new FacultyManager(rs.getInt("id"), rs.getString("name"), rs.getString("email"));
                    default -> throw new IllegalArgumentException("Unknown role: " + role);
                };
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };

        User user = JdbcUtil.findOne(query, mapper, email);

        assertNotNull(user);
        assertEquals("Robert", user.getName());
        assertEquals("robert_mirzoyan@edu.aua.am", user.getEmail());
        assertInstanceOf(Student.class, user);
    }

    @Test
    void testFindManyUserByRole() {
        String role = "Student";
        String query = "SELECT * FROM \"user\" WHERE role = ?";

        Function<ResultSet, User> mapper = rs -> {
            try {
                if (role.equals(rs.getString("role"))) {
                    return new Student(rs.getInt("id"), rs.getString("name"), rs.getString("email"));
                } else {
                    return null;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };

        List<User> users = JdbcUtil.findMany(query, mapper, role);

        assertNotNull(users);
        assertEquals("Student", users.get(0).getRole());
        assertEquals("Student", users.get(users.size()-1).getRole());
        assertFalse(users.isEmpty());
    }

    @Test
    void testExecuteInsertUser() {
        int userId = 198;
        String insertSql = "INSERT INTO \"user\" (id, name, email, role) VALUES (?, ?, ?, ?)";
        String deleteSql = "DELETE FROM \"user\" WHERE id = ?";

        try {
            JdbcUtil.execute(insertSql, userId, "Test User", "test_user@edu.aua.am", "Student");

            // Confirm it was inserted
            String query = "SELECT * FROM \"user\" WHERE id = ?";
            User user = JdbcUtil.findOne(query, rs ->
            {
                try {
                    return new Student(rs.getInt("id"), rs.getString("name"), rs.getString("email"));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }, userId);
            assertNotNull(user);
            assertEquals("Test User", user.getName());
        } finally {
            JdbcUtil.execute(deleteSql, userId);
        }
    }

    @Test
    void testExecuteInsertUserWithConsumer() {
        int userId = 199;
        String insertSql = "INSERT INTO \"user\" (id, name, email, role) VALUES (?, ?, ?, ?)";
        String deleteSql = "DELETE FROM \"user\" WHERE id = ?";

        try {
            JdbcUtil.execute(insertSql, stmt -> {
                try {
                    stmt.setInt(1, userId);
                    stmt.setString(2, "Consumer User");
                    stmt.setString(3, "consumer_user@edu.aua.am");
                    stmt.setString(4, "Student");
                } catch (SQLException e) {
                    fail("Failed to set parameters: " + e.getMessage());
                }
            });

            User user = JdbcUtil.findOne(
                    "SELECT * FROM \"user\" WHERE id = ?",
                    rs -> {
                        try {
                            return new Student(rs.getInt("id"), rs.getString("name"), rs.getString("email"));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    userId
            );

            assertNotNull(user);
            assertEquals("Consumer User", user.getName());
            assertEquals("consumer_user@edu.aua.am", user.getEmail());
        } finally {
            JdbcUtil.execute(deleteSql, userId);
        }
    }
}
