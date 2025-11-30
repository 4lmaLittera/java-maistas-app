package com.naujokaitis.maistas.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private User targetUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_restaurant_id")
    private Restaurant targetRestaurant;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Setter
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

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