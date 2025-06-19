package DB;

import Model.Room;
import Util.JdbcUtil;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

@Repository
public class RoomDB {

    private static final Function<ResultSet, Room> ROOM_MAPPER = rs -> {
        try {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            String type = rs.getString("type");
            int capacity = rs.getInt("capacity");
            return new Room(id, name, type, capacity);
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping room", e);
        }
    };

    public void save(Room room) {
        String query = "INSERT INTO room (name, type, capacity) VALUES (?, ?, ?)";
        JdbcUtil.execute(query, room.getName(), room.getType(), room.getCapacity());
    }

    public Room findById(int id) {
        String query = "SELECT * FROM room WHERE id = ?";
        return JdbcUtil.findOne(query, ROOM_MAPPER, id);
    }

    public List<Room> findAll() {
        String query = "SELECT * FROM room";
        return JdbcUtil.findMany(query, ROOM_MAPPER);
    }

    public void deleteById(int id) {
        String query = "DELETE FROM room WHERE id = ?";
        JdbcUtil.execute(query, id);
    }

    public void update(Room room) {
        String query = "UPDATE room SET name = ?, type = ?, capacity = ? WHERE id = ?";
        JdbcUtil.execute(query, room.getName(), room.getType(), room.getCapacity(), room.getId());
    }
}
