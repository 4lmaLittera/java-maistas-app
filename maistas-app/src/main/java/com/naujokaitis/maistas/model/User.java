package com.naujokaitis.maistas.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
public abstract class User {

    @NonNull
    private final UUID id;
    @NonNull
    private final String username;
    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final String password;
    @NonNull
    private final String email;
    @NonNull
    private final String phone;
    @Setter(AccessLevel.PACKAGE)
    @NonNull
    private UserStatus status;
    @Setter
    @NonNull
    private UserRole role;

    protected User(UUID id,
            String username,
            String password,
            String email,
            String phone,
            UserStatus status,
            UserRole role) {
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
        return this.password.equals(password);
    }
}
