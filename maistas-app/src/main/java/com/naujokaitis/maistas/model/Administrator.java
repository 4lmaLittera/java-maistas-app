package com.naujokaitis.maistas.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@DiscriminatorValue("ADMIN")
@Getter
@NoArgsConstructor(force = true)
public class Administrator extends User {

    public Administrator(UUID id,
            String username,
            String passwordHash,
            String email,
            String phone) {
        this(id, username, passwordHash, email, phone, UserStatus.ACTIVE);
    }

    public Administrator(UUID id,
            String username,
            String passwordHash,
            String email,
            String phone,
            UserStatus status) {
        super(id, username, passwordHash, email, phone, status, UserRole.ADMIN);
    }

    public void suspendUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }

        user.setStatus(UserStatus.SUSPENDED);
    }

    public void manageRoles(User user, UserRole role) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        user.setRole(role);
    }
}
