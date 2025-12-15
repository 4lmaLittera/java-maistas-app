package com.naujokaitis.maistas.api.controller;

import com.naujokaitis.maistas.api.exception.ResourceNotFoundException;
import com.naujokaitis.maistas.api.model.Restaurant;
import com.naujokaitis.maistas.api.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class RestaurantController {

    @Autowired
    private RestaurantRepository restaurantRepository;

    // READ - Get all restaurants
    @GetMapping("/allRestaurants")
    public @ResponseBody Iterable<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    // READ - Get single restaurant by ID with HATEOAS
    @GetMapping("/restaurant/{id}")
    public EntityModel<Restaurant> getRestaurantById(@PathVariable UUID id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", id));

        return EntityModel.of(restaurant,
                linkTo(methodOn(RestaurantController.class).getRestaurantById(id)).withSelfRel(),
                linkTo(methodOn(RestaurantController.class).getAllRestaurants()).withRel("restaurants"));
    }

    // CREATE - Insert new restaurant
    @PostMapping("/insertRestaurant")
    public @ResponseBody Restaurant createRestaurant(@RequestBody Restaurant restaurant) {
        if (restaurant.getId() == null) {
            Restaurant newRestaurant = Restaurant.create(
                    restaurant.getName(),
                    restaurant.getAddress(),
                    restaurant.getDescription()
            );
            return restaurantRepository.save(newRestaurant);
        }
        return restaurantRepository.save(restaurant);
    }

    // UPDATE - Update existing restaurant
    @PutMapping("/updateRestaurant")
    public @ResponseBody Restaurant updateRestaurant(@RequestBody Restaurant restaurant) {
        if (restaurant.getId() == null) {
            throw new IllegalArgumentException("Restaurant ID is required for update");
        }
        restaurantRepository.findById(restaurant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", restaurant.getId()));
        return restaurantRepository.save(restaurant);
    }

    // DELETE - Delete restaurant by ID
    @DeleteMapping("/deleteRestaurant/{id}")
    public @ResponseBody String deleteRestaurant(@PathVariable UUID id) {
        if (!restaurantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Restaurant", id);
        }
        restaurantRepository.deleteById(id);
        return "Restaurant with id " + id + " deleted successfully";
    }
}
