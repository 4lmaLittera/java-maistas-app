package com.naujokaitis.maistas.api.repository;

import com.naujokaitis.maistas.api.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MenuRepository extends JpaRepository<Menu, UUID> {
}
