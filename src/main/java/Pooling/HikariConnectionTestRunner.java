package Pooling;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;

public class HikariConnectionTestRunner {
    public static void main(String[] args) throws InterruptedException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/mydb");
        config.setUsername("rob");
        config.setPassword("12345678");
        config.setMaximumPoolSize(5);
        config.setConnectionTimeout(10000);

        HikariDataSource dataSource = new HikariDataSource(config);

        CountDownLatch latch = new CountDownLatch(5);

        long start = System.currentTimeMillis();

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try (Connection conn = dataSource.getConnection();
                     Statement stmt = conn.createStatement()) {
                    stmt.execute("SELECT pg_sleep(3)");
                    stmt.close();
                    System.out.println(Thread.currentThread().getName() + " done");
                } catch (SQLException e) {
                    System.out.println(Thread.currentThread().getName() + " failed");
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        latch.await();
        long end = System.currentTimeMillis();

        System.out.println("All threads finished in " + (end - start) / 1000.0 + " seconds.");

        dataSource.close();
    }
}
