package com.naujokaitis.maistas.mobile.activities;

import static com.naujokaitis.maistas.mobile.utils.Constants.HOME_URL;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.gson.*;
import com.naujokaitis.maistas.mobile.R;
import com.naujokaitis.maistas.mobile.utils.RestOperations;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;

public class DriverActivity extends AppCompatActivity {

    private TextView driverName, driverStatus, emptyMessage, currentOrderAddress, currentOrderStatus;
    private ListView availableOrdersList;
    private CardView currentOrderCard;
    private String driverId, driverUsername;
    private List<JsonObject> orders = new ArrayList<>();
    private JsonObject currentOrder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        String userJson = getIntent().getStringExtra("userJsonObject");
        try {
            JsonObject user = new Gson().fromJson(userJson, JsonObject.class);
            driverId = user.get("id").getAsString();
            driverUsername = user.get("username").getAsString();
        } catch (Exception e) {
            driverId = "";
            driverUsername = "Driver";
        }

        initViews();
        setupListeners();
        loadOrders();
    }

    private void initViews() {
        driverName = findViewById(R.id.driverName);
        driverStatus = findViewById(R.id.driverStatus);
        emptyMessage = findViewById(R.id.emptyMessage);
        availableOrdersList = findViewById(R.id.availableOrdersList);
        currentOrderCard = findViewById(R.id.currentOrderCard);
        currentOrderAddress = findViewById(R.id.currentOrderAddress);
        currentOrderStatus = findViewById(R.id.currentOrderStatus);
        driverName.setText("Welcome, " + driverUsername + "!");
    }

    private void setupListeners() {
        findViewById(R.id.refreshButton).setOnClickListener(v -> loadOrders());
        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        });

        // Chat Button
        findViewById(R.id.chatButton).setOnClickListener(v -> {
            if (currentOrder != null) {
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("orderId", currentOrder.get("id").getAsString());
                intent.putExtra("userId", driverId);
                startActivity(intent);
            }
        });

        // Delivered Button with Review
        findViewById(R.id.deliveredButton).setOnClickListener(v -> {
            if (currentOrder != null) showReviewDialog(currentOrder);
        });

        availableOrdersList.setOnItemClickListener((p, v, pos, id) -> {
            if (pos < orders.size()) showPickupDialog(orders.get(pos));
        });
    }

    private void loadOrders() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String myOrders = RestOperations.sendGet(HOME_URL + "orders/driver/" + driverId);
                String available = RestOperations.sendGet(HOME_URL + "orders/available");
                new Handler(Looper.getMainLooper()).post(() -> {
                    parseCurrentOrder(myOrders);
                    parseAvailableOrders(available);
                });
            } catch (IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> 
                    Toast.makeText(this, "Error loading orders", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void parseCurrentOrder(String response) {
        currentOrder = null;
        try {
            for (JsonElement el : new Gson().fromJson(response, JsonArray.class)) {
                JsonObject order = el.getAsJsonObject();
                if ("PICKED_UP".equals(order.get("currentStatus").getAsString())) {
                    currentOrder = order;
                    break;
                }
            }
        } catch (Exception ignored) {}

        if (currentOrder != null) {
            currentOrderCard.setVisibility(View.VISIBLE);
            currentOrderAddress.setText("Address: " + currentOrder.get("deliveryAddress").getAsString());
            currentOrderStatus.setText("Status: In Transit");
            driverStatus.setText("Status: Delivering");
            driverStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            currentOrderCard.setVisibility(View.GONE);
            driverStatus.setText("Status: Available");
            driverStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    private void parseAvailableOrders(String response) {
        orders.clear();
        try {
            List<String> items = new ArrayList<>();
            for (JsonElement el : new Gson().fromJson(response, JsonArray.class)) {
                JsonObject order = el.getAsJsonObject();
                orders.add(order);
                String addr = order.get("deliveryAddress").getAsString();
                String id = order.get("id").getAsString().substring(0, 8);
                items.add("Order #" + id + "\n" + addr);
            }
            if (orders.isEmpty()) {
                availableOrdersList.setVisibility(View.GONE);
                emptyMessage.setVisibility(View.VISIBLE);
            } else {
                emptyMessage.setVisibility(View.GONE);
                availableOrdersList.setVisibility(View.VISIBLE);
                availableOrdersList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items));
            }
        } catch (Exception e) {
            availableOrdersList.setVisibility(View.GONE);
            emptyMessage.setVisibility(View.VISIBLE);
        }
    }

    private void showPickupDialog(JsonObject order) {
        if (currentOrder != null) {
            Toast.makeText(this, "Deliver current order first", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
            .setTitle("Pick Up Order?")
            .setMessage("Address: " + order.get("deliveryAddress").getAsString())
            .setPositiveButton("Pick Up", (d, w) -> pickupOrder(order.get("id").getAsString()))
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void pickupOrder(String orderId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                RestOperations.sendPut(HOME_URL + "order/" + orderId + "/pickup/" + driverId, "");
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(this, "Order picked up!", Toast.LENGTH_SHORT).show();
                    loadOrders();
                });
            } catch (IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> 
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void markDelivered(String orderId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                RestOperations.sendPut(HOME_URL + "order/" + orderId + "/status?status=DELIVERED", "");
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(this, "Order delivered!", Toast.LENGTH_SHORT).show();
                    loadOrders();
                });
            } catch (IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> 
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void showReviewDialog(JsonObject order) {
        // Create Dialog Layout Programmatically
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        TextView title = new TextView(this);
        title.setText("Rate Client");
        title.setTextSize(18);
        title.setPadding(0, 0, 0, 20);
        layout.addView(title);

        RatingBar ratingBar = new RatingBar(this);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1);
        layout.addView(ratingBar);

        EditText commentInput = new EditText(this);
        commentInput.setHint("Comment (optional)");
        layout.addView(commentInput);

        new AlertDialog.Builder(this)
            .setTitle("Review Client")
            .setView(layout)
            .setPositiveButton("Submit & Finish", (d, w) -> {
                int rating = (int) ratingBar.getRating();
                String comment = commentInput.getText().toString();
                submitReviewAndFinish(order, rating, comment);
            })
            .setNegativeButton("Skip", (d, w) -> markDelivered(order.get("id").getAsString()))
            .setNeutralButton("Cancel", null)
            .show();
    }

    private void submitReviewAndFinish(JsonObject order, int rating, String comment) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Get Client ID
                String clientId = order.getAsJsonObject("client").get("id").getAsString();
                
                JsonObject review = new JsonObject();
                review.addProperty("authorId", driverId);
                review.addProperty("targetUserId", clientId);
                review.addProperty("rating", rating);
                review.addProperty("comment", comment);
                // targetRestaurantId is null

                RestOperations.sendPost(HOME_URL + "api/reviews/", review.toString());
                
                // Then mark delivered
                markDelivered(order.get("id").getAsString());

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> 
                    Toast.makeText(this, "Review failed, but verifying delivery...", Toast.LENGTH_SHORT).show());
                // Try marking delivered anyway
                 markDelivered(order.get("id").getAsString());
            }
        });
    }
}
