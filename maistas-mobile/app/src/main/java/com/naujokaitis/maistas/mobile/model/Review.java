package com.naujokaitis.maistas.mobile.model;

import java.util.UUID;

public class Review {
    private UUID id;
    private UUID authorId;
    private UUID targetRestaurantId;
    private UUID targetUserId;
    private int rating;
    private String comment;
    private String createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getAuthorId() { return authorId; }
    public void setAuthorId(UUID authorId) { this.authorId = authorId; }

    public UUID getTargetRestaurantId() { return targetRestaurantId; }
    public void setTargetRestaurantId(UUID targetRestaurantId) { this.targetRestaurantId = targetRestaurantId; }

    public UUID getTargetUserId() { return targetUserId; }
    public void setTargetUserId(UUID targetUserId) { this.targetUserId = targetUserId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
