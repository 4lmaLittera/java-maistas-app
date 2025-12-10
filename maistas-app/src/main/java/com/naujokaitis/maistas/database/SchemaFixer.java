package com.naujokaitis.maistas.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class SchemaFixer {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/maistas_db?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8";
        String user = "root";
        String password = "rootpassword";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            System.out.println("Connected to database. Attempting to fix schema...");

            // Make wallet_balance nullable
            String sql1 = "ALTER TABLE users MODIFY wallet_balance DECIMAL(10,2) NULL";
            try {
                stmt.execute(sql1);
                System.out.println("Successfully modified wallet_balance to be NULLABLE.");
            } catch (Exception e) {
                System.out.println("Error modifying wallet_balance: " + e.getMessage());
            }

            // Make default_address nullable (just in case)
            String sql2 = "ALTER TABLE users MODIFY default_address VARCHAR(255) NULL";
            try {
                stmt.execute(sql2);
                System.out.println("Successfully modified default_address to be NULLABLE.");
            } catch (Exception e) {
                System.out.println("Error modifying default_address: " + e.getMessage());
            }

            System.out.println("Schema fix completed.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
