package com.example.project.service;

import com.example.project.repository.RoomRepository;
import com.example.project.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public void addRoom(String name, String type, int capacity) {
        Room room = new Room(0, name, type, capacity);
        roomRepository.save(room);
    }

    public void removeRoom(int roomId) {
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null) {
            System.out.println("Failed to remove room");
            return;
        }
        roomRepository.deleteById(roomId);
        System.out.println("Room removed");
    }

    public void updateRoom(int roomId, String name, String type, int capacity) {
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null) {
            System.out.println("Room edit failed");
            return;
        }
        room.updateDetails(name, type, capacity);
        roomRepository.save(room);
        System.out.println("Room edit completed");
    }

    public Room getRoom(int roomId) {
        return roomRepository.findById(roomId).orElse(null);
    }

    public void printAllRooms() {
        for (Room room : roomRepository.findAll()){
            System.out.println(room.getName() + " (" + room.getType() + ", " + room.getCapacity() + " capacity), ID: " + room.getId());
        }
    }
}