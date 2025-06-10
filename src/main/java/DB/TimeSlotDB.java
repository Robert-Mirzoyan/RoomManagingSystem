package DB;

import Model.TimeSlot;
import Util.JdbcUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class TimeSlotDB {

    private static final Function<ResultSet, TimeSlot> TIME_SLOT_MAPPER = rs -> {
        try {
            int id = rs.getInt("id");
            LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
            LocalDateTime endTime = rs.getTimestamp("end_time").toLocalDateTime();
            return new TimeSlot(id, startTime, endTime);
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping time slot", e);
        }
    };

    public void save(TimeSlot timeSlot) {
        LocalDateTime start = timeSlot.getStartTime().withNano(0);
        LocalDateTime end = timeSlot.getEndTime().withNano(0);

        String selectQuery = "SELECT * FROM timeslot WHERE start_time = ? AND end_time = ?";
        TimeSlot existing = JdbcUtil.findOne(selectQuery, TIME_SLOT_MAPPER, Timestamp.valueOf(start), Timestamp.valueOf(end));

        if (existing == null) {
            String insertQuery = "INSERT INTO timeslot (start_time, end_time) VALUES (?, ?)";
            JdbcUtil.execute(insertQuery, Timestamp.valueOf(start), Timestamp.valueOf(end));
        }
    }

    public TimeSlot findById(int id) {
        String query = "SELECT * FROM timeslot WHERE id = ?";
        return JdbcUtil.findOne(query, TIME_SLOT_MAPPER, id);
    }

    public int getId(TimeSlot timeSlot) {
        LocalDateTime start = timeSlot.getStartTime().withNano(0);
        LocalDateTime end = timeSlot.getEndTime().withNano(0);
        String query = "SELECT * FROM timeslot WHERE start_time = ? AND end_time = ?";
        return Objects.requireNonNull(JdbcUtil.findOne(query, TIME_SLOT_MAPPER, Timestamp.valueOf(start), Timestamp.valueOf(end))).getId();
    }

    @SuppressWarnings("unused")
    public List<TimeSlot> findAll() {
        String query = "SELECT * FROM timeslot";
        return JdbcUtil.findMany(query, TIME_SLOT_MAPPER);
    }

    @SuppressWarnings("unused")
    public void deleteById(int id) {
        String query = "DELETE FROM timeslot WHERE id = ?";
        JdbcUtil.execute(query, id);
    }
}

