package com.example.project.exercise;

import com.example.project.model.Booking;
import com.example.project.model.Room;
import com.example.project.model.Status;
import com.example.project.model.Student;
import com.example.project.repository.*;
import jakarta.persistence.*;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RoomBookingPersistenceTest {

    @Autowired
    EntityManager entityManager;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserRepository userRepository;

    @AfterEach
    void cleanDb() {
        bookingRepository.deleteAll();
        roomRepository.deleteAll();
        userRepository.deleteAll();
    }

    // Save Parent without ID using repository.save(), entityManager.persist(), entityManager.merge(). Compare the results.
    @Test
    @Order(1)
    void saveRoomWithRepository() {
        Room room = new Room("Repo Room", "Lab", 10);
        Room saved = roomRepository.save(room);

        assertNotEquals((Integer) null, saved.getId());
    }

    @Test
    @Order(2)
    void persistRoomWithEntityManager() {
        Room room = new Room("Persist Room", "Lab", 10);

        entityManager.persist(room);
        entityManager.flush();

        assertNotEquals((Integer) null, room.getId());
    }

    @Test
    @Order(3)
    void mergeRoomWithEntityManager() {
        Room room = new Room("Merge Room", "Lab", 10);

        entityManager.merge(room);
        entityManager.flush();

        assertNotEquals((Integer) null, room.getId());
    }


    // Save Parent with an initialized ID using repository.save(), entityManager.persist(), entityManager.merge(). Compare the results.
    @Test
    @Order(4)
    void saveRoomWithPresetId() {
        Room room = new Room("Repo Preset Room", "Lab", 10);
        room.setId(100);

        // This fails because Spring Data assumes we are updating an existing entity with ID = 100(when doing setId(100)).
        // But such an entity does not exist in the database, merge/update fails and causes ObjectOptimisticLockingFailureException.
        assertThrows(ObjectOptimisticLockingFailureException.class, () -> roomRepository.save(room));
    }

    @Test
    @Order(5)
    void persistRoomWithPresetId() {
        Room room = new Room("Persist Preset Room", "Lab", 10);
        room.setId(100);

        // persist() expects a new entity with a generated ID.
        // Setting ID manually breaks this assumption, and it thinks it already exists, and it will likely fail with an exception.
        assertThrows(EntityExistsException.class, () -> {
            entityManager.persist(room);
            entityManager.flush();
        });
    }

    @Test
    @Order(6)
    void mergeRoomWithPresetId() {
        Room room = new Room("Merge Preset Room", "Lab", 10);
        room.setId(100);

        // merge will fail because room with ID=100 does not exist yet
        assertThrows(OptimisticLockException.class, () -> {
            entityManager.merge(room);
            entityManager.flush();
        });
    }

    // Insert Parent with some ID to the database. Save another Parent with the same ID using repository.save(), entityManager.persist(), entityManager.merge(). Compare the results.
    @Test
    @Order(7)
    void saveRoomWithDuplicateId_repository() {
        Room original = new Room("Original Room", "Lab", 10);
        Room saved = roomRepository.save(original);

        Room duplicate = new Room("Duplicate Room", "Classroom", 15);
        duplicate.setId(saved.getId());

        Room result = roomRepository.save(duplicate);

        assertEquals(saved.getId(), result.getId());

        Room updated = roomRepository.findById(duplicate.getId()).orElseThrow(() -> new AssertionError("Room not found"));

        assertEquals("Duplicate Room", updated.getName());
    }

    @Test
    @Order(8)
    void saveRoomWithDuplicateId_persist() {
        Room original = new Room("Original Room", "Lab", 10);
        entityManager.persist(original);
        entityManager.flush();

        Room duplicate = new Room("Duplicate Room", "Classroom", 15);
        duplicate.setId(original.getId());

        // Expected to have EntityExistsException as persist is for new classes
        assertThrows(EntityExistsException.class, () -> {
            entityManager.persist(duplicate);
            entityManager.flush();
        });
    }

    @Test
    @Order(9)
    void saveRoomWithDuplicateId_merge() {
        Room original = new Room("Original Room", "Lab", 10);
        entityManager.persist(original);
        entityManager.flush();

        Room duplicate = new Room("Duplicate Room", "Classroom", 15);
        duplicate.setId(original.getId());

        entityManager.merge(duplicate);
        entityManager.flush();

        Room updated = roomRepository.findById(duplicate.getId()).orElseThrow(() -> new AssertionError("Room not found"));

        assertEquals("Duplicate Room", updated.getName());
    }

    // Save Parent with Children, which are not present in the database - using the same 3 approaches
    @Test
    @Order(10)
    void saveRoomWithNewBookings_repository() {
        Student student = new Student("Robert", "rob@gmail.com");
        userRepository.save(student);

        Room room = new Room("Room", "Lab", 20);

        new Booking(room, LocalDateTime.now(), LocalDateTime.now().plusHours(1), student, Status.PENDING);
        new Booking(room, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3), student, Status.PENDING);

        Room saved = roomRepository.save(room);

        Room fromDb = roomRepository.findById(saved.getId()).orElseThrow(() -> new AssertionError("Room not found"));

        assertEquals(0, fromDb.getBookings().size());
    }

    @Test
    @Order(11)
    void saveRoomWithNewBookings_persist() {
        Student student = new Student("Robert", "rob@gmail.com");
        entityManager.persist(student);
        entityManager.flush();

        Room room = new Room("Room", "Lab", 20);

        new Booking(room, LocalDateTime.now(), LocalDateTime.now().plusHours(1), student, Status.PENDING);
        new Booking(room, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3), student, Status.PENDING);

        entityManager.persist(room);
        entityManager.flush();

        Room fromDb = roomRepository.findById(room.getId()).orElseThrow(() -> new AssertionError("Room not found"));

        assertEquals(0, fromDb.getBookings().size());
    }

    @Test
    @Order(12)
    void saveRoomWithNewBookings_merge() {
        Student student = new Student("Robert", "rob@gmail.com");
        entityManager.persist(student);
        entityManager.flush();

        Room room = new Room("Room", "Lab", 20);

        new Booking(room, LocalDateTime.now(), LocalDateTime.now().plusHours(1), student, Status.PENDING);
        new Booking(room, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3), student, Status.PENDING);

        Room merged = entityManager.merge(room);
        entityManager.flush();
        entityManager.clear();

        Room fromDb = roomRepository.findById(merged.getId()).orElseThrow(() -> new AssertionError("Room not found"));

        assertEquals(0, fromDb.getBookings().size());
    }

    // Save Parent with Children, which are already present in the database - using the same 3 approaches
    @Test
    @Order(13)
    void saveRoomWithPresentBookings_repository() {
        Student student = new Student("Robert", "rob@gmail.com");
        userRepository.save(student);

        Room room = new Room("Room", "Lab", 20);

        Booking booking1 = new Booking(room, LocalDateTime.now(), LocalDateTime.now().plusHours(1), student, Status.PENDING);
        Booking booking2 = new Booking(room, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3), student, Status.PENDING);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

//        room.setBookings(List.of(booking1, booking2));

        Room saved = roomRepository.save(room);
        entityManager.flush();
        entityManager.clear();

        Room fromDb = roomRepository.findById(saved.getId()).orElseThrow(() -> new AssertionError("Room not found"));

        assertEquals(2, fromDb.getBookings().size());
    }

    @Test
    @Order(14)
    void saveRoomWithPresentBookings_persist() {
        Student student = new Student("Robert", "rob@gmail.com");
        entityManager.persist(student);
        entityManager.flush();

        Room room = new Room("Room", "Lab", 20);

        Booking booking1 = new Booking(room, LocalDateTime.now(), LocalDateTime.now().plusHours(1), student, Status.PENDING);
        Booking booking2 = new Booking(room, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3), student, Status.PENDING);
        entityManager.persist(booking1);
        entityManager.persist(booking2);

//        room.setBookings(List.of(booking1, booking2));

        entityManager.persist(room);
        entityManager.flush();
        entityManager.clear();

        Room fromDb = roomRepository.findById(room.getId()).orElseThrow(() -> new AssertionError("Room not found"));

        assertEquals(2, fromDb.getBookings().size());
    }

    @Test
    @Order(15)
    void saveRoomWithPresentBookings_merge() {
        Student student = new Student("Robert", "rob@gmail.com");
        entityManager.persist(student);
        entityManager.flush();

        Room room = new Room("Room", "Lab", 20);

        Booking booking1 = new Booking(room, LocalDateTime.now(), LocalDateTime.now().plusHours(1), student, Status.PENDING);
        Booking booking2 = new Booking(room, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3), student, Status.PENDING);
        entityManager.persist(booking1);
        entityManager.persist(booking2);

        room.setBookings(List.of(booking1, booking2));

        Room merged = entityManager.merge(room);
        entityManager.flush();
        entityManager.clear();

        Room fromDb = roomRepository.findById(merged.getId()).orElseThrow(() -> new AssertionError("Room not found"));

        assertEquals(2, fromDb.getBookings().size());
    }

    // Save Child without Parent - using the same 3 approaches
    @Test
    @Order(16)
    void saveBookingsWithoutRoom_repository() {
        Student student = new Student("Robert", "rob@gmail.com");

        Room room = new Room("Room", "Lab", 20);

        Booking booking = new Booking(room, LocalDateTime.now(), LocalDateTime.now().plusHours(1), student, Status.PENDING);

        // It throws exception indicating that parent entity must be saved first
        assertThrows(IllegalStateException.class, () -> {
            bookingRepository.save(booking);
            entityManager.flush();
        });
        entityManager.clear();
    }

    @Test
    @Order(17)
    void saveBookingsWithoutRoom_persist() {
        Student student = new Student("Robert", "rob@gmail.com");

        Room room = new Room("Room", "Lab", 20);

        Booking booking = new Booking(room, LocalDateTime.now(), LocalDateTime.now().plusHours(1), student, Status.PENDING);

        // It throws exception indicating that parent entity must be saved first
        assertThrows(IllegalStateException.class, () -> {
            entityManager.persist(booking);
            entityManager.flush();
        });
        entityManager.clear();
    }

    @Test
    @Order(18)
    void saveBookingsWithoutRoom_merge() {
        Student student = new Student("Robert", "rob@gmail.com");

        Room room = new Room("Room", "Lab", 20);

        Booking booking = new Booking(room, LocalDateTime.now(), LocalDateTime.now().plusHours(1), student, Status.PENDING);

        // It throws exception indicating that parent entity must be saved first
        assertThrows(IllegalStateException.class, () -> {
            entityManager.merge(booking);
            entityManager.flush();
        });
        entityManager.clear();
    }

    // Save Child with Parent initialized, but not present in the database - using the same 3 approaches.
    @Test
    @Order(19)
    void saveBookingsWithoutRoomInDB_repository() {
        Student student = new Student("Robert", "rob@gmail.com");

        Room room = new Room("Room", "Lab", 20);
        room.setId(100);

        Booking booking = new Booking(room, LocalDateTime.now(), LocalDateTime.now().plusHours(1), student, Status.PENDING);

        // Spring data sees Room has ID 100 and tries to insert booking with that room as FK, but since there's no room in the DB it throws exception about violating FK
        assertThrows(DataIntegrityViolationException.class, () -> {
            bookingRepository.save(booking);
            entityManager.flush();
        });
        entityManager.clear();
    }

    @Test
    @Order(20)
    void saveBookingsWithoutRoomInDB_persist() {
        Student student = new Student("Robert", "rob@gmail.com");

        Room room = new Room("Room", "Lab", 20);
        room.setId(100);

        Booking booking = new Booking(room, LocalDateTime.now(), LocalDateTime.now().plusHours(1), student, Status.PENDING);

        // persist() tries to insert the booking as it is, and it includes the FK to room. But since that room doesn't exist in DB, it throws FK violation exception in DB
        assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persist(booking);
            entityManager.flush();
        });
        entityManager.clear();
    }

    @Test
    @Order(21)
    void saveBookingsWithoutRoomInDB_merge() {
        Student student = new Student("Robert", "rob@gmail.com");

        Room room = new Room("Room", "Lab", 20);
        room.setId(100);

        Booking booking = new Booking(room, LocalDateTime.now(), LocalDateTime.now().plusHours(1), student, Status.PENDING);

        // merge() attempts to reattach both Booking and Room, but Room doesn’t exist in the DB, so it throws EntityNotFoundException as it tries to find room and fails
        assertThrows(EntityNotFoundException.class, () -> {
            entityManager.merge(booking);
            entityManager.flush();
        });
        entityManager.clear();
    }

    //Save Child with Parent initialized, present in the database, but detached from EntityManager/Session - using the same 3 approaches
    @Test
    @Order(22)
    void saveBookingWithDetachedRoom_repository() {
        Student student = new Student("Robert", "rob@gmail.com");
        userRepository.save(student);

        Room room = new Room("Room", "Lab", 20);
        roomRepository.save(room);

        entityManager.flush();
        entityManager.clear();

        Booking booking = new Booking(room, LocalDateTime.now(), LocalDateTime.now().plusHours(1), student, Status.PENDING);

        // Works because Spring Data internally uses merge, which reattaches detached Room
        Booking saved = bookingRepository.save(booking);
        assertNotNull(saved.getRoom());
    }

    @Test
    @Order(23)
    void saveBookingWithDetachedRoom_persist() {
        Student student = new Student("Robert", "rob@gmail.com");
        entityManager.persist(student);

        Room room = new Room("Room", "Lab", 20);
        entityManager.persist(room);
        entityManager.flush();
        entityManager.clear();

        Booking booking = new Booking(room, LocalDateTime.now(), LocalDateTime.now().plusHours(1), student, Status.PENDING);

        entityManager.persist(booking);
        entityManager.flush();

        Booking fromDb = bookingRepository.findAll().get(0);
        assertEquals(room.getId(), fromDb.getRoom().getId());
    }

    @Test
    @Order(24)
    void saveBookingWithDetachedRoom_merge() {
        Student student = new Student("Robert", "rob@gmail.com");
        entityManager.persist(student);

        Room room = new Room("Room", "Lab", 20);
        entityManager.persist(room);
        entityManager.flush();
        entityManager.clear();

        Booking booking = new Booking(room, LocalDateTime.now(), LocalDateTime.now().plusHours(1), student, Status.PENDING);

        // This works as merge reattaches the room
        Booking merged = entityManager.merge(booking);
        entityManager.flush();

        assertNotNull(merged.getRoom());
    }

    // Fetch the Parent with JpaRepository, try changing it and don’t save it explicitly. Flush the session and check whether the changes were propagated to the database
    @Test
    @Order(25)
    void changeRoomWithoutSaving_repository() {
        Room savedRoom = roomRepository.save(new Room("Original Room", "Lab", 20));

        Room room = roomRepository.findById(savedRoom.getId()).orElseThrow();
        room.setCapacity(99);
        room.setName("Modified Room");

        // EntityManager flush detects the change and synchronizes the DB thanks to dirty checking.
        entityManager.flush();

        Room fromDb = roomRepository.findById(room.getId()).orElseThrow();
        assertEquals("Modified Room", fromDb.getName());
        assertEquals(99, fromDb.getCapacity());
    }

    @Test
    @Order(26)
    void changeRoomWithoutSaving_persist() {
        Room room = new Room("Original Room", "Lab", 20);
        entityManager.persist(room);
        entityManager.flush();

        room.setCapacity(99);
        room.setName("Modified Room");

        entityManager.flush();

        Room fromDb = roomRepository.findById(room.getId()).orElseThrow();
        assertEquals("Modified Room", fromDb.getName());
        assertEquals(99, fromDb.getCapacity());
    }

    // Start the transaction, fetch the Parent with JpaRepository, try changing it and don’t save it explicitly. Flush the session and check whether the changes were propagated to the database
    // same as step 11(tests 25, 26)
}
