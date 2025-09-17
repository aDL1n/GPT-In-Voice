package dev.adlin.database.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.adlin.database.DataBase;
import dev.adlin.llm.adapters.Role;
import dev.adlin.llm.memory.LongTermMemoryData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SQLite implements DataBase {

    private static final Logger log = LogManager.getLogger(SQLite.class);

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private Connection connection;

    public SQLite() {
    }

    @Override
    public void load() {
        try {
            DataSource dataSource = this.createDataSource(new File("src/main/resources/database/LTM.db"));
            connection = dataSource.getConnection();
            log.info("Database connected!");
        } catch (Exception e) {
            log.error("Database connection error", e);
        }

    }

    @Override
    public void createLongTermMemoryTable() {
        CompletableFuture.runAsync(() -> {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS data (id INTEGER PRIMARY KEY AUTOINCREMENT, role VARCHAR(1) NOT NULL, username VARCHAR(32), message TEXT, date INTEGER NOT NULL)");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void saveLongTermMemory(LongTermMemoryData memoryData) {
        CompletableFuture.runAsync(() -> {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO data (role, username, message, date) VALUES (?, ?, ?, ?)"
            )) {
                statement.setString(1, memoryData.role.getName());
                statement.setString(2, memoryData.userName);
                statement.setString(3, memoryData.message);
                statement.setLong(4, memoryData.date.getTime());

                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void saveLongTermMemories(List<LongTermMemoryData> memoryDataList) {
        CompletableFuture.runAsync(() -> {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO data (role, username, message, date) VALUES (?, ?, ?, ?)"
            )) {
                for (LongTermMemoryData memoryData : memoryDataList) {
                    statement.setString(1, memoryData.role.getName());
                    statement.setString(2, memoryData.userName);
                    statement.setString(3, memoryData.message);
                    statement.setLong(4, memoryData.date.getTime());

                    statement.addBatch();
                }

                statement.executeBatch();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<List<LongTermMemoryData>> getAllLongTermMemories() {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement prepareStatement = connection.prepareStatement("SELECT * FROM data ORDER BY date ASC");
                 ResultSet resultSet = prepareStatement.executeQuery()
            ) {
                List<LongTermMemoryData> result = new ArrayList<>();

                while (resultSet.next()) {
                    Role role = Role.Utils.getRoleFromDB(resultSet.getString("role"));
                    String username = resultSet.getString("username");
                    String message = resultSet.getString("message");
                    Date date = new Date(resultSet.getLong("date"));
                    result.add(new LongTermMemoryData(role, date, username, message));
                }

                return result;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<List<LongTermMemoryData>> getLongTermMemories(int length) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT role, username, message, date FROM data ORDER BY date DESC LIMIT ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, length);
                try (ResultSet rs = ps.executeQuery()) {

                    List<LongTermMemoryData> result = new ArrayList<>();
                    while (rs.next()) {
                        Role role = Role.Utils.getRoleFromDB(rs.getString("role"));
                        String username = rs.getString("username");
                        String message = rs.getString("message");
                        Date date = new Date(rs.getLong("date"));
                        result.add(new LongTermMemoryData(role, date, username, message));
                    }

                    Collections.reverse(result);
                    return result;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private DataSource createDataSource(File dbFile) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + dbFile);
        config.setAutoCommit(true);

        return new HikariDataSource(config);
    }
}
