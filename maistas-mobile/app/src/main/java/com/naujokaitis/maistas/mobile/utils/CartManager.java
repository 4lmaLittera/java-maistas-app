package com.naujokaitis.maistas.mobile.utils;

import com.naujokaitis.maistas.mobile.model.CartItem;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<CartItem> items = new ArrayList<>();
    private String restaurantId;
    private String restaurantName;

    private CartManager() {}

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addItem(CartItem item) {
        // Check if item already in cart
        for (CartItem existing : items) {
            if (existing.getMenuItemId().equals(item.getMenuItemId())) {
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
                return;
            }
        }
        items.add(item);
    }

    public void removeItem(String menuItemId) {
        items.removeIf(item -> item.getMenuItemId().equals(menuItemId));
    }

    public void updateQuantity(String menuItemId, int quantity) {
        for (CartItem item : items) {
            if (item.getMenuItemId().equals(menuItemId)) {
                if (quantity <= 0) {
                    removeItem(menuItemId);
                } else {
                    item.setQuantity(quantity);
                }
                return;
            }
        }
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    public void setRestaurant(String id, String name) {
        if (restaurantId != null && !restaurantId.equals(id)) {
            items.clear();
        }
        this.restaurantId = id;
        this.restaurantName = name;
    }

    public String getRestaurantId() { return restaurantId; }
    public String getRestaurantName() { return restaurantName; }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    public int getItemCount() {
        int count = 0;
        for (CartItem item : items) {
            count += item.getQuantity();
        }
        return count;
    }

    public void clear() {
        items.clear();
        restaurantId = null;
        restaurantName = null;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
