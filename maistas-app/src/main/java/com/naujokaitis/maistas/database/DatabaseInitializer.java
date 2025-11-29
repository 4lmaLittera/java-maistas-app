package com.naujokaitis.maistas.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Sukurti users lentelę
            createUsersTable(stmt);
            
            // Sukurti restaurants lentelę
            createRestaurantsTable(stmt);
            
            // Sukurti menus lentelę
            createMenusTable(stmt);
            
            // Sukurti menu_items lentelę
            createMenuItemsTable(stmt);
            
            // Sukurti orders lentelę
            createOrdersTable(stmt);
            
            // Sukurti order_items lentelę
            createOrderItemsTable(stmt);
            
            // Pridėti kitas lenteles pagal poreikį...
            
            System.out.println("Database initialized successfully");
            
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createUsersTable(Statement stmt) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id VARCHAR(36) PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                password_hash VARCHAR(255) NOT NULL,
                email VARCHAR(100) NOT NULL,
                phone VARCHAR(20) NOT NULL,
                status VARCHAR(20) NOT NULL,
                role VARCHAR(20) NOT NULL,
                -- Client specific fields
                default_address VARCHAR(200),
                loyalty_points INT DEFAULT 0,
                -- Driver specific fields
                vehicle_type VARCHAR(20),
                available BOOLEAN DEFAULT FALSE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            )
            """;
        stmt.execute(sql);
    }

    private static void createRestaurantsTable(Statement stmt) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS restaurants (
                id VARCHAR(36) PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                address VARCHAR(200) NOT NULL,
                description TEXT,
                rating DECIMAL(3,2),
                owner_id VARCHAR(36),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (owner_id) REFERENCES users(id)
            )
            """;
        stmt.execute(sql);
    }

    private static void createMenusTable(Statement stmt) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS menus (
                id VARCHAR(36) PRIMARY KEY,
                restaurant_id VARCHAR(36) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
            )
            """;
        stmt.execute(sql);
    }

    private static void createMenuItemsTable(Statement stmt) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS menu_items (
                id VARCHAR(36) PRIMARY KEY,
                menu_id VARCHAR(36) NOT NULL,
                name VARCHAR(100) NOT NULL,
                description TEXT,
                price DECIMAL(10,2) NOT NULL,
                category VARCHAR(50),
                inventory_count INT DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (menu_id) REFERENCES menus(id)
            )
            """;
        stmt.execute(sql);
    }

    private static void createOrdersTable(Statement stmt) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS orders (
                id VARCHAR(36) PRIMARY KEY,
                client_id VARCHAR(36) NOT NULL,
                restaurant_id VARCHAR(36) NOT NULL,
                driver_id VARCHAR(36),
                total_price DECIMAL(10,2) NOT NULL,
                delivery_address VARCHAR(200) NOT NULL,
                status VARCHAR(20) NOT NULL,
                placed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (client_id) REFERENCES users(id),
                FOREIGN KEY (restaurant_id) REFERENCES restaurants(id),
                FOREIGN KEY (driver_id) REFERENCES users(id)
            )
            """;
        stmt.execute(sql);
    }

    private static void createOrderItemsTable(Statement stmt) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS order_items (
                id VARCHAR(36) PRIMARY KEY,
                order_id VARCHAR(36) NOT NULL,
                menu_item_id VARCHAR(36) NOT NULL,
                quantity INT NOT NULL,
                unit_price DECIMAL(10,2) NOT NULL,
                special_instructions TEXT,
                FOREIGN KEY (order_id) REFERENCES orders(id),
                FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)
            )
            """;
        stmt.execute(sql);
    }
}



