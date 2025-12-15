package com.naujokaitis.maistas.mobile.activities;

import static com.naujokaitis.maistas.mobile.utils.Constants.HOME_URL;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.*;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.*;
import com.naujokaitis.maistas.mobile.R;
import com.naujokaitis.maistas.mobile.utils.RestOperations;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;

public class OrdersActivity extends AppCompatActivity {

    private ListView ordersList;
    private LinearLayout emptyContainer;
    private androidx.cardview.widget.CardView activeOrderCard;
    private TextView activeOrderAddress, activeOrderStatus;
    private String clientId;
    private List<JsonObject> orders = new ArrayList<>();
    private JsonObject activeOrderJson = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        clientId = getIntent().getStringExtra("userId");
        
        ordersList = findViewById(R.id.ordersList);
        emptyContainer = findViewById(R.id.emptyContainer);
        activeOrderCard = findViewById(R.id.activeOrderCard);
        activeOrderAddress = findViewById(R.id.activeOrderAddress);
        activeOrderStatus = findViewById(R.id.activeOrderStatus);
        
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.refreshButton).setOnClickListener(v -> loadOrders());
        ordersList.setOnItemClickListener((p, v, pos, id) -> {
            if (pos < orders.size()) showDetails(orders.get(pos));
        });
        
        loadOrders();
    }

    private void loadOrders() {
        if (clientId == null || clientId.isEmpty()) {
            showEmpty();
            return;
        }
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String response = RestOperations.sendGet(HOME_URL + "orders/client/" + clientId);
                new Handler(Looper.getMainLooper()).post(() -> parseOrders(response));
            } catch (IOException e) {
                new Handler(Looper.getMainLooper()).post(this::showEmpty);
            }
        });
    }

    private void parseOrders(String response) {
        orders.clear();
        activeOrderJson = null;
        try {
            List<String> items = new ArrayList<>();
            JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
            List<String> activeStatuses = Arrays.asList("PENDING", "PREPARING", "READY", "PICKED_UP");

            for (JsonElement el : jsonArray) {
                JsonObject order = el.getAsJsonObject();
                String status = order.get("currentStatus").getAsString();
                
                // Find first active order
                if (activeOrderJson == null && activeStatuses.contains(status)) {
                    activeOrderJson = order;
                    continue; // Do not add to main list
                }

                orders.add(order);
                String addr = order.get("deliveryAddress").getAsString();
                String restaurant = "";
                if (order.has("restaurant") && order.get("restaurant").isJsonObject()) {
                    restaurant = order.getAsJsonObject("restaurant").get("name").getAsString();
                }
                items.add(getStatusIcon(status) + " " + restaurant + "\n" + addr + "\n" + getStatusText(status));
            }

            // Update Active Order Card
            if (activeOrderJson != null) {
                activeOrderCard.setVisibility(View.VISIBLE);
                activeOrderAddress.setText("Address: " + activeOrderJson.get("deliveryAddress").getAsString());
                activeOrderStatus.setText("Status: " + getStatusText(activeOrderJson.get("currentStatus").getAsString()));
                activeOrderCard.setOnClickListener(v -> showDetails(activeOrderJson));
            } else {
                activeOrderCard.setVisibility(View.GONE);
            }

            // Update Orders List
            if (orders.isEmpty() && activeOrderJson == null) {
                showEmpty();
            } else {
                emptyContainer.setVisibility(View.GONE);
                ordersList.setVisibility(View.VISIBLE);
                ordersList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showEmpty();
        }
    }

    private String getStatusIcon(String s) {
        switch (s) {
            case "PENDING": return "⏳";
            case "PREPARING": return "👨‍🍳";
            case "READY": return "📦";
            case "PICKED_UP": return "🚗";
            case "DELIVERED": return "✅";
            default: return "📋";
        }
    }

    private String getStatusText(String s) {
        switch (s) {
            case "PENDING": return "Pending";
            case "PREPARING": return "Preparing";
            case "READY": return "Ready for pickup";
            case "PICKED_UP": return "In transit";
            case "DELIVERED": return "Delivered";
            default: return s;
        }
    }

    private void showEmpty() {
        orders.clear();
        activeOrderJson = null;
        if (activeOrderCard != null) activeOrderCard.setVisibility(View.GONE);
        ordersList.setVisibility(View.GONE);
        emptyContainer.setVisibility(View.VISIBLE);
    }

    private void showDetails(JsonObject order) {
        String msg = "Address: " + order.get("deliveryAddress").getAsString() +
                "\nStatus: " + getStatusText(order.get("currentStatus").getAsString()) +
                "\nPayment: " + order.get("paymentType").getAsString();
        
        String orderId = order.get("id").getAsString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
            .setTitle("Order Details")
            .setMessage(msg)
            .setPositiveButton("OK", null)
            .setNeutralButton("Chat", (d, w) -> {
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("orderId", orderId);
                intent.putExtra("userId", clientId);
                startActivity(intent);
            });

        // Add Review button if delivered
        String status = order.get("currentStatus").getAsString();
        if ("DELIVERED".equals(status)) {
            builder.setNegativeButton("Review", (d, w) -> {
                if (order.has("restaurant")) {
                    String restId = order.getAsJsonObject("restaurant").get("id").getAsString();
                    Intent intent = new Intent(this, ReviewActivity.class);
                    intent.putExtra("userId", clientId);
                    intent.putExtra("restaurantId", restId);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Cannot review: Restaurant not found", Toast.LENGTH_SHORT).show();
                }
            });
        }

        builder.show();
    }
}
