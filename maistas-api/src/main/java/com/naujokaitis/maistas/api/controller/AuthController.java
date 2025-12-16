package com.naujokaitis.maistas.api.controller;

import com.naujokaitis.maistas.api.model.Client;
import com.naujokaitis.maistas.api.model.Driver;
import com.naujokaitis.maistas.api.model.User;
import com.naujokaitis.maistas.api.repository.ClientRepository;
import com.naujokaitis.maistas.api.repository.DriverRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private DriverRepository driverRepository;

    @PostMapping("/validateUser")
    public @ResponseBody User validateUser(@RequestBody String info) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(info, JsonObject.class);
        String login = jsonObject.has("login") ? jsonObject.get("login").getAsString() : null;
        String password = jsonObject.has("password") ? jsonObject.get("password").getAsString() : null;

        if (login == null || password == null) {
            return null;
        }

        // Search in Clients first
        for (Client client : clientRepository.findAll()) {
            if (client.getUsername().equals(login)) {
                if (client.authenticate(password)) {
                    return client;
                }
            }
        }

        // Search in Drivers
        for (Driver driver : driverRepository.findAll()) {
            if (driver.getUsername().equals(login)) {
                if (driver.authenticate(password)) {
                    return driver;
                }
            }
        }

        return null;
    }
}
