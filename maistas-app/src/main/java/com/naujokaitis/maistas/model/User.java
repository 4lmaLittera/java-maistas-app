package com.naujokaitis.maistas.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class User {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Getter(AccessLevel.PROTECTED)
    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    public User(@NonNull UUID id,
            @NonNull String username,
            @NonNull String password,
            @NonNull String email,
            @NonNull String phone,
            @NonNull UserStatus status,
            @NonNull UserRole role) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.password = Objects.requireNonNull(password, "password must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.phone = Objects.requireNonNull(phone, "phone must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.role = Objects.requireNonNull(role, "role must not be null");
    }

    public String getPasswordHash() {
        return password;
    }

    public boolean authenticate(String password) {
        if (password == null || this.password == null) {
            return false;
        }
        if (this.password.startsWith("$2a$") || this.password.startsWith("$2b$") || this.password.startsWith("$2y$")) {
            return BCrypt.checkpw(password, this.password);
        }
        return this.password.equals(password);
    }
}
