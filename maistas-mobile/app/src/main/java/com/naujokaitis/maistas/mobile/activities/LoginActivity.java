package com.naujokaitis.maistas.mobile.activities;

import static com.naujokaitis.maistas.mobile.utils.Constants.VALIDATE_USER_URL;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.naujokaitis.maistas.mobile.R;
import com.naujokaitis.maistas.mobile.utils.RestOperations;

import java.io.IOException;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        errorText = findViewById(R.id.errorText);
    }

    public void validateUser(View view) {
        TextView loginField = findViewById(R.id.loginField);
        TextView passwordField = findViewById(R.id.passwordField);

        String login = loginField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (login.isEmpty() || password.isEmpty()) {
            showError("Enter username and password");
            return;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("login", login);
        jsonObject.addProperty("password", password);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String response = RestOperations.sendPost(VALIDATE_USER_URL, jsonObject.toString());
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response != null && !response.equals("Error") && !response.isEmpty() && !response.equals("null")) {
                        navigateByRole(response);
                    } else {
                        showError("Invalid credentials");
                    }
                });
            } catch (IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> showError("Connection error"));
            }
        });
    }

    private void navigateByRole(String userJson) {
        try {
            JsonObject userObj = new Gson().fromJson(userJson, JsonObject.class);
            String role = userObj.has("role") ? userObj.get("role").getAsString() : "";
            
            Intent intent;
            if ("DRIVER".equalsIgnoreCase(role)) {
                intent = new Intent(this, DriverActivity.class);
            } else {
                intent = new Intent(this, RestaurantsActivity.class);
            }
            intent.putExtra("userJsonObject", userJson);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Intent intent = new Intent(this, RestaurantsActivity.class);
            intent.putExtra("userJsonObject", userJson);
            startActivity(intent);
            finish();
        }
    }

    public void loadRegWindow(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
