package com.naujokaitis.maistas.mobile.activities;

import static com.naujokaitis.maistas.mobile.utils.Constants.*;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.JsonObject;
import com.naujokaitis.maistas.mobile.R;
import com.naujokaitis.maistas.mobile.utils.RestOperations;
import java.io.IOException;
import java.util.concurrent.Executors;

public class ReviewActivity extends AppCompatActivity {

    private String userId;
    private String targetRestaurantId;
    private RatingBar ratingBar;
    private EditText commentInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        userId = getIntent().getStringExtra("userId");
        // For now focusing on Restaurant reviews from Order history
        targetRestaurantId = getIntent().getStringExtra("restaurantId");

        ratingBar = findViewById(R.id.ratingBar);
        commentInput = findViewById(R.id.commentInput);

        findViewById(R.id.submitReviewButton).setOnClickListener(v -> submitReview());
        findViewById(R.id.cancelButton).setOnClickListener(v -> finish());
    }

    private void submitReview() {
        float rating = ratingBar.getRating();
        String comment = commentInput.getText().toString().trim();

        if (rating < 1) {
            Toast.makeText(this, "Please select at least 1 star", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject body = new JsonObject();
        body.addProperty("authorId", userId);
        if (targetRestaurantId != null) {
            body.addProperty("targetRestaurantId", targetRestaurantId);
        }
        body.addProperty("rating", (int) rating);
        body.addProperty("comment", comment);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String response = RestOperations.sendPost(REVIEW_CREATE_URL, body.toString());
                new Handler(Looper.getMainLooper()).post(() -> {
                   if (response != null && !response.equals("Error")) {
                       Toast.makeText(this, "Review submitted!", Toast.LENGTH_SHORT).show();
                       finish();
                   } else {
                       Toast.makeText(this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                   }
                });
            } catch (IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> 
                    Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
