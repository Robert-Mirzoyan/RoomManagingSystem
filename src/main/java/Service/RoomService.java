package Service;

import Repository.RoomRepository;
import Model.Admin;
import Model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class RoomService {
//    private final RoomDB roomDB;
    private final RoomRepository roomRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public boolean addRoom(String name, String type, int capacity, Admin admin) {
        if (!admin.getRole().equals("Admin")){
            return false;
        }
        Room room = new Room(0, name, type, capacity);
        roomRepository.save(room);
        return true;
    }

    public boolean removeRoom(int roomId) {
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null) return false;
        roomRepository.deleteById(roomId);
        return true;
    }

    public boolean updateRoom(int roomId, String name, String type, int capacity) {
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null) return false;
        room.updateDetails(name, type, capacity);
        roomRepository.save(room);
        return true;
    }

    public Room getRoom(int roomId) {
        return roomRepository.findById(roomId).orElse(null);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
}