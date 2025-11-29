package com.naujokaitis.maistas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.naujokaitis.maistas.model.User;
import com.naujokaitis.maistas.model.UserRole;
import com.naujokaitis.maistas.model.Administrator;
import com.naujokaitis.maistas.model.RestaurantOwner;
import com.naujokaitis.maistas.model.Client;
import com.naujokaitis.maistas.model.Driver;
import com.naujokaitis.maistas.model.VehicleType;
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
                String sqlOwner = "INSERT INTO users (id, username, password_hash, email, phone, status, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmtOwner = connection.prepareStatement(sqlOwner);
                stmtOwner.setString(1, user.getId().toString());
                stmtOwner.setString(2, user.getUsername());
                stmtOwner.setString(3, user.getPasswordHash());
                stmtOwner.setString(4, user.getEmail());
                stmtOwner.setString(5, user.getPhone());
                // default to ACTIVE if UserStatus available
                stmtOwner.setString(6, (user.getStatus() != null) ? user.getStatus().toString() : "ACTIVE");
                stmtOwner.setString(7, user.getRole().toString());
                stmtOwner.executeUpdate();
                return true;
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

    public String getPasswordHashByUsername(String username) throws SQLException {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password_hash");
                }
                return null;
            }
        }
    }

    public String getUserIdByUsername(String username) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("id");
                }
                return null;
            }
        }
    }

    public void updatePasswordHashById(String id, String newHash) throws SQLException {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setString(2, id);
            ps.executeUpdate();
        }
    }

    public User loadUserByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password_hash, email, phone, status, role FROM users WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                UUID id = UUID.fromString(rs.getString("id"));
                String uname = rs.getString("username");
                String pwdHash = rs.getString("password_hash");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                UserRole role = UserRole.valueOf(rs.getString("role"));

                switch (role) {
                    case ADMIN:
                        return new Administrator(id, uname, pwdHash, email, phone);
                    case RESTAURANT_OWNER:
                        return new RestaurantOwner(id, uname, pwdHash, email, phone);
                    case CLIENT:
                        return new Client(id, uname, pwdHash, email, phone, "", 0, java.util.List.of());
                    case DRIVER:
                        return new Driver(id, uname, pwdHash, email, phone, VehicleType.CAR, true);
                    default:
                        return null;
                }
            }
        }
    }
}