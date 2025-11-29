package com.naujokaitis.maistas.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@DiscriminatorValue("RESTAURANT_OWNER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestaurantOwner extends User {

    @Getter(AccessLevel.NONE)
    @Transient
    private List<Restaurant> managedRestaurants = new ArrayList<>();

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
