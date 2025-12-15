package com.naujokaitis.maistas.mobile.activities;

import static com.naujokaitis.maistas.mobile.utils.Constants.HOME_URL;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.naujokaitis.maistas.mobile.R;
import com.naujokaitis.maistas.mobile.model.CartItem;
import com.naujokaitis.maistas.mobile.utils.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;

public class CartActivity extends AppCompatActivity {

    private ListView cartItemsList;
    private TextView emptyCartMessage, totalPrice, restaurantName;
    private LinearLayout orderSummary;
    private EditText deliveryAddress;
    private Spinner paymentTypeSpinner;
    private CartManager cartManager;
    private String userId;
    
    private final String[] paymentTypes = {"CASH", "CARD", "ONLINE"};
    private final String[] paymentLabels = {"Cash", "Card", "Online"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        userId = getIntent().getStringExtra("userId");
        cartManager = CartManager.getInstance();
        initViews();
        setupListeners();
        updateUI();
    }

    private void initViews() {
        cartItemsList = findViewById(R.id.cartItemsList);
        emptyCartMessage = findViewById(R.id.emptyCartMessage);
        totalPrice = findViewById(R.id.totalPrice);
        restaurantName = findViewById(R.id.restaurantName);
        orderSummary = findViewById(R.id.orderSummary);
        deliveryAddress = findViewById(R.id.deliveryAddress);
        paymentTypeSpinner = findViewById(R.id.paymentTypeSpinner);

        paymentTypeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentLabels));
    }

    private void setupListeners() {
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.placeOrderButton).setOnClickListener(v -> placeOrder());
    }

    private void updateUI() {
        if (cartManager.isEmpty()) {
            cartItemsList.setVisibility(View.GONE);
            orderSummary.setVisibility(View.GONE);
            emptyCartMessage.setVisibility(View.VISIBLE);
        } else {
            cartItemsList.setVisibility(View.VISIBLE);
            orderSummary.setVisibility(View.VISIBLE);
            emptyCartMessage.setVisibility(View.GONE);
            restaurantName.setText(cartManager.getRestaurantName());
            totalPrice.setText("€" + cartManager.getTotal());

            List<String> items = new ArrayList<>();
            for (CartItem item : cartManager.getItems()) {
                items.add(item.getQuantity() + "x " + item.getName() + " - €" + item.getSubtotal());
            }
            cartItemsList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items));
        }
    }

    private void placeOrder() {
        String address = deliveryAddress.getText().toString().trim();
        if (address.isEmpty()) {
            Toast.makeText(this, "Enter delivery address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cartManager.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject json = new JsonObject();
        json.addProperty("clientId", userId);
        json.addProperty("restaurantId", cartManager.getRestaurantId());
        json.addProperty("deliveryAddress", address);
        json.addProperty("paymentType", paymentTypes[paymentTypeSpinner.getSelectedItemPosition()]);

        JsonArray itemsArray = new JsonArray();
        for (CartItem item : cartManager.getItems()) {
            JsonObject itemJson = new JsonObject();
            itemJson.addProperty("menuItemId", item.getMenuItemId());
            itemJson.addProperty("quantity", item.getQuantity());
            itemsArray.add(itemJson);
        }
        json.add("items", itemsArray);

        findViewById(R.id.placeOrderButton).setEnabled(false);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String response = RestOperations.sendPost(HOME_URL + "insertOrder", json.toString());
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response != null && !response.equals("Error") && !response.isEmpty()) {
                        Toast.makeText(this, "Order placed!", Toast.LENGTH_SHORT).show();
                        cartManager.clear();
                        finish();
                    } else {
                        Toast.makeText(this, "Order failed", Toast.LENGTH_SHORT).show();
                        findViewById(R.id.placeOrderButton).setEnabled(true);
                    }
                });
            } catch (IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.placeOrderButton).setEnabled(true);
                });
            }
        });
    }
}
