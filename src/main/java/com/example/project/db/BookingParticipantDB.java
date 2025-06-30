package com.example.project.db;

import com.example.project.model.Student;
import com.example.project.util.JdbcUtil;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

@Repository
@SuppressWarnings("unused")
public class BookingParticipantDB {

    private static final Function<ResultSet, Student> STUDENT_MAPPER = rs -> {
        try {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            String email = rs.getString("email");
            return new Student(name, email);
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping student", e);
        }
    };

    public List<Student> findParticipantsByBookingId(int bookingId) {
        String query = """
            SELECT u.* FROM booking_participant bp
            JOIN "user" u ON bp.user_id = u.id
            WHERE bp.booking_id = ? AND u.role = 'Student'
            """;
        return JdbcUtil.findMany(query, STUDENT_MAPPER, bookingId);
    }

    public void addParticipantToBooking(int bookingId, int studentId) {
        String insert = "INSERT INTO booking_participant (booking_id, user_id) VALUES (?, ?)";
        JdbcUtil.execute(insert, bookingId, studentId);
    }

    public void removeParticipantFromBooking(int bookingId, int studentId) {
        String delete = "DELETE FROM booking_participant WHERE booking_id = ? AND user_id = ?";
        JdbcUtil.execute(delete, bookingId, studentId);
    }
}
