package com.naujokaitis.maistas.api.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@DiscriminatorValue("ADMIN")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Administrator extends User {

    public Administrator(UUID id, String username, String password, String email, String phone, UserStatus status) {
        super(id, username, password, email, phone, status, UserRole.ADMIN);
    }

    public static Administrator create(String username, String password, String email, String phone) {
        return new Administrator(UUID.randomUUID(), username, password, email, phone, UserStatus.ACTIVE);
    }
}
