package com.naujokaitis.maistas.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
public class OrderFilterCriteria {

    private OrderStatus status;
    private DateRange dateRange;
    private String restaurantName;
    private BigDecimal minTotal;
    private BigDecimal maxTotal;
    private String driverName;

    public boolean matches(Order order) {
        Objects.requireNonNull(order, "order must not be null");

        if (status != null && order.getCurrentStatus() != status) {
            return false;
        }
        if (dateRange != null && !dateRange.contains(order.getPlacedAt())) {
            return false;
        }
        if (restaurantName != null) {
            if (order.getRestaurant() == null ||
                    !order.getRestaurant().getName().toLowerCase().contains(restaurantName.toLowerCase())) {
                return false;
            }
        }
        if (minTotal != null && order.getTotalPrice().compareTo(minTotal) < 0) {
            return false;
        }
        if (maxTotal != null && order.getTotalPrice().compareTo(maxTotal) > 0) {
            return false;
        }
        if (driverName != null) {
            if (order.getDriver() == null ||
                    !order.getDriver().getUsername().toLowerCase().contains(driverName.toLowerCase())) {
                return false;
            }
        }
        return true;
    }
}

