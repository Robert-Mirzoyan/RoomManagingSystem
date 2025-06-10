package DB;

import Model.*;
import Util.JdbcUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

public class BookingDB {
    private final TimeSlotDB timeSlotDB = new TimeSlotDB();
    private final RoomDB roomDB = new RoomDB();
    private final UserDB userDB = new UserDB();
    private final BookingParticipantDB bookingParticipantDB = new BookingParticipantDB();

    private final Function<ResultSet, Booking> BOOKING_MAPPER = rs -> {
        try {
            int id = rs.getInt("id");
            int roomId = rs.getInt("room_id");
            int timeSlotId = rs.getInt("timeslot_id");
            int studentId = rs.getInt("user_id");
            Status status = Status.valueOf(rs.getString("status"));

            return new Booking(id, roomDB.findById(roomId), timeSlotDB.findById(timeSlotId), userDB.findByStudentId(studentId), status);
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping booking", e);
        }
    };

    public void save(Booking booking) throws SQLException {
        String query = "INSERT INTO booking (user_id, room_id, timeslot_id, status) VALUES (?, ?, ?, ?)";
        timeSlotDB.save(booking.getTimeSlot());
        JdbcUtil.execute(query, booking.getStudent().getId(), booking.getRoom().getId(), timeSlotDB.getId(booking.getTimeSlot()), booking.getStatus().toString());
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

    @SuppressWarnings("unused")
    public void deleteById(int id) {
        String query = "DELETE FROM booking WHERE id = ?";
        JdbcUtil.execute(query, id);
    }

    public void updateTimeSlot(Booking booking, TimeSlot timeSlot) {
        String query = "UPDATE booking SET timeslot_id = ? WHERE id = ?";

        timeSlotDB.save(timeSlot);
        JdbcUtil.execute(query, timeSlotDB.getId(timeSlot), booking.getId());
    }

    public void updateStatus(Booking booking, String status) {
        String query = "UPDATE booking SET status = ? WHERE id = ?";
        JdbcUtil.execute(query, status, booking.getId());
    }

    public List<Booking> findByFilters(Integer roomId) {
        String query = "SELECT * FROM booking WHERE room_id = ?";
        return JdbcUtil.findMany(query, BOOKING_MAPPER, roomId);
    }

    @SuppressWarnings("unused")
    private List<Student> findParticipants(int bookingId) {
        return bookingParticipantDB.findParticipantsByBookingId(bookingId);
    }
}
