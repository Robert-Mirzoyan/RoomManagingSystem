package com.example.project.service;

import com.example.project.repository.RoomRepository;
import com.example.project.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room addRoom(String name, String type, int capacity) {
        if (capacity <= 0) {
            return null;
        }
        Room room = new Room(name, type, capacity);
        roomRepository.save(room);
        return room;
    }

    public Room removeRoom(int roomId) {
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null) {
            System.out.println("Failed to remove room");
            return null;
        }
        roomRepository.deleteById(roomId);
        System.out.println("Room removed");
        return room;
    }

    public Room updateRoom(int roomId, String name, String type, int capacity) {
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null) {
            System.out.println("Room edit failed");
            return null;
        }
        room.updateDetails(name, type, capacity);
        roomRepository.save(room);
        System.out.println("Room edit completed");
        return room;
    }

    public Room getRoom(int roomId) {
        return roomRepository.findById(roomId).orElse(null);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll(Sort.by("id").ascending());
    }
}