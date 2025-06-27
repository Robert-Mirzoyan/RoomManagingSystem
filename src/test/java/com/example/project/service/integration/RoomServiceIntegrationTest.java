package com.example.project.service.integration;

import com.example.project.model.Room;
import com.example.project.service.RoomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest()
@ActiveProfiles("test")
@Transactional
class RoomServiceIntegrationTest {

    @Autowired
    private RoomService roomService;

    @Test
    void testAddRoom() {
        roomService.addRoom("Test Room", "Lab", 15);

        List<Room> allRooms = roomService.getAllRooms();
        assertTrue(allRooms.stream().anyMatch(r -> r.getName().equals("Test Room")));
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 15, 20})
    void testAddRoomWithValidCapacity(int capacity) {
        roomService.addRoom("Test Room", "Lab", capacity);
        List<Room> rooms = roomService.getAllRooms();

        assertTrue(
                rooms.stream().anyMatch(r -> r.getName().equals("Test Room") && r.getCapacity() == capacity)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCapacity")
    void testAddRoomWithInvalidCapacity(int capacity) {
        int size = roomService.getAllRooms().size();
        roomService.addRoom("Test Room", "Lab", capacity);
        List<Room> rooms = roomService.getAllRooms();

        assertEquals(size, rooms.size());

        assertTrue(
                rooms.stream().noneMatch(r -> r.getName().equals("Test Room") && r.getCapacity() == capacity)
        );
    }

    private static Stream<Arguments> provideInvalidCapacity() {
        return Stream.of(
                Arguments.of(-20),
                Arguments.of(-10),
                Arguments.of(0)
        );
    }
}

