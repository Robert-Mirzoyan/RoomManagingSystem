package Service;

import Model.*;

import java.time.LocalTime;
import java.util.*;

import static Model.Status.PENDING;
import static Model.Status.REJECTED;
import static Model.Status.CANCELLED;

public class BookingService {
    private final Map<Integer, Booking> bookings = new HashMap<>();
    private int nextBookingId = 1;
    private LocalTime openTime = LocalTime.of(8, 0);
    private LocalTime closeTime = LocalTime.of(22, 0);

    public boolean requestBooking(Room room, TimeSlot slot, Student student) {

        if (isWithinAllowedHours(slot)) {
            System.out.println("Booking must be between " + openTime + " and " + closeTime + "." );
            return false;
        }

        for (Booking existing : bookings.values()) {
            if (existing.getRoom().getId() == room.getId() &&
                    existing.getTimeSlot().overlaps(slot) &&
                    existing.getStatus() != REJECTED &&
                    existing.getStatus() != CANCELLED) {
                System.out.println("Conflict with an existing booking.");
                return false;
            }
        }

        if (room == null) {
            System.out.println("Non existing room.");
            return false;
        }

        Booking booking = new Booking(nextBookingId++, room, slot, student, PENDING);
        bookings.put(booking.getId(), booking);
        return true;
    }

    public boolean editBooking(int bookingId, TimeSlot newSlot, Student student) {
        if (!student.getEmail().equals(bookings.get(bookingId).getStudent().getEmail())) return false;

        if (isWithinAllowedHours(newSlot)) {
            System.out.println("Edited time must be between " + openTime + " and " + closeTime + ".");
            return false;
        }

        Booking booking = bookings.get(bookingId);
        if (booking == null || !booking.isEditable()) return false;

        for (Booking existing : bookings.values()) {
            if (existing.getId() != bookingId &&
                    existing.getTimeSlot().overlaps(newSlot) &&
                    existing.getStatus() != Status.REJECTED &&
                    existing.getStatus() != Status.CANCELLED) {
                return false;
            }
        }

        booking = new Booking(booking.getId(), booking.getRoom(), newSlot, booking.getStudent(), PENDING);
        bookings.put(bookingId, booking);
        return true;
    }

    private boolean isWithinAllowedHours(TimeSlot slot) {
        LocalTime start = slot.getStartTime().toLocalTime();
        LocalTime end = slot.getEndTime().toLocalTime();

        return start.isBefore(openTime) || end.isAfter(closeTime) ||
                !slot.getStartTime().toLocalDate().equals(slot.getEndTime().toLocalDate());
    }


    public boolean cancelBooking(int bookingId, Student student) {
        if (!student.getEmail().equals(bookings.get(bookingId).getStudent().getEmail())) return false;

        Booking booking = bookings.get(bookingId);
        if (booking == null) return false;
        booking.cancel();
        return true;
    }

    public boolean approveBooking(int bookingId, FacultyManager manager) {
        if (!manager.getRole().equals("FacultyManager")) return false;
        Booking booking = bookings.get(bookingId);
        if (booking == null || booking.getStatus() != PENDING) return false;
        booking.approve();
        return true;
    }

    public boolean rejectBooking(int bookingId, FacultyManager manager) {
        if (!manager.getRole().equals("FacultyManager")) return false;

        Booking booking = bookings.get(bookingId);
        if (booking == null || booking.getStatus() != PENDING) return false;
        booking.reject();
        return true;
    }

    public List<Booking> filterBookings(Integer roomId, Integer studentId) {
        List<Booking> results = new ArrayList<>();
        for (Booking booking : bookings.values()) {
            boolean match = true;
            if (roomId != null && booking.getRoom().getId() != roomId) match = false;
            if (studentId != null && booking.getStudent().getId() != studentId) match = false;
            if (match) results.add(booking);
        }
        return results;
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings.values());
    }

    public List<Booking> getBookingsByStudent(Student student){
        List<Booking> result = new ArrayList<>();
        for (Booking booking : bookings.values()){
            if (booking.getStudent().getId() == student.getId()){
                result.add(booking);
            }
        }
        return result;
    }

    public LocalTime getOpenTime() {
        return openTime;
    }

    public LocalTime getCloseTime() {
        return closeTime;
    }

    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    public void setCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
    }
}