package com.naujokaitis.maistas.api.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class User {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Getter(AccessLevel.NONE)
    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    public User(UUID id, String username, String password, String email, String phone, UserStatus status, UserRole role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.role = role;
    }

    public String getPasswordHash() {
        return password;
    }

    public void setPasswordHash(String passwordHash) {
        this.password = passwordHash;
    }

    public boolean authenticate(String password) {
        if (password == null || this.password == null) {
            return false;
        }
        // BCrypt password check
        if (this.password.startsWith("$2a$") || this.password.startsWith("$2b$") || this.password.startsWith("$2y$")) {
            return org.mindrot.jbcrypt.BCrypt.checkpw(password, this.password);
        }
        // Plain text comparison for non-hashed passwords
        return this.password.equals(password);
    }
}
