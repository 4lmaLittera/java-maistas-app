package com.naujokaitis.maistas.api.repository;

import com.naujokaitis.maistas.api.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {
    List<MenuItem> findByMenuId(UUID menuId);
}
