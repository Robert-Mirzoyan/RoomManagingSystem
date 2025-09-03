package com.example.project.service;

import com.example.project.dto.RoomCreateDto;
import com.example.project.dto.RoomReadDto;
import com.example.project.model.Room;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomRestService {
    private final RoomService roomService;

    public RoomReadDto getRoomById(int id) {
        return toReadDto(roomService.getRoom(id));
    }

    public RoomReadDto updateRoomById(int id, RoomCreateDto dto) {

        if (roomService.getRoom(id) == null) {
            return null;
        }
        Room updated = roomService.updateRoom(id, dto.getName(),  dto.getType(), dto.getCapacity());
        return toReadDto(updated);
    }

    public RoomReadDto createRoom(RoomCreateDto dto) {

        Room saved = roomService.addRoom(dto.getName(), dto.getType(), dto.getCapacity());
        return toReadDto(saved);
    }

    public RoomReadDto deleteRoom(int id) {

        if (roomService.getRoom(id) == null) {
            return null;
        }
        Room deleted = roomService.removeRoom(id);
        return toReadDto(deleted);
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
