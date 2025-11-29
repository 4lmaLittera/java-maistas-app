package com.naujokaitis.maistas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.naujokaitis.maistas.model.User;
import java.util.UUID;

public class UserDAO {

    private final Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean saveUserToDatabase(User user) throws SQLException {
        switch (user.getRole()) {
            case CLIENT:
                return false;
            case RESTAURANT_OWNER:
                return false;
            case DRIVER:
                return false;
            case ADMIN:
                String sql = "INSERT INTO users (id, username, password_hash, email, phone, status, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, user.getId().toString());
                statement.setString(2, user.getUsername());
                statement.setString(3, user.getPasswordHash());
                statement.setString(4, user.getEmail());
                statement.setString(5, user.getPhone());
                statement.setString(6, user.getStatus().toString());
                statement.setString(7, user.getRole().toString());
                statement.executeUpdate();
                return true;
            default:
                return false;
        }
    }
}