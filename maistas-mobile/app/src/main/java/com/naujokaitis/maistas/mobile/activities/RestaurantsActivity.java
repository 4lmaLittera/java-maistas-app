package com.naujokaitis.maistas.mobile.activities;

import static com.naujokaitis.maistas.mobile.utils.Constants.ALL_RESTAURANTS_URL;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.naujokaitis.maistas.mobile.R;
import com.naujokaitis.maistas.mobile.model.Restaurant;
import com.naujokaitis.maistas.mobile.utils.RestOperations;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RestaurantsActivity extends AppCompatActivity {

    private ListView restaurantsList;
    private MaterialButton ordersButton, logoutButton;
    private List<Restaurant> restaurants = new ArrayList<>();
    private String userJson;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_restaurants);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userJson = getIntent().getStringExtra("userJsonObject");
        
        // Extract userId from user JSON
        try {
            Gson gson = new Gson();
            JsonObject userObj = gson.fromJson(userJson, JsonObject.class);
            userId = userObj.get("id").getAsString();
        } catch (Exception e) {
            userId = "";
        }
        
        initViews();
        setupListeners();
        loadRestaurants();
    }

    private TextView loyaltyPointsText;

    private void initViews() {
        restaurantsList = findViewById(R.id.restaurantsList);
        ordersButton = findViewById(R.id.ordersButton);
        logoutButton = findViewById(R.id.logoutButton);
        loyaltyPointsText = findViewById(R.id.loyaltyPointsText);
        
        // Parse loyalty points
        try {
            Gson gson = new Gson();
            JsonObject userObj = gson.fromJson(userJson, JsonObject.class);
            if (userObj.has("loyaltyPoints")) {
                int points = userObj.get("loyaltyPoints").getAsInt();
                loyaltyPointsText.setText("Points: " + points);
            }
        } catch (Exception e) {
            // Ignore
        }
    }

    private void setupListeners() {
        // Open Orders Activity
        ordersButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrdersActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        // Logout
        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Restaurant click -> Menu
        restaurantsList.setOnItemClickListener((parent, view, position, id) -> {
            Restaurant selected = restaurants.get(position);
            Intent intent = new Intent(RestaurantsActivity.this, MenuActivity.class);
            intent.putExtra("restaurantId", selected.getId().toString());
            intent.putExtra("restaurantName", selected.getName());
            intent.putExtra("userId", userId);
            intent.putExtra("userJsonObject", userJson);
            startActivity(intent);
        });
    }

    private void loadRestaurants() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendGet(ALL_RESTAURANTS_URL);
                handler.post(() -> {
                    if (response != null && !response.equals("Error") && !response.isEmpty()) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Restaurant>>(){}.getType();
                        restaurants = gson.fromJson(response, listType);
                        
                        List<String> restaurantNames = new ArrayList<>();
                        for (Restaurant r : restaurants) {
                            restaurantNames.add(r.getName() + "\n📍 " + r.getAddress());
                        }
                        
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            RestaurantsActivity.this,
                            android.R.layout.simple_list_item_1,
                            restaurantNames
                        );
                        restaurantsList.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "Nepavyko gauti restoranų", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                handler.post(() -> {
                    Toast.makeText(this, "Klaida jungiantis prie serverio", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
