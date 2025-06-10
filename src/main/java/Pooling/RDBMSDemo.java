package Pooling;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class RDBMSDemo {
    private static HikariDataSource dataSource;

    public RDBMSDemo() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/mydb");
        config.setUsername("rob");
        config.setPassword("12345678");
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(10000);

        dataSource = new HikariDataSource(config);
    }

    public int WithoutTransaction() {
        String insertBooking = "INSERT INTO booking (user_id, room_id, timeslot_id, status) VALUES (?, ?, ?, ?) RETURNING id";
        String insertParticipant = "INSERT INTO booking_participant (booking_id, user_id) VALUES (?, ?)";

        int bookingId = -1;

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(true);

            try (PreparedStatement stmt = conn.prepareStatement(insertBooking)) {
                stmt.setInt(1, 101);
                stmt.setInt(2, 2);
                stmt.setInt(3, 6);
                stmt.setString(4, "PENDING");
                ResultSet rs = stmt.executeQuery();
                rs.next();
                bookingId = rs.getInt(1);
            }

            if (true) throw new RuntimeException("Simulated crash before inserting participant");

            try (PreparedStatement stmt = conn.prepareStatement(insertParticipant)) {
                stmt.setInt(1, bookingId);
                stmt.setInt(2, 102);
                stmt.executeUpdate();
            }

            return bookingId;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return bookingId;
        }
    }

    public int WithTransaction() {
        String insertBooking = "INSERT INTO booking (user_id, room_id, timeslot_id, status) VALUES (?, ?, ?, ?) RETURNING id";
        String insertParticipant = "INSERT INTO booking_participant (booking_id, user_id) VALUES (?, ?)";

        int bookingId = -1;

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(insertBooking)) {
                stmt.setInt(1, 101);
                stmt.setInt(2, 2);
                stmt.setInt(3, 6);
                stmt.setString(4, "PENDING");
                ResultSet rs = stmt.executeQuery();
                rs.next();
                bookingId = rs.getInt(1);
            }

            if (true) throw new RuntimeException("Simulated crash before inserting participant");

            try (PreparedStatement stmt = conn.prepareStatement(insertParticipant)) {
                stmt.setInt(1, bookingId);
                stmt.setInt(2, 102);
                stmt.executeUpdate();
            }

            conn.commit();
            return bookingId;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return bookingId;
        }
    }

    public boolean bookingExists(int bookingId) {
        String query = "SELECT COUNT(*) FROM booking WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    public void deleteBooking(int bookingId) {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM booking_participant WHERE booking_id = " + bookingId);
            stmt.executeUpdate("DELETE FROM booking WHERE id = " + bookingId);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void readRoomTwiceWithDelay(int roomId, int isolationLevel, ArrayList<Integer> readResults) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(isolationLevel);

            String select = "SELECT capacity FROM room WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(select)) {
                stmt.setInt(1, roomId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    readResults.add(rs.getInt(1));
                }
            }

            Thread.sleep(5000);

            try (PreparedStatement stmt = conn.prepareStatement(select)) {
                stmt.setInt(1, roomId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    readResults.add(rs.getInt(1));
                }
            }

            conn.commit();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void updateRoomCapacity(int roomId, int newCapacity) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            String update = "UPDATE room SET capacity = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(update)) {
                stmt.setInt(1, newCapacity);
                stmt.setInt(2, roomId);
                stmt.executeUpdate();
            }
            conn.commit();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void populateRooms() {
        String sql = "INSERT INTO room (name, type, capacity) VALUES (?, ?, ?)";
        Random rand = new Random();
        String[] types = {"Classroom", "Lab", "Hall", "Office"};

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);
            for (int i = 1; i <= 1000000; i++) {
                stmt.setString(1, "Room " + i);
                stmt.setString(2, types[rand.nextInt(types.length)]);
                stmt.setInt(3, rand.nextInt(100)); // 0–99
                stmt.addBatch();

                if (i % 10000 == 0) {
                    stmt.executeBatch();
                    conn.commit();
//                    System.out.println("Inserted: " + i);
                }
            }

            stmt.executeBatch();
            conn.commit();
            System.out.println("Finished inserting 1 million rooms.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void runQuery() {
        String sql = "EXPLAIN ANALYZE SELECT id FROM room WHERE capacity > 20";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void createIndex() {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("CREATE INDEX id_room_capacity ON room(id, capacity)")) {
            stmt.execute();
            System.out.println("Index created on capacity.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Task 3
    public static void main(String[] args) {
        RDBMSDemo demo = new RDBMSDemo();

        demo.populateRooms();

        long start = System.currentTimeMillis();
        demo.runQuery();
        long end = System.currentTimeMillis();
        System.out.println("Non-Indexed Time: " + (end - start) + "ms");

        demo.createIndex();
        start = System.currentTimeMillis();
        demo.runQuery();
        end = System.currentTimeMillis();
        System.out.println("Indexed Time: " + (end - start) + "ms");
    }

}
