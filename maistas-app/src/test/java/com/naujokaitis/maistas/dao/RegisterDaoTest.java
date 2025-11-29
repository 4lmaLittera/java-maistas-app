package com.naujokaitis.maistas.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.naujokaitis.maistas.model.Administrator;
import com.naujokaitis.maistas.model.Client;
import com.naujokaitis.maistas.model.Driver;
import com.naujokaitis.maistas.model.RestaurantOwner;
import com.naujokaitis.maistas.model.VehicleType;

public class RegisterDaoTest {

    private Connection connection;
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() throws SQLException {
        // Use H2 in-memory database for tests
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        try (Statement stmt = connection.createStatement()) {
            String create = "CREATE TABLE users ("
                    + "id VARCHAR(36) PRIMARY KEY,"
                    + "username VARCHAR(50),"
                    + "password_hash VARCHAR(255),"
                    + "email VARCHAR(255),"
                    + "phone VARCHAR(20),"
                    + "status VARCHAR(50),"
                    + "role VARCHAR(50)"
                    + ");";
            stmt.execute(create);
        }
        userDAO = new UserDAO(connection);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("DROP TABLE users");
            }
            connection.close();
        }
    }

    @Test
    public void saveAdminAndOwnerShouldPersist() throws SQLException {
        Administrator admin = new Administrator(UUID.randomUUID(), "admin1", "passhash", "admin@example.com", "+37060000000");
        RestaurantOwner owner = new RestaurantOwner(UUID.randomUUID(), "owner1", "ownerhash", "owner@example.com", "+37060000001");

        assertTrue(userDAO.saveUserToDatabase(admin), "Admin should be saved");
        assertTrue(userDAO.saveUserToDatabase(owner), "Restaurant owner should be saved");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertTrue(rs.next());
            int count = rs.getInt(1);
            assertEquals(2, count);
        }
    }

    @Test
    public void clientAndDriverShouldNotBeSaved() throws SQLException {
        Client client = new Client(UUID.randomUUID(), "client1", "cpass", "c@example.com", "+37060000002", "Some address", 0, java.util.List.of());
        Driver driver = new Driver(UUID.randomUUID(), "driver1", "dpass", "d@example.com", "+37060000003", VehicleType.CAR, true);

        assertFalse(userDAO.saveUserToDatabase(client));
        assertFalse(userDAO.saveUserToDatabase(driver));

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertTrue(rs.next());
            int count = rs.getInt(1);
            assertEquals(0, count);
        }
    }
}
