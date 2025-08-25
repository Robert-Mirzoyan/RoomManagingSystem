package com.example.project.service;

import com.example.project.dto.RoomCreateDto;
import com.example.project.dto.RoomReadDto;
import com.example.project.model.Room;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class RoomRestService {
    private final RoomService roomService;
    private final UserClient userClient;


    public RoomReadDto getRoomById(int id) {
        return toReadDto(roomService.getRoom(id));
    }

    public RoomReadDto updateRoomById(int id, RoomCreateDto dto) {
        assertAdmin(dto.getUserId());

        if (roomService.getRoom(id) == null) {
            return null;
        }
        Room updated = roomService.updateRoom(id, dto.getName(),  dto.getType(), dto.getCapacity());
        return toReadDto(updated);
    }

    public RoomReadDto createRoom(RoomCreateDto dto) {
        assertAdmin(dto.getUserId());

        Room saved = roomService.addRoom(dto.getName(), dto.getType(), dto.getCapacity());
        return toReadDto(saved);
    }

    public RoomReadDto deleteRoom(int id, @RequestParam Integer userId) {
        assertAdmin(userId);

        if (roomService.getRoom(id) == null) {
            return null;
        }
        Room deleted = roomService.removeRoom(id);
        return toReadDto(deleted);
    }

    @SneakyThrows
    private void assertAdmin(Integer userId) {
        if (!userClient.isAdmin(userId)) {
            throw new AccessDeniedException("Only Admins can perform this action");
        }
    }

    private RoomReadDto toReadDto(Room room) {
        if (room == null) {
            throw new EntityNotFoundException("Room not found");
        }
        return new RoomReadDto(room.getId(), room.getName(), room.getType(), room.getCapacity());
    }

    public Page<RoomReadDto> getAllRooms(Pageable pageable) {
        return roomService.getAllRooms(pageable)
                .map(this::toReadDto);
    }

    public Page<RoomReadDto> filterRooms(String name, String type, int minCapacity, Pageable pageable) {
        return roomService.filterRooms(name, type, minCapacity, pageable)
                .map(this::toReadDto);
    }
}
