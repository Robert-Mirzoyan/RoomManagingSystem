package Service;

import Model.*;
import java.util.*;

public class RoomService {
    private final Map<Integer, Room> rooms = new HashMap<>();
    private int nextRoomId = 1;

    public boolean addRoom(String name, String type, int capacity, Admin admin) {
        if (!admin.getRole().equals("Admin")){
            return false;
        }
        Room room = new Room(nextRoomId++, name, type, capacity);
        rooms.put(room.getId(), room);
        return true;
    }

    public boolean removeRoom(int roomId) {
        if (!rooms.containsKey(roomId)) return false;
        rooms.remove(roomId);
        return true;
    }

    public boolean updateRoom(int roomId, String name, String type, int capacity) {
        Room room = rooms.get(roomId);
        if (room == null) return false;
        room.updateDetails(name, type, capacity);
        return true;
    }

    public Room getRoom(int roomId) {
        return rooms.get(roomId);
    }

    public List<Room> searchRooms(String type, int minCapacity) {
        List<Room> result = new ArrayList<>();
        for (Room room : rooms.values()) {
            if (room.getType().equalsIgnoreCase(type) && room.getCapacity() >= minCapacity) {
                result.add(room);
            }
        }
        return result;
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }
}