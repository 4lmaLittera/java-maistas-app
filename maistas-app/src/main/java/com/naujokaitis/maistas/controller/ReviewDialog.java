package com.naujokaitis.maistas.controller;

import com.naujokaitis.maistas.database.CustomHibernate;
import com.naujokaitis.maistas.database.GenericHibernate;
import com.naujokaitis.maistas.model.Administrator;
import com.naujokaitis.maistas.model.Client;
import com.naujokaitis.maistas.model.Driver;
import com.naujokaitis.maistas.model.Restaurant;
import com.naujokaitis.maistas.model.Review;
import com.naujokaitis.maistas.model.Session;
import com.naujokaitis.maistas.model.User;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ReviewDialog extends Dialog<Void> {

    private Restaurant restaurant;
    private User targetUser;
    private final GenericHibernate<Review> reviewRepo = new GenericHibernate<>(Review.class);
    private final CustomHibernate customRepo = new CustomHibernate();
    private ListView<Review> reviewsList;
    private Button editBtn;
    private Button deleteBtn;
    private final boolean isRestaurantReview;

    public ReviewDialog(Restaurant restaurant) {
        this.restaurant = restaurant;
        this.isRestaurantReview = true;
        setupDialog("Reviews for " + restaurant.getName(), "Customer Reviews");
    }

    public ReviewDialog(User targetUser) {
        this.targetUser = targetUser;
        this.isRestaurantReview = false;
        setupDialog("Reviews for " + targetUser.getUsername(), "User Reviews");
    }

    private void setupDialog(String title, String header) {
        setTitle(title);
        setHeaderText(header);

        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().add(closeButtonType);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.setPrefWidth(450);
        content.setPrefHeight(550);

        // List of existing reviews
        reviewsList = new ListView<>();
        reviewsList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Review review, boolean empty) {
                super.updateItem(review, empty);
                if (empty || review == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox box = new VBox(5);
                    Label authorLabel = new Label(review.getAuthor().getUsername() + " - " + review.getRating() + "/5");
                    authorLabel.setStyle("-fx-font-weight: bold");
                    Label dateLabel = new Label(review.getCreatedAt().toString());
                    dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
                    Label commentLabel = new Label(review.getComment());
                    commentLabel.setWrapText(true);

                    box.getChildren().addAll(authorLabel, dateLabel, commentLabel);
                    setGraphic(box);
                }
            }
        });
        loadReviews();

        // Edit and Delete buttons
        editBtn = new Button("Edit");
        editBtn.setDisable(true);
        editBtn.setOnAction(e -> handleEditReview());

        deleteBtn = new Button("Delete");
        deleteBtn.setDisable(true);
        deleteBtn.setOnAction(e -> handleDeleteReview());

        HBox reviewButtons = new HBox(10, editBtn, deleteBtn);

        // Update button states based on selection
        reviewsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateButtonStates(newVal);
        });

        // Add new review section
        TitledPane addReviewPane = new TitledPane();
        addReviewPane.setText("Write a Review");
        addReviewPane.setCollapsible(false);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        ComboBox<Integer> ratingBox = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        ratingBox.setPromptText("Rating");

        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Your comment...");
        commentArea.setPrefRowCount(3);

        Button submitBtn = new Button("Submit Review");
        submitBtn.setOnAction(e -> {
            if (validateReview(ratingBox.getValue(), commentArea.getText())) {
                createReview(ratingBox.getValue(), commentArea.getText());
                ratingBox.setValue(null);
                commentArea.clear();
                loadReviews();
            }
        });

        grid.add(new Label("Rating:"), 0, 0);
        grid.add(ratingBox, 1, 0);
        grid.add(new Label("Comment:"), 0, 1);
        grid.add(commentArea, 1, 1);
        grid.add(submitBtn, 1, 2);

        addReviewPane.setContent(grid);

        content.getChildren().addAll(new Label("Reviews:"), reviewsList, reviewButtons);

        // Only allow Client and Driver to write reviews
        User currentUser = Session.getInstance().getCurrentUser();
        if (currentUser instanceof Client || currentUser instanceof Driver) {
            content.getChildren().add(addReviewPane);
        }

        getDialogPane().setContent(content);
    }

    private void updateButtonStates(Review selectedReview) {
        User currentUser = Session.getInstance().getCurrentUser();
        if (selectedReview == null || currentUser == null) {
            editBtn.setDisable(true);
            deleteBtn.setDisable(true);
            return;
        }

        boolean isAuthor = selectedReview.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser instanceof Administrator;

        // Edit: only author (Client/Driver) can edit
        editBtn.setDisable(!isAuthor);

        // Delete: author (Client/Driver) OR Admin can delete
        deleteBtn.setDisable(!isAuthor && !isAdmin);
    }

    private void handleEditReview() {
        Review selected = reviewsList.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        User currentUser = Session.getInstance().getCurrentUser();
        if (!selected.getAuthor().getId().equals(currentUser.getId())) {
            showAlert("You can only edit your own reviews");
            return;
        }

        // Create edit dialog
        Dialog<Review> editDialog = new Dialog<>();
        editDialog.setTitle("Edit Review");
        editDialog.setHeaderText("Edit your review");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        editDialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        ComboBox<Integer> ratingBox = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        ratingBox.setValue(selected.getRating());

        TextArea commentArea = new TextArea(selected.getComment());
        commentArea.setPrefRowCount(3);

        grid.add(new Label("Rating:"), 0, 0);
        grid.add(ratingBox, 1, 0);
        grid.add(new Label("Comment:"), 0, 1);
        grid.add(commentArea, 1, 1);

        editDialog.getDialogPane().setContent(grid);

        editDialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (ratingBox.getValue() != null && commentArea.getText() != null
                        && !commentArea.getText().trim().isEmpty()) {
                    selected.updateRating(ratingBox.getValue());
                    selected.setComment(commentArea.getText().trim());
                    return selected;
                }
            }
            return null;
        });

        Optional<Review> result = editDialog.showAndWait();
        result.ifPresent(review -> {
            try {
                reviewRepo.update(review);
                if (isRestaurantReview) {
                    customRepo.updateRestaurantRating(restaurant.getId());
                }
                loadReviews();
            } catch (Exception e) {
                showAlert("Failed to update review: " + e.getMessage());
            }
        });
    }

    private void handleDeleteReview() {
        Review selected = reviewsList.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        User currentUser = Session.getInstance().getCurrentUser();
        boolean isAuthor = selected.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser instanceof Administrator;

        if (!isAuthor && !isAdmin) {
            showAlert("You don't have permission to delete this review");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Review?");
        confirm.setContentText("Are you sure you want to delete this review?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    reviewRepo.delete(selected);
                    if (isRestaurantReview) {
                        customRepo.updateRestaurantRating(restaurant.getId());
                    }
                    loadReviews();
                } catch (Exception e) {
                    showAlert("Failed to delete review: " + e.getMessage());
                }
            }
        });
    }

    private void loadReviews() {
        try {
            List<Review> reviews;
            if (isRestaurantReview) {
                reviews = customRepo.findReviewsByRestaurantId(restaurant.getId());
                // Also load reviews where this restaurant is the target? 
                // Wait, requirements say "Reviews about clients... and drivers".
                // Review model has targetUser and targetRestaurant.
                // findReviewsByRestaurantId likely queries by targetRestaurant.
            } else {
                // For users, we need to fetch reviews where targetUser is this user
                 reviews = customRepo.findReviewsByUserId(targetUser.getId());
            }
            if (reviews != null) {
                reviewsList.setItems(FXCollections.observableArrayList(reviews));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateReview(Integer rating, String comment) {
        if (rating == null) {
            showAlert("Please select a rating");
            return false;
        }
        if (comment == null || comment.trim().isEmpty()) {
            showAlert("Please write a comment");
            return false;
        }
        return true;
    }

    private void createReview(int rating, String comment) {
        User currentUser = Session.getInstance().getCurrentUser();
        if (currentUser == null) {
            showAlert("You must be logged in to leave a review");
            return;
        }

        try {
            Review review = new Review(
                    UUID.randomUUID(),
                    currentUser,
                    isRestaurantReview ? null : targetUser,
                    isRestaurantReview ? restaurant : null,
                    rating,
                    comment,
                    LocalDateTime.now());
            reviewRepo.save(review);

            // Update restaurant's average rating
            if (isRestaurantReview) {
                customRepo.updateRestaurantRating(restaurant.getId());
            }
        } catch (Exception e) {
            showAlert("Failed to save review: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }
}
