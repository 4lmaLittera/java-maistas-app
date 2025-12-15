package com.naujokaitis.maistas.api.controller;

import com.naujokaitis.maistas.api.exception.ResourceNotFoundException;
import com.naujokaitis.maistas.api.model.Driver;
import com.naujokaitis.maistas.api.model.UserRole;
import com.naujokaitis.maistas.api.model.UserStatus;
import com.naujokaitis.maistas.api.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class DriverController {

    @Autowired
    private DriverRepository driverRepository;

    // READ - Get all drivers
    @GetMapping("/allDrivers")
    public @ResponseBody Iterable<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    // READ - Get single driver by ID with HATEOAS links
    @GetMapping("/driver/{id}")
    public EntityModel<Driver> getDriverById(@PathVariable UUID id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", id));

        return EntityModel.of(driver,
                linkTo(methodOn(DriverController.class).getDriverById(id)).withSelfRel(),
                linkTo(methodOn(DriverController.class).getAllDrivers()).withRel("drivers"));
    }

    // CREATE - Insert new driver
    @PostMapping("/insertDriver")
    public @ResponseBody Driver createDriver(@RequestBody Driver driver) {
        // Generate UUID if not provided
        if (driver.getId() == null) {
            Driver newDriver = Driver.create(
                    driver.getUsername(),
                    driver.getPasswordHash(),
                    driver.getEmail(),
                    driver.getPhone(),
                    driver.getVehicleType(),
                    driver.isAvailable()
            );
            return driverRepository.save(newDriver);
        }
        // Set default values if not provided
        if (driver.getStatus() == null) {
            driver.setStatus(UserStatus.ACTIVE);
        }
        if (driver.getRole() == null) {
            driver.setRole(UserRole.DRIVER);
        }
        return driverRepository.save(driver);
    }

    // UPDATE - Update existing driver
    @PutMapping("/updateDriver")
    public @ResponseBody Driver updateDriver(@RequestBody Driver driver) {
        // Verify the driver exists
        if (driver.getId() == null) {
            throw new IllegalArgumentException("Driver ID is required for update");
        }
        driverRepository.findById(driver.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver", driver.getId()));

        return driverRepository.save(driver);
    }

    // UPDATE - Update driver by ID with partial data
    @PutMapping("/updateDriver/{id}")
    public @ResponseBody Driver updateDriverById(@PathVariable UUID id, @RequestBody Driver driverData) {
        Driver existingDriver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", id));

        // Update only non-null fields
        if (driverData.getUsername() != null) {
            existingDriver.setUsername(driverData.getUsername());
        }
        if (driverData.getEmail() != null) {
            existingDriver.setEmail(driverData.getEmail());
        }
        if (driverData.getPhone() != null) {
            existingDriver.setPhone(driverData.getPhone());
        }
        if (driverData.getVehicleType() != null) {
            existingDriver.setVehicleType(driverData.getVehicleType());
        }
        if (driverData.getStatus() != null) {
            existingDriver.setStatus(driverData.getStatus());
        }
        // Available is a primitive, so we always update it
        existingDriver.setAvailable(driverData.isAvailable());

        return driverRepository.save(existingDriver);
    }

    // DELETE - Delete driver by ID
    @DeleteMapping("/deleteDriver/{id}")
    public @ResponseBody String deleteDriver(@PathVariable UUID id) {
        if (!driverRepository.existsById(id)) {
            throw new ResourceNotFoundException("Driver", id);
        }
        driverRepository.deleteById(id);
        return "Driver with id " + id + " deleted successfully";
    }
}
