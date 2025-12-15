package com.naujokaitis.maistas.mobile.activities;

import static com.naujokaitis.maistas.mobile.utils.Constants.HOME_URL;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.naujokaitis.maistas.mobile.R;
import com.naujokaitis.maistas.mobile.utils.RestOperations;
import java.io.IOException;
import java.util.concurrent.*;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameField, emailField, phoneField, addressField, passwordField, confirmPasswordField;
    private RadioGroup userTypeGroup;
    private TextInputLayout addressLayout;
    private LinearLayout vehicleTypeLayout;
    private Spinner vehicleTypeSpinner;

    private final String[] vehicleTypes = {"CAR", "MOTORCYCLE", "BICYCLE", "SCOOTER"};
    private final String[] vehicleLabels = {"Car", "Motorcycle", "Bicycle", "Scooter"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
        setupListeners();
    }

    private void initViews() {
        usernameField = findViewById(R.id.usernameField);
        emailField = findViewById(R.id.emailField);
        phoneField = findViewById(R.id.phoneField);
        addressField = findViewById(R.id.addressField);
        passwordField = findViewById(R.id.passwordField);
        confirmPasswordField = findViewById(R.id.confirmPasswordField);
        userTypeGroup = findViewById(R.id.userTypeGroup);
        addressLayout = findViewById(R.id.addressLayout);
        vehicleTypeLayout = findViewById(R.id.vehicleTypeLayout);
        vehicleTypeSpinner = findViewById(R.id.vehicleTypeSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vehicleLabels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleTypeSpinner.setAdapter(adapter);
    }

    private void setupListeners() {
        userTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioClient) {
                addressLayout.setVisibility(View.VISIBLE);
                vehicleTypeLayout.setVisibility(View.GONE);
            } else {
                addressLayout.setVisibility(View.GONE);
                vehicleTypeLayout.setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.registerButton).setOnClickListener(v -> register());
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void register() {
        String username = usernameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String phone = phoneField.getText().toString().trim();
        String address = addressField.getText().toString().trim();
        String password = passwordField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();

        if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isClient = userTypeGroup.getCheckedRadioButtonId() == R.id.radioClient;
        if (isClient && address.isEmpty()) {
            Toast.makeText(this, "Address required", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject json = new JsonObject();
        json.addProperty("username", username);
        json.addProperty("email", email);
        json.addProperty("phone", phone);
        json.addProperty("password", password);

        String endpoint;
        if (isClient) {
            json.addProperty("defaultAddress", address);
            json.addProperty("loyaltyPoints", 0);
            endpoint = HOME_URL + "insertClient";
        } else {
            json.addProperty("vehicleType", vehicleTypes[vehicleTypeSpinner.getSelectedItemPosition()]);
            json.addProperty("available", true);
            endpoint = HOME_URL + "insertDriver";
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String response = RestOperations.sendPost(endpoint, json.toString());
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response != null && !response.equals("Error") && !response.isEmpty()) {
                        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> 
                    Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
