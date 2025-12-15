package com.naujokaitis.maistas.mobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.naujokaitis.maistas.mobile.R;
import com.naujokaitis.maistas.mobile.model.*;
import com.naujokaitis.maistas.mobile.utils.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executors;

public class MenuActivity extends AppCompatActivity {

    private ListView menuItemsList;
    private TextView restaurantTitle, cartBadge;
    private String restaurantId, restaurantName, userId;
    private List<MenuItem> menuItems = new ArrayList<>();
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        cartManager = CartManager.getInstance();
        restaurantId = getIntent().getStringExtra("restaurantId");
        restaurantName = getIntent().getStringExtra("restaurantName");
        userId = getIntent().getStringExtra("userId");

        menuItemsList = findViewById(R.id.menuItemsList);
        restaurantTitle = findViewById(R.id.restaurantTitle);
        cartBadge = findViewById(R.id.cartBadge);

        restaurantTitle.setText(restaurantName);
        cartManager.setRestaurant(restaurantId, restaurantName);

        loadMenu();
        setupListeners();
        updateCartBadge();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }

    private void setupListeners() {
        findViewById(R.id.cartFab).setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        menuItemsList.setOnItemClickListener((parent, view, position, id) -> {
            if (position < menuItems.size()) {
                MenuItem item = menuItems.get(position);
                new AlertDialog.Builder(this)
                    .setTitle("Add to Cart")
                    .setMessage(item.getName() + "\n\n" + item.getDescription() + "\n\nPrice: €" + item.getPrice())
                    .setPositiveButton("Add", (d, w) -> {
                        cartManager.addItem(new CartItem(item.getId().toString(), item.getName(), 
                            new BigDecimal(item.getPrice().toString()), 1));
                        updateCartBadge();
                        Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            }
        });
    }

    private void updateCartBadge() {
        int count = cartManager.getItemCount();
        cartBadge.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        cartBadge.setText(String.valueOf(count));
    }

    private void loadMenu() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String response = RestOperations.sendGet(Constants.RESTAURANT_URL + restaurantId + "/menu");
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response != null && !response.equals("Error") && !response.isEmpty()) {
                        Menu menu = new Gson().fromJson(response, Menu.class);
                        if (menu != null && menu.getItems() != null) {
                            menuItems = menu.getItems();
                            List<String> names = new ArrayList<>();
                            for (MenuItem item : menuItems) {
                                names.add(item.getName() + "\n" + item.getDescription() + "\n€" + item.getPrice());
                            }
                            menuItemsList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names));
                        }
                    } else {
                        Toast.makeText(this, "No menu available", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> 
                    Toast.makeText(this, "Error loading menu", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
