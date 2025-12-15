package com.naujokaitis.maistas.api.repository;

import com.naujokaitis.maistas.api.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
}
