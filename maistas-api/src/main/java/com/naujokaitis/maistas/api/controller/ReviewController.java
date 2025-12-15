package com.naujokaitis.maistas.api.controller;

import com.naujokaitis.maistas.api.exception.ResourceNotFoundException;
import com.naujokaitis.maistas.api.model.Restaurant;
import com.naujokaitis.maistas.api.model.Review;
import com.naujokaitis.maistas.api.model.User;
import com.naujokaitis.maistas.api.repository.RestaurantRepository;
import com.naujokaitis.maistas.api.repository.ReviewRepository;
import com.naujokaitis.maistas.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    // POST - Create Review
    @PostMapping("/")
    public Review createReview(@RequestBody ReviewRequest request) {
        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author", request.getAuthorId()));

        User targetUser = null;
        if (request.getTargetUserId() != null) {
            targetUser = userRepository.findById(request.getTargetUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Target User", request.getTargetUserId()));
        }

        Restaurant targetRestaurant = null;
        if (request.getTargetRestaurantId() != null) {
            targetRestaurant = restaurantRepository.findById(request.getTargetRestaurantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Target Restaurant", request.getTargetRestaurantId()));
        }

        Review review = new Review(
                UUID.randomUUID(),
                author,
                targetUser,
                targetRestaurant,
                request.getRating(),
                request.getComment(),
                LocalDateTime.now()
        );

        Review savedReview = reviewRepository.save(review);
        
        // Recalculate Rating
        if (targetRestaurant != null) {
            Double newRating = reviewRepository.getAverageRatingForRestaurant(targetRestaurant.getId());
            targetRestaurant.setRating(newRating);
            restaurantRepository.save(targetRestaurant);
        }
        // User rating update logic could go here too (requires rating field in User?)

        return savedReview;
    }

    // GET - Reviews for Restaurant
    @GetMapping("/restaurant/{restaurantId}")
    public List<Review> getRestaurantReviews(@PathVariable UUID restaurantId) {
        return reviewRepository.findByTargetRestaurantIdOrderByCreatedAtDesc(restaurantId);
    }
    
    // GET - Reviews for User (Driver/Client)
    @GetMapping("/user/{userId}")
    public List<Review> getUserReviews(@PathVariable UUID userId) {
        return reviewRepository.findByTargetUserIdOrderByCreatedAtDesc(userId);
    }

    // DTO
    public static class ReviewRequest {
        private UUID authorId;
        private UUID targetUserId;
        private UUID targetRestaurantId;
        private int rating;
        private String comment;

        public UUID getAuthorId() { return authorId; }
        public void setAuthorId(UUID authorId) { this.authorId = authorId; }
        public UUID getTargetUserId() { return targetUserId; }
        public void setTargetUserId(UUID targetUserId) { this.targetUserId = targetUserId; }
        public UUID getTargetRestaurantId() { return targetRestaurantId; }
        public void setTargetRestaurantId(UUID targetRestaurantId) { this.targetRestaurantId = targetRestaurantId; }
        public int getRating() { return rating; }
        public void setRating(int rating) { this.rating = rating; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
    }
}
