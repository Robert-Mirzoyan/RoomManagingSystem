package com.example.project.repository;

import com.example.project.model.Booking;
import com.example.project.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Meta;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByStudentIdOrderByIdAsc(int studentId);

    List<Booking> findByRoomId(int roomId);

    @Meta(comment = "Updates time slots of given booking.")
    @Modifying
    @Query("UPDATE Booking b SET b.startTime = :start, b.endTime = :end WHERE b.id = :id")
    void updateTimeSlot(@Param("id") int id, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Meta(comment = "Updates status of given booking.")
    @Modifying
    @Query("UPDATE Booking b SET b.status = :status WHERE b.id = :id")
    void updateStatus(@Param("id") int id, @Param("status") Status status);
}
