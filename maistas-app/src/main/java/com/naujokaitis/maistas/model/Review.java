package com.naujokaitis.maistas.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Review {

    private final UUID id;
    private final User author;
    private final User targetUser;
    private final Restaurant targetRestaurant;
    private int rating;
    @Setter
    private String comment;
    private final LocalDateTime createdAt;

    public Review(UUID id,
            User author,
            User targetUser,
            Restaurant targetRestaurant,
            int rating,
            String comment,
            LocalDateTime createdAt) {

        if (targetUser == null && targetRestaurant == null) {
            throw new IllegalArgumentException("Review target must be a user or a restaurant");
        }

        this.id = Objects.requireNonNull(id, "id must not be null");
        this.author = Objects.requireNonNull(author, "author must not be null");
        this.targetUser = targetUser;
        this.targetRestaurant = targetRestaurant;
        this.rating = validateRating(rating);
        this.comment = comment;
        this.createdAt = Objects.requireNonNullElse(createdAt, LocalDateTime.now());
    }

    public void updateRating(int newRating) {
        this.rating = validateRating(newRating);
    }

    private int validateRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        return rating;
    }
}