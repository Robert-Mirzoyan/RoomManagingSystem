package Service;

import Model.*;
import DB.*;
import java.util.*;

public class RoomService {
    private final RoomDB roomDB = new RoomDB();

    public boolean addRoom(String name, String type, int capacity, Admin admin) {
        if (!admin.getRole().equals("Admin")){
            return false;
        }
        Room room = new Room(0, name, type, capacity);
        roomDB.save(room);
        return true;
    }

    public boolean removeRoom(int roomId) {
        Room room = roomDB.findById(roomId);
        if (room == null) return false;
        roomDB.deleteById(roomId);
        return true;
    }

    public boolean updateRoom(int roomId, String name, String type, int capacity) {
        Room room = roomDB.findById(roomId);
        if (room == null) return false;
        room.updateDetails(name, type, capacity);
        roomDB.update(room);
        return true;
    }

    public Room getRoom(int roomId) {
        return roomDB.findById(roomId);
    }

    public List<Room> searchRooms(String type, int minCapacity) {
        return roomDB.findByTypeAndCapacity(type, minCapacity);
    }

    public List<Room> getAllRooms() {
        return roomDB.findAll();
    }
}