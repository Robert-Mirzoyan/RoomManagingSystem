package Service;

import Repository.BookingRepository;
import Repository.UserRepository;
import Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static Model.Status.*;

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

    public boolean requestBooking(Room room, LocalDateTime startTime, LocalDateTime endTime, Student student) {

        if (isNotWithinAllowedHours(startTime, endTime)) {
            System.out.println("Booking must be between " + openTime + " and " + closeTime + "." );
            return false;
        }

        for (Booking existing : bookingRepository.findAll()) {
            if (existing.getRoom().getId() == room.getId() &&
                    existing.overlaps(startTime, endTime) &&
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

        Booking booking = new Booking(0, room, startTime, endTime, student, PENDING);

        promptToAddParticipants(booking, student);

        bookingRepository.save(booking);
        return true;
    }

    @Transactional
    public boolean editBooking(int bookingId, LocalDateTime startTime, LocalDateTime endTime, Student student) {
        if (!student.getEmail().equals(Objects.requireNonNull(bookingRepository.findById(bookingId).orElse(null)).getStudent().getEmail())) {
            System.out.println("Invalid student id.");
            return false;
        }

        if (isNotWithinAllowedHours(startTime, endTime)) {
            System.out.println("Edited time must be between " + openTime + " and " + closeTime + ".");
            return false;
        }

        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null || !booking.isEditable()) return false;

        for (Booking existing : bookingRepository.findAll()) {
            if (existing.getId() != bookingId &&
                    existing.getRoom().getId() == booking.getRoom().getId() &&
                    existing.overlaps(startTime, endTime) &&
                    existing.getStatus() != REJECTED &&
                    existing.getStatus() != CANCELLED) {
                System.out.println("Conflict with an existing booking.");
                return false;
            }
        }

        if (!(booking.getStartTime().equals(startTime))) {
            bookingRepository.updateTimeSlot(booking.getId(), startTime, endTime);
        }
        bookingRepository.updateStatus(booking.getId(), PENDING);

        promptToRemoveParticipants(booking, student);
        promptToAddParticipants(booking, student);
        bookingRepository.save(booking);
        return true;
    }

    private boolean isNotWithinAllowedHours(LocalDateTime newStartTime, LocalDateTime newEndTime) {
        LocalTime start = newStartTime.toLocalTime();
        LocalTime end = newEndTime.toLocalTime();

        return start.isBefore(openTime) || end.isAfter(closeTime) ||
                !newStartTime.toLocalDate().equals(newEndTime.toLocalDate());
    }

    private void promptToAddParticipants(Booking booking, Student currentStudent) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nWho would you like to add as participants?");
        System.out.println("Here are the available students:");

        System.out.printf("(You)ID: %d | Name: %s | Email: %s%n", booking.getStudent().getId(), booking.getStudent().getName(), booking.getStudent().getEmail());
        for (User user : userRepository.findAll()) {
            if (user.getId() != currentStudent.getId() && user.getRole().equals("Student")) {
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
                if (id == currentStudent.getId()) {
                    System.out.println("You can't add yourself as a participant.");
                    continue;
                }

                Optional<User> match = userRepository.findById(id);
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

    private void promptToRemoveParticipants(Booking booking, Student currentStudent) {
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
                if (id == currentStudent.getId()) {
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
    public boolean cancelBooking(int bookingId, Student student) {
        if (!student.getEmail().equals(Objects.requireNonNull(bookingRepository.findById(bookingId).orElse(null)).getStudent().getEmail())) return false;

        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) return false;
        bookingRepository.updateStatus(booking.getId(),CANCELLED);
        return true;
    }

    @Transactional
    public boolean approveBooking(int bookingId, FacultyManager manager) {
        return updateBooking(bookingId, manager, APPROVED);
    }

    @Transactional
    public boolean rejectBooking(int bookingId, FacultyManager manager) {
        return updateBooking(bookingId, manager, REJECTED);
    }

    private boolean updateBooking(int bookingId, FacultyManager manager, Status status) {
        if (!manager.getRole().equals("FacultyManager")) return false;

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

    public List<Student> getParticipants(int bookingId) {
        return bookingRepository.findById(bookingId).map(booking -> new ArrayList<>(booking.getParticipants())).orElse(new ArrayList<>());
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