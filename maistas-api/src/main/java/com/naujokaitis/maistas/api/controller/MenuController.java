package com.naujokaitis.maistas.api.controller;

import com.naujokaitis.maistas.api.exception.ResourceNotFoundException;
import com.naujokaitis.maistas.api.model.Menu;
import com.naujokaitis.maistas.api.model.MenuItem;
import com.naujokaitis.maistas.api.model.Restaurant;
import com.naujokaitis.maistas.api.repository.MenuItemRepository;
import com.naujokaitis.maistas.api.repository.MenuRepository;
import com.naujokaitis.maistas.api.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class MenuController {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    // READ - Get menu by restaurant ID
    @GetMapping("/restaurant/{restaurantId}/menu")
    public @ResponseBody Menu getMenuByRestaurant(@PathVariable UUID restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", restaurantId));
        
        if (restaurant.getMenu() == null) {
            throw new ResourceNotFoundException("Menu for restaurant", restaurantId);
        }
        return restaurant.getMenu();
    }

    // READ - Get all menu items for a menu
    @GetMapping("/menu/{menuId}/items")
    public @ResponseBody List<MenuItem> getMenuItems(@PathVariable UUID menuId) {
        return menuItemRepository.findByMenuId(menuId);
    }

    // READ - Get single menu item by ID
    @GetMapping("/menuItem/{id}")
    public @ResponseBody MenuItem getMenuItem(@PathVariable UUID id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", id));
    }

    // CREATE - Add menu item to menu
    @PostMapping("/menu/{menuId}/addItem")
    public @ResponseBody MenuItem addMenuItem(@PathVariable UUID menuId, @RequestBody MenuItem menuItem) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu", menuId));

        MenuItem newItem = MenuItem.create(
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.getCategory(),
                menuItem.getInventoryCount()
        );
        newItem.setMenu(menu);
        return menuItemRepository.save(newItem);
    }

    // DELETE - Remove menu item
    @DeleteMapping("/menuItem/{id}")
    public @ResponseBody String deleteMenuItem(@PathVariable UUID id) {
        if (!menuItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("MenuItem", id);
        }
        menuItemRepository.deleteById(id);
        return "MenuItem with id " + id + " deleted successfully";
    }
}
