package com.naujokaitis.maistas.api.repository;

import com.naujokaitis.maistas.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findByUsernameAndPassword(String username, String password);
}
