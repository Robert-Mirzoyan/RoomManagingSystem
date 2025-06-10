package Service;

import DB.BookingDB;
import DB.BookingParticipantDB;
import DB.UserDB;
import Model.*;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.*;
import static Model.Status.*;

public class BookingService {
    private final BookingDB bookingDB = new BookingDB();
    private final BookingParticipantDB bookingParticipantDB = new BookingParticipantDB();
    private final UserDB userDB = new UserDB();

    private LocalTime openTime = LocalTime.of(8, 0);
    private LocalTime closeTime = LocalTime.of(22, 0);

    public boolean requestBooking(Room room, TimeSlot slot, Student student) {

        if (isNotWithinAllowedHours(slot)) {
            System.out.println("Booking must be between " + openTime + " and " + closeTime + "." );
            return false;
        }

        for (Booking existing : bookingDB.findAll()) {
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

        Booking booking = new Booking(0, room, slot, student, PENDING);

        promptToAddParticipants(booking, student);

        try {
            bookingDB.save(booking);
        } catch (SQLException e) {
            System.out.println("Error saving booking: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean editBooking(int bookingId, TimeSlot newSlot, Student student) {
        if (!student.getEmail().equals(bookingDB.findById(bookingId).getStudent().getEmail())) return false;

        if (isNotWithinAllowedHours(newSlot)) {
            System.out.println("Edited time must be between " + openTime + " and " + closeTime + ".");
            return false;
        }

        Booking booking = bookingDB.findById(bookingId);
        if (booking == null || !booking.isEditable()) return false;

        for (Booking existing : bookingDB.findAll()) {
            if (existing.getId() != bookingId &&
                    existing.getRoom().getId() == booking.getRoom().getId() &&
                    existing.getTimeSlot().overlaps(newSlot) &&
                    existing.getStatus() != Status.REJECTED &&
                    existing.getStatus() != Status.CANCELLED) {
                System.out.println("Conflict with an existing booking.");
                return false;
            }
        }

        if (!booking.getTimeSlot().equals(newSlot)){
            bookingDB.updateTimeSlot(booking, newSlot);
        }
        bookingDB.updateStatus(booking,"PENDING");

        promptToRemoveParticipants(booking, student);
        promptToAddParticipants(booking, student);
        return true;
    }

    private boolean isNotWithinAllowedHours(TimeSlot slot) {
        LocalTime start = slot.getStartTime().toLocalTime();
        LocalTime end = slot.getEndTime().toLocalTime();

        return start.isBefore(openTime) || end.isAfter(closeTime) ||
                !slot.getStartTime().toLocalDate().equals(slot.getEndTime().toLocalDate());
    }

    private void promptToAddParticipants(Booking booking, Student currentStudent) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nWho would you like to add as participants?");
        System.out.println("Here are the available students:");

        System.out.printf("(You)ID: %d | Name: %s | Email: %s%n", booking.getStudent().getId(), booking.getStudent().getName(), booking.getStudent().getEmail());
        for (Student s : userDB.findAllStudents()) {
            if (s.getId() != currentStudent.getId()) {
                System.out.printf("     ID: %d | Name: %s | Email: %s%n", s.getId(), s.getName(), s.getEmail());
            }
        }

        System.out.println("\nEnter the IDs of the students you want to add (separated by spaces), or enter 0 to skip:");
        String input = scanner.nextLine().trim();

        if (input.equals("0")) {
            System.out.println("No participants were added.");
            return;
        }

        String[] idStrings = input.split("\\s+");
        for (String idText : idStrings) {
            try {
                int id = Integer.parseInt(idText);
                if (id == currentStudent.getId()) {
                    System.out.println("You can't add yourself as a participant.");
                    continue;
                }

                Optional<Student> match = userDB.findAllStudents().stream()
                        .filter(s -> s.getId() == id)
                        .findFirst();
                if (match.isPresent()) {
                    Student participant = match.get();
                    if (bookingParticipantDB.findParticipantsByBookingId(booking.getId()).contains(participant)) {
                        System.out.println(participant.getName() + " is already in the participant list.");
                    } else {
                        bookingParticipantDB.addParticipantToBooking(booking.getId(), participant.getId());
                        System.out.println("Added: " + participant.getName());
                    }
                } else {
                    System.out.println("No student found with ID: " + id);
                }

            } catch (NumberFormatException e) {
                System.out.println("'" + idText + "' is not a valid number.");
            }
        }
    }

    private void promptToRemoveParticipants(Booking booking, Student currentStudent) {
        List<Student> participants = bookingParticipantDB.findParticipantsByBookingId(booking.getId());
        if (participants.isEmpty()) {
            System.out.println("\nThere are no participants to remove.");
            return;
        }

        System.out.println("\nHere are the current participants:");
        System.out.printf("(You)ID: %d | Name: %s | Email: %s%n", booking.getStudent().getId(), booking.getStudent().getName(), booking.getStudent().getEmail());
        for (Student s : participants) {
            System.out.printf("     ID: %d | Name: %s | Email: %s%n", s.getId(), s.getName(), s.getEmail());
        }

        System.out.println("\nEnter the IDs of the participants you want to remove (separated by spaces), or enter 0 to skip:");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();

        if (input.equals("0")) {
            System.out.println("No participants were removed.");
            return;
        }

        String[] idStrings = input.split("\\s+");
        for (String idText : idStrings) {
            try {
                int id = Integer.parseInt(idText);
                if (id == currentStudent.getId()) {
                    System.out.println("You can't remove yourself.");
                    continue;
                }

                boolean removed = participants.stream().anyMatch(student -> student.getId() == id);

                if (removed) {
                    bookingParticipantDB.removeParticipantFromBooking(booking.getId(), id);
                    System.out.println("Removed participant with ID: " + id);
                } else {
                    System.out.println("No participant found with ID: " + id);
                }
            } catch (NumberFormatException e) {
                System.out.println("'" + idText + "' is not a valid number.");
            }
        }
    }

    public boolean cancelBooking(int bookingId, Student student) {
        if (!student.getEmail().equals(bookingDB.findById(bookingId).getStudent().getEmail())) return false;

        Booking booking = bookingDB.findById(bookingId);
        if (booking == null) return false;
        bookingDB.updateStatus(booking,"CANCELLED");
        return true;
    }

    public boolean approveBooking(int bookingId, FacultyManager manager) {
        if (!manager.getRole().equals("FacultyManager")) return false;

        Booking booking = bookingDB.findById(bookingId);
        if (booking == null || booking.getStatus() != PENDING) return false;
        bookingDB.updateStatus(booking,"APPROVED");
        return true;
    }

    public boolean rejectBooking(int bookingId, FacultyManager manager) {
        if (!manager.getRole().equals("FacultyManager")) return false;

        Booking booking = bookingDB.findById(bookingId);
        if (booking == null || booking.getStatus() != PENDING) return false;
        bookingDB.updateStatus(booking,"REJECTED");
        return true;
    }

    public List<Booking> filterBookings(Integer roomId) {
        return bookingDB.findByFilters(roomId);
    }

    public List<Booking> getAllBookings() {
        return bookingDB.findAll();
    }

    public List<Booking> getBookingsByStudent(int id){
        return bookingDB.findByStudentId(id);
    }

    public List<Student> getParticipants(int bookingId) {
        return bookingParticipantDB.findParticipantsByBookingId(bookingId);
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