package DB;

import Model.Booking;
import Model.Status;
import Util.JdbcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Repository
public class BookingDB {
    private RoomDB roomDB;
    private UserDB userDB;

    @Autowired
    public BookingDB(RoomDB roomDB, UserDB userDB) {
        this.roomDB = roomDB;
        this.userDB = userDB;
    }

    private final Function<ResultSet, Booking> BOOKING_MAPPER = rs -> {
        try {
            int id = rs.getInt("id");
            int roomId = rs.getInt("room_id");
            LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
            LocalDateTime endTime = rs.getTimestamp("end_time").toLocalDateTime();
            int studentId = rs.getInt("user_id");
            Status status = Status.valueOf(rs.getString("status"));

            return new Booking(id, roomDB.findById(roomId), startTime, endTime, userDB.findByStudentId(studentId), status);
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping booking", e);
        }
    };

    public void save(Booking booking) throws SQLException {
        String query = "INSERT INTO booking (user_id, room_id, start_time, end_time, status) VALUES (?, ?, ?, ?)";
        JdbcUtil.execute(query, booking.getStudent().getId(), booking.getRoom().getId(), booking.getStartTime(), booking.getEndTime(), booking.getStatus().toString());
    }

    public Booking findById(int id) {
        String query = "SELECT * FROM booking WHERE id = ?";
        return JdbcUtil.findOne(query, BOOKING_MAPPER, id);
    }

    public List<Booking> findByStudentId(int id) {
        String query = "SELECT * FROM booking WHERE user_id = ?";
        return JdbcUtil.findMany(query, BOOKING_MAPPER, id);
    }

    public List<Booking> findAll() {
        String query = "SELECT * FROM booking";
        return JdbcUtil.findMany(query, BOOKING_MAPPER);
    }

    public void deleteById(int id) {
        String query = "DELETE FROM booking WHERE id = ?";
        JdbcUtil.execute(query, id);
    }

    public void updateTimeSlot(Booking booking, LocalDateTime startTime, LocalDateTime endTime) {
        String query = "UPDATE booking SET start_time = ?, end_time = ? WHERE id = ?";
        JdbcUtil.execute(query, Timestamp.valueOf(startTime), Timestamp.valueOf(endTime), booking.getId());
    }

    public void updateStatus(Booking booking, String status) {
        String query = "UPDATE booking SET status = ? WHERE id = ?";
        JdbcUtil.execute(query, status, booking.getId());
    }

    public List<Booking> findByFilters(Integer roomId) {
        String query = "SELECT * FROM booking WHERE room_id = ?";
        return JdbcUtil.findMany(query, BOOKING_MAPPER, roomId);
    }
}
