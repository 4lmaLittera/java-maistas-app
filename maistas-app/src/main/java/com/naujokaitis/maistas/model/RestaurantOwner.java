package com.naujokaitis.maistas.model;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class RestaurantOwner extends User {

    @Getter(AccessLevel.NONE)
    private final List<Restaurant> managedRestaurants;

    public RestaurantOwner(UUID id,
            String username,
            String passwordHash,
            String email,
            String phone) {
        this(id, username, passwordHash, email, phone, UserStatus.ACTIVE, List.of());
    }

    public RestaurantOwner(UUID id,
            String username,
            String passwordHash,
            String email,
            String phone,
            UserStatus status,
            List<Restaurant> managedRestaurants) {
        super(id, username, passwordHash, email, phone, status, UserRole.RESTAURANT_OWNER);
        this.managedRestaurants = new ArrayList<>(Objects.requireNonNullElse(managedRestaurants, List.of()));
    }

    public List<Restaurant> getManagedRestaurants() {
        return Collections.unmodifiableList(managedRestaurants);
    }

    public void addRestaurant(Restaurant restaurant) {
        managedRestaurants.add(Objects.requireNonNull(restaurant, "restaurant must not be null"));
    }

    public void removeRestaurant(Restaurant restaurant) {
        managedRestaurants.remove(restaurant);
    }
}
