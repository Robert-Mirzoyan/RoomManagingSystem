package Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class JdbcUtil {
    private static final String URL = "jdbc:postgresql://localhost:5432/mydb";
    private static final String USER = "rob";
    private static final String PASSWORD = "12345678";

    public static void execute(String query, Object... args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < args.length; i++) {
                stmt.setObject(i + 1, args[i]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error: " + query, e);
        }
    }

    public static void execute(String query, Consumer<PreparedStatement> consumer) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(query)) {
            consumer.accept(stmt);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error: " + query, e);
        }
    }

    //The first method (a) is convenient for straightforward queries, automatically mapping parameters using a loop.
    //The second method (b) offers more flexibility by letting you manually set parameters with full control using a lambda function.

    public static <T> T findOne(String query, Function<ResultSet, T> mapper, Object... args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < args.length; i++) {
                stmt.setObject(i + 1, args[i]);
            }
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            }
            T result = mapper.apply(rs);
            if (rs.next()) {
                throw new RuntimeException("Multiple rows returned!");
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error executing findOne: " + query, e);
        }
    }

    public static <T> List<T> findMany(String query, Function<ResultSet, T> mapper, Object... args) {
        List<T> results = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < args.length; i++) {
                stmt.setObject(i + 1, args[i]);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(mapper.apply(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing findMany: " + query, e);
        }
        return results;
    }
}