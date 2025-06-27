package com.example.project.service.mock;

import com.example.project.model.Room;
import com.example.project.repository.RoomRepository;
import com.example.project.service.RoomService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
public class RoomServiceMockTest {
    @Autowired
    private RoomService roomService;

    @MockitoBean
    private RoomRepository roomRepository;

    @Test
    void testAddRoomDelegatesToRepository() {
        roomService.addRoom("Mock Lab", "Lab", 25);

        // Capture the argument passed to roomRepository.save()
        ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);
        verify(roomRepository).save(roomCaptor.capture());

        Room savedRoom = roomCaptor.getValue();

        Assertions.assertEquals("Mock Lab", savedRoom.getName());
        Assertions.assertEquals("Lab", savedRoom.getType());
        Assertions.assertEquals(25, savedRoom.getCapacity());
    }
}
