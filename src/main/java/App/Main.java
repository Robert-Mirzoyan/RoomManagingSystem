package App;

import Service.*;
import Model.*;
import DB.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        RoomService roomService = new RoomService();
        BookingService bookingService = new BookingService();
        ReportService reportService = new ReportService(bookingService);

        UserDB userDB = new UserDB();

        List<User> studentUsers = userDB.findAllStudents();
        ArrayList<Student> students = (ArrayList<Student>) studentUsers.stream()
                .map(u -> (Student) u)
                .collect(Collectors.toList());


        FacultyManager manager = (FacultyManager) userDB.findByEmail("skachat@aua.am").orElseThrow();
        Admin admin = (Admin) userDB.findByEmail("artur@edu.aua.am").orElseThrow();

        // Rooms
//        roomService.addRoom("Room A", "Lab", 20, admin);
//        roomService.addRoom("Room B", "Auditorium", 30, admin);

        // For testing
        // 2025/06/01 12:30-2025/06/01 14:00
        // 2025/09/09 12:30-2025/09/09 14:00
        // 2025/10/10 12:30-2025/10/10 14:00
        // 2025/10/11 12:30-2025/10/11 14:00

        while (true){
            System.out.println("to log in as student 1 enter 1\nto log in as student 2 enter 2\nto log in as student 3 enter 3\nto log in as student 4 enter 4\nto log in as manager enter 5\nto log in as admin enter 6\nto exit enter 0");
            Scanner scanner = new Scanner(System.in);
            int input = scanner.nextInt();
            if (input == 0){
                System.out.println("Exited the system. Goodbye!");
                break;
            }
            else if (input == 1){
                studentAccount(students.get(0), bookingService, roomService, students, scanner);
            }
            else if (input == 2){
                studentAccount(students.get(1), bookingService, roomService, students, scanner);
            }
            else if (input == 3){
                studentAccount(students.get(2), bookingService, roomService, students, scanner);
            }
            else if (input == 4){
                studentAccount(students.get(3), bookingService, roomService, students, scanner);
            }
            else if (input == 5){
                managerAccount(manager, bookingService, scanner);
            }
            else if (input == 6){
                adminAccount(admin, bookingService, roomService, reportService, scanner);
            }
        }
    }
    public static void studentAccount(Student student, BookingService bookingService, RoomService roomService, ArrayList<Student> students, Scanner scanner){
        System.out.println("Student account: " + student.getName() + ", " + student.getEmail());
        while (true) {
            System.out.println("\nto get your bookings enter 1\nto edit your booking enter 2\nto add booking enter 3\nto cancel booking enter 4\nto exit enter 0");
            int input = scanner.nextInt();
            if (input == 0) {
                break;
            }
            else if (input == 1){
                List<Booking> bookings = bookingService.getBookingsByStudent(student.getId());
                if (bookings.isEmpty()){
                    System.out.println("\nNo Bookings found for this student");
                }
                for (Booking booking : bookings){
                    System.out.printf(
                            "Booking ID: %d, Room: %s, Booking status: %s, Time Slot: %s - %s%n",
                            booking.getId(),
                            booking.getRoom().getName(),
                            booking.getStatus(),
                            booking.getTimeSlot().getStartTime(),
                            booking.getTimeSlot().getEndTime()
                    );
                    System.out.println("Participants:");
                    System.out.printf("(You)ID: %d | Name: %s | Email: %s%n", booking.getStudent().getId(), booking.getStudent().getName(), booking.getStudent().getEmail());
                    for (Student s : bookingService.getParticipants(booking.getId())) {
                        System.out.printf("     ID: %d | Name: %s | Email: %s%n", s.getId(), s.getName(), s.getEmail());
                    }
                    System.out.println();
                }
            } // 2025/06/01 12:30-2025/06/01 14:00
            else if (input == 2) {
                System.out.println("Enter booking ID to edit");
                int id;
                try {
                    id = scanner.nextInt();
                    TimeSlot slot = readTimeSlotFromInput(scanner, id);
                    boolean result = bookingService.editBooking(id, slot, student);
                    System.out.println(result ? "Booking edit requested" : "Booking edit request failed");
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid time slot format");
                } catch (NullPointerException e) {
                    System.out.println("Booking not found");
                }
            } else if (input == 3) {
                List<Room> rooms = roomService.getAllRooms();
                for (Room room : rooms) {
                    System.out.println(room.getName() + " (" + room.getType() + ", " + room.getCapacity() + " capacity), ID: " + room.getId());
                }

                System.out.println("Enter room id to add booking");
                int id;
                try {
                    id = scanner.nextInt();
                    TimeSlot slot = readTimeSlotFromInput(scanner, 0);
                    boolean result = bookingService.requestBooking(roomService.getRoom(id), slot, student);
                    System.out.println(result ? "Booking requested" : "Booking request failed");
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid time slot format");
                } catch (NullPointerException e) {
                    System.out.println("Booking not found or invalid room ID");
                } catch (Exception e) {
                    System.out.println("Unknown error occurred: " + e.getMessage());
                }
            }
            else if (input == 4){
                System.out.println("Enter booking ID to cancel");
                int id = scanner.nextInt();
                boolean result = bookingService.cancelBooking(id, student);
                System.out.println(result ? "Booking cancelled" : "Booking not found" );
            }
            else {
                System.out.println("Invalid input");
            }
        }
    }

    private static TimeSlot readTimeSlotFromInput(Scanner scanner, int slotId) {
        System.out.println("Enter time slot in \"yyyy/MM/dd HH:mm-yyyy/MM/dd HH:mm\" format");
        scanner.nextLine(); // Consume newline
        String input = scanner.nextLine();
        String[] parts = input.split("-");
        if (parts.length != 2) throw new IllegalArgumentException("Invalid format");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        return new TimeSlot(slotId, LocalDateTime.parse(parts[0], formatter), LocalDateTime.parse(parts[1], formatter));
    }

    public static void managerAccount(FacultyManager manager, BookingService bookingService, Scanner scanner){
        System.out.println("Manager account: " + manager.getName() + ", " + manager.getEmail());
        while (true){
            System.out.println("\nto get all bookings enter 1\nto approve booking enter 2\nto reject booking enter 3\nto filter bookings by room enter 4\nto filter bookings by requester enter 5\nto exit enter 0");
            int input = scanner.nextInt();
            if (input == 0) {
                break;
            }
            else if (input == 1){
                List<Booking> bookings = bookingService.getAllBookings();
                if (bookings.isEmpty()){
                    System.out.println("No Bookings found for this student");
                }
                for (Booking booking : bookings){
                    System.out.printf(
                            "Booking ID: %d, Room: %s (ID: %d), Booking status: %s, Requester ID: %d, Time Slot: %s - %s%n",
                            booking.getId(),
                            booking.getRoom().getName(),
                            booking.getRoom().getId(),
                            booking.getStatus(),
                            booking.getStudent().getId(),
                            booking.getTimeSlot().getStartTime(),
                            booking.getTimeSlot().getEndTime()
                    );
//                    booking.getParticipants();
                }
            }
            else if (input == 2){
                System.out.println("Enter booking ID to approve");
                int id = scanner.nextInt();
                boolean result = bookingService.approveBooking(id, manager);
                System.out.println(result ? "Booking approved" : "Booking approval failed");
            }
            else if (input == 3){
                System.out.println("Enter booking ID to reject");
                int id = scanner.nextInt();
                boolean result = bookingService.rejectBooking(id, manager);
                System.out.println(result ? "Booking rejected" : "Booking rejection failed");
            }
            else if (input == 4){
                System.out.println("Enter room id to filter by room");
                int id = scanner.nextInt();
                List<Booking> bookings = bookingService.filterBookings(id);
                for (Booking booking : bookings){
                    System.out.printf(
                            "Booking ID: %d, Room: %s (ID: %d), Booking status: %s, Requester ID: %d, Time Slot: %s - %s%n",
                            booking.getId(),
                            booking.getRoom().getName(),
                            booking.getRoom().getId(),
                            booking.getStatus(),
                            booking.getStudent().getId(),
                            booking.getTimeSlot().getStartTime(),
                            booking.getTimeSlot().getEndTime()
                    );
                }
            }
            else if (input == 5){
                System.out.println("Enter student id to filter by requester");
                int id = scanner.nextInt();
                List<Booking> bookings = bookingService.getBookingsByStudent(id);
                for (Booking booking : bookings){
                    System.out.printf(
                            "Booking ID: %d, Room: %s (ID: %d), Booking status: %s, Requester ID: %d, Time Slot: %s - %s%n",
                            booking.getId(),
                            booking.getRoom().getName(),
                            booking.getRoom().getId(),
                            booking.getStatus(),
                            booking.getStudent().getId(),
                            booking.getTimeSlot().getStartTime(),
                            booking.getTimeSlot().getEndTime()
                    );
                }
            }
        }
    }
    public static void adminAccount(Admin admin, BookingService bookingService, RoomService roomService, ReportService reportService, Scanner scanner){
        System.out.println("Admin account: " + admin.getName() + ", " + admin.getEmail());
        while (true){
            System.out.println("\nto view all rooms enter 1\nto change room open/close time enter 2\nto add room enter 3\nto remove room enter 4\nto edit room info enter 5\nto generate usage statistics enter 6\nto exit enter 0");
            int input = scanner.nextInt();
            if (input == 0) {
                break;
            }
            else if (input == 1){
                List<Room> rooms = roomService.getAllRooms();

                for (Room room : rooms){
                    System.out.println(room.getName() + " (" + room.getType() + ", " + room.getCapacity() + " capacity), ID: " + room.getId());
                }
                System.out.println("Opening time: " + bookingService.getOpenTime() + ", closing time: " + bookingService.getCloseTime());
            }
            else if (input == 2){
                System.out.println("Enter new opening time in \"HH:mm\" format");
                String ignore = scanner.nextLine();
                String newOpenTime = scanner.nextLine();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                System.out.println("Enter new closing time in \"HH:mm\" format");
                String newCloseTime = scanner.nextLine();
                bookingService.setOpenTime(LocalTime.parse(newOpenTime, formatter));
                bookingService.setCloseTime(LocalTime.parse(newCloseTime, formatter));
                System.out.println("Opening time changed to " + bookingService.getOpenTime() + ", closing time changed to " + bookingService.getCloseTime());
            }
            else if (input == 3){
                System.out.println("Enter new room name");
                String ignore = scanner.nextLine();
                String name = scanner.nextLine();
                System.out.println("Enter new room type");
                String type = scanner.nextLine();
                System.out.println("Enter new room capacity");
                int capacity = scanner.nextInt();
                boolean result = roomService.addRoom(name, type, capacity, admin);
                System.out.println(result ? "Room added" : "Adding room failed");
            } else if (input == 4) {
                System.out.println("Enter room id to remove");
                int id = scanner.nextInt();
                if (roomService.getRoom(id) == null) {
                    System.out.println("Room not found");
                    continue;
                }
                List<Booking> bookings = bookingService.filterBookings(id);
                boolean canRemove = true;
                for (Booking booking : bookings) {
                    if (booking.getStatus() == Status.APPROVED){
                        System.out.println("Booking ID: " + booking.getId() + ", Room: " + booking.getRoom().getName() + " is still approved, cannot remove");
                        canRemove = false;
                    }
                }
                if (canRemove) {
                    boolean result = roomService.removeRoom(id);
                    System.out.println(result ? "Room removed" : "Failed to remove room");
                }
            }
            else if (input == 5){
                System.out.println("Enter room id to edit");
                int id;
                try {
                    id = scanner.nextInt();
                } catch (Exception e) {
                    System.out.println("Invalid id");
                    continue;
                }
                Room room = roomService.getRoom(id);
                if (room == null) {
                    System.out.println("Room not found");
                    continue;
                }
                System.out.println("Enter new name");
                String ignore = scanner.nextLine();
                String name = scanner.nextLine();
                System.out.println("Enter new type");
                String type = scanner.nextLine();
                System.out.println("Enter new capacity");
                int capacity = scanner.nextInt();
                boolean result = roomService.updateRoom(id, name, type, capacity);
                System.out.println(result ? "Room edit completed" : "Room edit failed");
            } else if (input == 6) {
                generateStatistics(reportService);
            }
        }
    }
    private static void generateStatistics(ReportService reportService){
        while (true){
            System.out.println("\nto generate top N most used rooms enter 1\nto generate room usage statistics for specific date enter 2\nto exit enter 0");
            Scanner scanner = new Scanner(System.in);
            int input = scanner.nextInt();
            if (input == 0){
                break;
            }
            else if (input == 1){
                System.out.println("Enter number of rooms to generate statistics for");
                int number = scanner.nextInt();
                List<Room> rooms = reportService.mostBookedRooms(number);
                System.out.println();
                for (Room room : rooms){
                    System.out.println(room.getName() + " (" + room.getType() + ", " + room.getCapacity() + " capacity), ID: " + room.getId());
                }
            }
            else if (input == 2){
                System.out.println("Enter date in \"yyyy/MM/dd\" format");
                String ignore = scanner.nextLine();
                String date = scanner.nextLine();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                List<Booking> bookings = reportService.roomUsageByDate(LocalDate.parse(date,formatter));
                for (Booking booking : bookings){
                    System.out.printf(
                            "Booking ID: %d, Room: %s, Booking status: %s, Requester ID: %d, Time Slot: %s - %s%n",
                            booking.getId(),
                            booking.getRoom().getName(),
                            booking.getStatus(),
                            booking.getStudent().getId(),
                            booking.getTimeSlot().getStartTime(),
                            booking.getTimeSlot().getEndTime()
                    );
                }
            }
        }
    }
}