package com.example.project.Service;

import com.example.project.Repository.BookingRepository;
import com.example.project.Repository.UserRepository;
import com.example.project.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static com.example.project.Model.Status.*;

@Service
public class BookingService {
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    private LocalTime openTime = LocalTime.of(8, 0);
    private LocalTime closeTime = LocalTime.of(22, 0);

    @Autowired
    public BookingService(BookingRepository bookingRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    public void requestBooking(Room room, LocalDateTime startTime, LocalDateTime endTime, Student student) {

        if (room == null) {
            System.out.println("Non existing room.\nBooking request failed");
            return;
        }

        if (isNotWithinAllowedHours(startTime, endTime)) {
            System.out.println("Booking must be between " + openTime + " and " + closeTime + ".\nBooking request failed");
            return;
        }

        for (Booking existing : bookingRepository.findAll()) {
            if (existing.getRoom().getId() == room.getId() &&
                    existing.overlaps(startTime, endTime) &&
                    existing.getStatus() != REJECTED &&
                    existing.getStatus() != CANCELLED) {
                System.out.println("Conflict with an existing booking.\nBooking request failed");
                return;
            }
        }

        Booking booking = new Booking(0, room, startTime, endTime, student, PENDING);
        promptToAddParticipants(booking, student.getId());
        bookingRepository.save(booking);
        System.out.println("Booking requested");
    }

    @Transactional
    public void editBooking(int bookingId, LocalDateTime startTime, LocalDateTime endTime, int studentId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);

        if (booking == null || booking.getStudent().getId() != studentId || !booking.isEditable()) {
            System.out.println("Invalid booking id.\nBooking edit request failed");
            return;
        }

        if (isNotWithinAllowedHours(startTime, endTime)) {
            System.out.println("Edited time must be between " + openTime + " and " + closeTime + ".\nBooking edit request failed");
            return;
        }

        for (Booking existing : bookingRepository.findAll()) {
            if (existing.getId() != bookingId &&
                    existing.getRoom().getId() == booking.getRoom().getId() &&
                    existing.overlaps(startTime, endTime) &&
                    existing.getStatus() != REJECTED &&
                    existing.getStatus() != CANCELLED) {
                System.out.println("Conflict with an existing booking.\nBooking edit request failed");
                return;
            }
        }

        if (!(booking.getStartTime().equals(startTime))) {
            bookingRepository.updateTimeSlot(booking.getId(), startTime, endTime);
        }
        bookingRepository.updateStatus(booking.getId(), PENDING);

        promptToRemoveParticipants(booking, studentId);
        promptToAddParticipants(booking, studentId);
        bookingRepository.save(booking);
        System.out.println("Booking edit requested");
    }

    private boolean isNotWithinAllowedHours(LocalDateTime newStartTime, LocalDateTime newEndTime) {
        LocalTime start = newStartTime.toLocalTime();
        LocalTime end = newEndTime.toLocalTime();

        return start.isBefore(openTime) || end.isAfter(closeTime) ||
                !newStartTime.toLocalDate().equals(newEndTime.toLocalDate());
    }

    private void promptToAddParticipants(Booking booking, int currentStudentId) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nWho would you like to add as participants?");
        System.out.println("Here are the available students:");

        System.out.printf("(You)ID: %d | Name: %s | Email: %s%n", booking.getStudent().getId(), booking.getStudent().getName(), booking.getStudent().getEmail());
        List<User> users =  userRepository.findAll();
        for (User user : users) {
            if (user.getId() != currentStudentId && user.getRole().equals("Student")) {
                System.out.printf("     ID: %d | Name: %s | Email: %s%n", user.getId(), user.getName(), user.getEmail());
            }
        }

        System.out.println("\nEnter the IDs of the students you want to add (separated by spaces), or enter 0 to skip:");
        String input = scanner.nextLine().trim();

        if (input.equals("0")) {
            System.out.println("No participants were added.");
            return;
        }

        Set<Student> currentParticipants = booking.getParticipants();

        String[] idStrings = input.split("\\s+");
        for (String idText : idStrings) {
            try {
                int id = Integer.parseInt(idText);
                if (id == currentStudentId) {
                    System.out.println("You can't add yourself as a participant.");
                    continue;
                }

                Optional<User> match = users.stream().filter(u -> u.getId() == id).findFirst();
                if (match.isPresent() && match.get() instanceof Student student) {
                    if (currentParticipants.contains(student)) {
                        System.out.println(student.getName() + " is already in the participant list.");
                    } else {
                        currentParticipants.add(student);
                        System.out.println("Added: " + student.getName());
                    }
                } else {
                    System.out.println("No student found with ID: " + id);
                }
            } catch (NumberFormatException e) {
                System.out.println("'" + idText + "' is not a valid number.");
            }
        }
    }

    private void promptToRemoveParticipants(Booking booking, int currentStudentId) {
        Set<Student> participants = booking.getParticipants();
        if (participants.isEmpty()) {
            System.out.println("\nThere are no participants to remove.");
            return;
        }

        System.out.println("\nHere are the current participants:");
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
                if (id == currentStudentId) {
                    System.out.println("You can't remove yourself.");
                    continue;
                }

                boolean removed = participants.removeIf(p -> p.getId() == id);

                if (removed) {
                    System.out.println("Removed participant with ID: " + id);
                } else {
                    System.out.println("No participant found with ID: " + id);
                }
            } catch (NumberFormatException e) {
                System.out.println("'" + idText + "' is not a valid number.");
            }
        }
    }

    @Transactional
    public void cancelBooking(int bookingId, Student student) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);

        if (booking != null && booking.getStudent().getId() == student.getId()) {
            bookingRepository.updateStatus(booking.getId(),CANCELLED);
            System.out.println("Booking with ID: " + bookingId + " has been cancelled.");
        } else  {
            System.out.println("Booking with ID: " + bookingId + " not found.");
        }
    }

    @Transactional
    public void approveBooking(int bookingId) {
        boolean result = updateBooking(bookingId, APPROVED);
        System.out.println(result ? "Booking approved" : "Booking approval failed");
    }

    @Transactional
    public void rejectBooking(int bookingId) {
        boolean result =  updateBooking(bookingId, REJECTED);
        System.out.println(result ? "Booking rejected" : "Booking rejection failed");
    }

    private boolean updateBooking(int bookingId, Status status) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null || booking.getStatus() != PENDING) return false;
        bookingRepository.updateStatus(booking.getId(), status);
        return true;
    }

    public List<Booking> filterBookings(Integer roomId) {
        return bookingRepository.findByRoomId(roomId);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> getBookingsByStudent(int id){
        return bookingRepository.findByStudentId(id);
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