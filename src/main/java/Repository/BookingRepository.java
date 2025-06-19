package Repository;

import Model.Booking;
import Model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByStudentId(int studentId);

    List<Booking> findByRoomId(int roomId);

    @Modifying
    @Query("UPDATE Booking b SET b.startTime = :start, b.endTime = :end WHERE b.id = :id")
    void updateTimeSlot(@Param("id") int id, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Modifying
    @Query("UPDATE Booking b SET b.status = :status WHERE b.id = :id")
    void updateStatus(@Param("id") int id, @Param("status") Status status);
}
