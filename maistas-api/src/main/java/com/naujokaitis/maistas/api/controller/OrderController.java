package com.naujokaitis.maistas.api.controller;

import com.naujokaitis.maistas.api.exception.ResourceNotFoundException;
import com.naujokaitis.maistas.api.model.*;
import com.naujokaitis.maistas.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PricingRuleRepository pricingRuleRepository;

    // READ - Get all orders
    @GetMapping("/allOrders")
    public @ResponseBody Iterable<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // READ - Get single order by ID with HATEOAS
    @GetMapping("/order/{id}")
    public EntityModel<Order> getOrderById(@PathVariable UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        return EntityModel.of(order,
                linkTo(methodOn(OrderController.class).getOrderById(id)).withSelfRel(),
                linkTo(methodOn(OrderController.class).getAllOrders()).withRel("orders"));
    }

    // READ - Get orders by client ID
    @GetMapping("/orders/client/{clientId}")
    public @ResponseBody List<Order> getOrdersByClient(@PathVariable UUID clientId) {
        return orderRepository.findByClientId(clientId);
    }

    // READ - Get orders by driver ID
    @GetMapping("/orders/driver/{driverId}")
    public @ResponseBody List<Order> getOrdersByDriver(@PathVariable UUID driverId) {
        return orderRepository.findByDriverId(driverId);
    }

    // READ - Get orders by restaurant ID
    @GetMapping("/orders/restaurant/{restaurantId}")
    public @ResponseBody List<Order> getOrdersByRestaurant(@PathVariable UUID restaurantId) {
        return orderRepository.findByRestaurantId(restaurantId);
    }

    // READ - Get orders by status
    @GetMapping("/orders/status/{status}")
    public @ResponseBody List<Order> getOrdersByStatus(@PathVariable OrderStatus status) {
        return orderRepository.findByCurrentStatus(status);
    }

    // READ - Get READY orders without driver (for drivers to pick up)
    @GetMapping("/orders/available")
    public @ResponseBody List<Order> getAvailableOrders() {
        return orderRepository.findByDriverIsNullAndCurrentStatus(OrderStatus.READY);
    }

    // CREATE - Create new order
    @PostMapping("/insertOrder")
    public @ResponseBody Order createOrder(@RequestBody OrderCreateRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", request.getClientId()));
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", request.getRestaurantId()));

        Order order = Order.create(client, restaurant, request.getDeliveryAddress(), request.getPaymentType());
        
        // Dynamic Pricing Logic
        java.time.LocalTime now = java.time.LocalTime.now();
        Iterable<PricingRule> rules = pricingRuleRepository.findAll();
        for (PricingRule rule : rules) {
             if (rule.getTimeRange() != null && rule.getTimeRange().contains(now)) {
                 order.applyPricingRule(rule);
                 break; // Apply first matching rule
             }
        }
        
        return orderRepository.save(order);
    }

    // UPDATE - Update order status
    @PutMapping("/order/{id}/status")
    public @ResponseBody Order updateOrderStatus(@PathVariable UUID id, @RequestParam OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        
        // Award loyalty points if completed
        if (status == OrderStatus.DELIVERED && order.getCurrentStatus() != OrderStatus.DELIVERED) {
            Client client = order.getClient();
            if (client != null) {
                // Award 10 points per order for now (can be dynamic)
                client.setLoyaltyPoints(client.getLoyaltyPoints() + 10);
                clientRepository.save(client);
            }
        }
        
        order.updateStatus(status);
        return orderRepository.save(order);
    }

    // UPDATE - Assign driver to order (pickup)
    @PutMapping("/order/{id}/pickup/{driverId}")
    public @ResponseBody Order pickupOrder(@PathVariable UUID id, @PathVariable UUID driverId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", driverId));

        order.assignDriver(driver);
        order.updateStatus(OrderStatus.PICKED_UP);
        return orderRepository.save(order);
    }

    // DELETE - Delete order by ID
    @DeleteMapping("/deleteOrder/{id}")
    public @ResponseBody String deleteOrder(@PathVariable UUID id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order", id);
        }
        orderRepository.deleteById(id);
        return "Order with id " + id + " deleted successfully";
    }

    // DTO for order creation
    public static class OrderCreateRequest {
        private UUID clientId;
        private UUID restaurantId;
        private String deliveryAddress;
        private PaymentType paymentType;

        public UUID getClientId() { return clientId; }
        public void setClientId(UUID clientId) { this.clientId = clientId; }
        public UUID getRestaurantId() { return restaurantId; }
        public void setRestaurantId(UUID restaurantId) { this.restaurantId = restaurantId; }
        public String getDeliveryAddress() { return deliveryAddress; }
        public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
        public PaymentType getPaymentType() { return paymentType; }
        public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }
    }
}
