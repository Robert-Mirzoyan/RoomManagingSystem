package Pooling;

import Util.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;

public class ConnectionTestRunner {
    public static void main(String[] args) throws InterruptedException, SQLException {
        DataSource dataSource = new SingleConnectionDataSource(
                "jdbc:postgresql://localhost:5432/mydb", "rob", "12345678");

        CountDownLatch latch = new CountDownLatch(5);

        long start = System.currentTimeMillis();

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    Connection conn = dataSource.getConnection();
                    Statement stmt = conn.createStatement();
                    stmt.execute("SELECT pg_sleep(3)");
                    stmt.close();
                    System.out.println(Thread.currentThread().getName() + " done");
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }

            }).start();
        }

        latch.await();
        long end = System.currentTimeMillis();

        System.out.println("All threads finished in " + (end - start) / 1000.0 + " seconds.");

        dataSource.getConnection().close();
    }
}