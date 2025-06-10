import Pooling.RDBMSDemo;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class RDBMSDemoTest {

    RDBMSDemo demo = new RDBMSDemo();
    int roomId = 2;

    @Test
    public void testWithoutTransaction() {
        int bookingId = demo.WithoutTransaction();
        assertTrue(demo.bookingExists(bookingId));
        demo.deleteBooking(bookingId);
    }

    @Test
    public void testWithTransaction() {
        int bookingId = demo.WithTransaction();
        assertFalse(demo.bookingExists(bookingId));
    }

    @Test
    public void testReadCommitted() throws InterruptedException {
        demo.updateRoomCapacity(roomId, 100);
        ArrayList<Integer> reads = new ArrayList<>();

        Thread reader = new Thread(() -> demo.readRoomTwiceWithDelay(roomId, Connection.TRANSACTION_READ_COMMITTED, reads));
        Thread writer = new Thread(() -> {
            try {
                Thread.sleep(2000);
                demo.updateRoomCapacity(roomId, 200);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        });

        reader.start();
        writer.start();
        reader.join();
        writer.join();

        assertEquals(2, reads.size());
        assertNotEquals(reads.get(0), reads.get(1));
    }

    @Test
    public void testRepeatableRead() throws InterruptedException {
        demo.updateRoomCapacity(roomId, 100);
        ArrayList<Integer> reads = new ArrayList<>();

        Thread reader = new Thread(() -> demo.readRoomTwiceWithDelay(roomId, Connection.TRANSACTION_REPEATABLE_READ, reads));
        Thread writer = new Thread(() -> {
            try {
                Thread.sleep(2000);
                demo.updateRoomCapacity(roomId, 200);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        });

        reader.start();
        writer.start();
        reader.join();
        writer.join();

        assertEquals(2, reads.size());
        assertEquals(reads.get(0), reads.get(1));
    }
}
