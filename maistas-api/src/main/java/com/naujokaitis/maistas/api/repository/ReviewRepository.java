package com.naujokaitis.maistas.api.repository;

import com.naujokaitis.maistas.api.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByTargetRestaurantIdOrderByCreatedAtDesc(UUID restaurantId);
    List<Review> findByTargetUserIdOrderByCreatedAtDesc(UUID userId);
    List<Review> findByAuthorIdOrderByCreatedAtDesc(UUID authorId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.targetRestaurant.id = :restaurantId")
    Double getAverageRatingForRestaurant(@Param("restaurantId") UUID restaurantId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.targetUser.id = :userId")
    Double getAverageRatingForUser(@Param("userId") UUID userId);
}
