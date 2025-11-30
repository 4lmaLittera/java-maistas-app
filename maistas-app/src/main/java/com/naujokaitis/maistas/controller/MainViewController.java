package com.naujokaitis.maistas.controller;

import com.naujokaitis.maistas.App;
import com.naujokaitis.maistas.database.GenericHibernate;
import com.naujokaitis.maistas.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class MainViewController {

    // TabPane and Tabs
    @FXML
    private TabPane mainTabPane;
    @FXML
    private Tab restaurantsTab;
    @FXML
    private Tab menuItemsTab;
    @FXML
    private Tab ordersTab;
    @FXML
    private Tab usersTab;

    // Restaurants Tab
    @FXML
    private TableView<Restaurant> restaurantsTable;
    @FXML
    private TableColumn<Restaurant, String> restaurantNameCol;
    @FXML
    private TableColumn<Restaurant, String> restaurantAddressCol;
    @FXML
    private TableColumn<Restaurant, Double> restaurantRatingCol;
    @FXML
    private TableColumn<Restaurant, String> restaurantOwnerCol;
    @FXML
    private Button addRestaurantBtn;
    @FXML
    private Button editRestaurantBtn;
    @FXML
    private Button deleteRestaurantBtn;

    // Menu Items Tab
    @FXML
    private TableView<com.naujokaitis.maistas.model.MenuItem> menuItemsTable;
    @FXML
    private TableColumn<com.naujokaitis.maistas.model.MenuItem, String> menuItemNameCol;
    @FXML
    private TableColumn<com.naujokaitis.maistas.model.MenuItem, MenuCategory> menuItemCategoryCol;
    @FXML
    private TableColumn<com.naujokaitis.maistas.model.MenuItem, BigDecimal> menuItemPriceCol;
    @FXML
    private TableColumn<com.naujokaitis.maistas.model.MenuItem, Integer> menuItemInventoryCol;
    @FXML
    private Button addMenuItemBtn;
    @FXML
    private Button editMenuItemBtn;
    @FXML
    private Button deleteMenuItemBtn;

    // Orders Tab
    @FXML
    private TableView<Order> ordersTable;
    @FXML
    private TableColumn<Order, String> orderIdCol;
    @FXML
    private TableColumn<Order, String> orderClientCol;
    @FXML
    private TableColumn<Order, String> orderRestaurantCol;
    @FXML
    private TableColumn<Order, OrderStatus> orderStatusCol;
    @FXML
    private TableColumn<Order, BigDecimal> orderTotalCol;
    @FXML
    private TableColumn<Order, LocalDateTime> orderDateCol;
    @FXML
    private ComboBox<OrderStatus> orderStatusFilter;

    // Users Tab
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, String> userUsernameCol;
    @FXML
    private TableColumn<User, String> userEmailCol;
    @FXML
    private TableColumn<User, UserRole> userRoleCol;
    @FXML
    private TableColumn<User, UserStatus> userStatusCol;
    @FXML
    private Button addUserBtn;
    @FXML
    private Button editUserBtn;
    @FXML
    private Button deleteUserBtn;

    // Status bar
    @FXML
    private Label statusLabel;
    @FXML
    private Label userInfoLabel;

    // Data storage
    private final ObservableList<Restaurant> restaurants = FXCollections.observableArrayList();
    private final ObservableList<com.naujokaitis.maistas.model.MenuItem> menuItems = FXCollections
            .observableArrayList();
    private final ObservableList<Order> orders = FXCollections.observableArrayList();
    private final ObservableList<User> users = FXCollections.observableArrayList();

    // Database access
    private final GenericHibernate<Restaurant> restaurantRepo = new GenericHibernate<>(Restaurant.class);
    private final GenericHibernate<com.naujokaitis.maistas.model.MenuItem> menuItemRepo = new GenericHibernate<>(
            com.naujokaitis.maistas.model.MenuItem.class);
    private final GenericHibernate<Order> orderRepo = new GenericHibernate<>(Order.class);
    private final GenericHibernate<User> userRepo = new GenericHibernate<>(User.class);

    @FXML
    private void initialize() {
        setupTables();
        configureRoleBasedAccess();
        loadAllData();
        setupTableSelectionListeners();
        updateUserInfo();
    }

    private void setupTables() {
        // Restaurants table
        restaurantNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        restaurantAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        restaurantRatingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));
        restaurantOwnerCol.setCellValueFactory(cellData -> {
            Restaurant restaurant = cellData.getValue();
            String ownerName = restaurant.getOwner() != null ? restaurant.getOwner().getUsername() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(ownerName);
        });
        restaurantsTable.setItems(restaurants);

        // Menu Items table
        menuItemNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        menuItemCategoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        menuItemPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        menuItemInventoryCol.setCellValueFactory(new PropertyValueFactory<>("inventoryCount"));
        menuItemsTable.setItems(menuItems);

        // Orders table
        orderIdCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getId().toString().substring(0, 8)));
        orderClientCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getClient().getUsername()));
        orderRestaurantCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getRestaurant().getName()));
        orderStatusCol.setCellValueFactory(new PropertyValueFactory<>("currentStatus"));
        orderTotalCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        orderDateCol.setCellValueFactory(new PropertyValueFactory<>("placedAt"));
        ordersTable.setItems(orders);

        // Users table
        userUsernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        userEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        userRoleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        userStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        usersTable.setItems(users);

        // Setup order status filter
        orderStatusFilter.setItems(FXCollections.observableArrayList(OrderStatus.values()));
    }

    private void configureRoleBasedAccess() {
        User currentUser = Session.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }

        UserRole role = currentUser.getRole();

        // Configure tab visibility based on role
        switch (role) {
            case ADMIN:
                // Admin sees everything
                break;
            case RESTAURANT_OWNER:
                // Owner sees restaurants, menu items, orders
                mainTabPane.getTabs().remove(usersTab);
                break;
            case CLIENT:
                // Client sees only restaurants and their orders
                mainTabPane.getTabs().removeAll(menuItemsTab, usersTab);
                addRestaurantBtn.setVisible(false);
                editRestaurantBtn.setVisible(false);
                deleteRestaurantBtn.setVisible(false);
                break;
            case DRIVER:
                // Driver sees only orders
                mainTabPane.getTabs().removeAll(restaurantsTab, menuItemsTab, usersTab);
                break;
        }
    }

    private void setupTableSelectionListeners() {
        restaurantsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean selected = newVal != null;
            editRestaurantBtn.setDisable(!selected);
            deleteRestaurantBtn.setDisable(!selected);
        });

        menuItemsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean selected = newVal != null;
            editMenuItemBtn.setDisable(!selected);
            deleteMenuItemBtn.setDisable(!selected);
        });

        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean selected = newVal != null;
            editUserBtn.setDisable(!selected);
            deleteUserBtn.setDisable(!selected);
        });
    }

    private void loadAllData() {
        handleRefreshRestaurants();
        handleRefreshMenuItems();
        handleRefreshOrders();
        handleRefreshUsers();
    }

    private void updateUserInfo() {
        User currentUser = Session.getInstance().getCurrentUser();
        if (currentUser != null) {
            userInfoLabel.setText("Logged in as: " + currentUser.getUsername() +
                    " (" + currentUser.getRole() + ")");
        }
    }

    // Menu actions
    @FXML
    private void handleLogout() {
        Session.getInstance().setCurrentUser(null);
        try {
            App.showInitialView();
        } catch (IOException e) {
            showError("Logout Error", "Failed to return to login screen", e.getMessage());
        }
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Maistas Delivery System");
        alert.setContentText("Version 1.0\nFood delivery management system\nBuilt with JavaFX and Hibernate");
        alert.showAndWait();
    }

    // Restaurant actions
    @FXML
    private void handleRefreshRestaurants() {
        try {
            List<Restaurant> data = restaurantRepo.findAll();
            restaurants.clear();
            restaurants.addAll(data);
            statusLabel.setText("Loaded " + data.size() + " restaurants");
        } catch (Exception e) {
            showError("Load Error", "Failed to load restaurants", e.getMessage());
        }
    }

    @FXML
    private void handleAddRestaurant() {
        RestaurantDialog.showAddDialog().ifPresent(restaurant -> {
            try {
                restaurantRepo.save(restaurant);
                restaurants.add(restaurant);
                statusLabel.setText("Restaurant added successfully");
            } catch (Exception e) {
                showError("Add Error", "Failed to add restaurant", e.getMessage());
            }
        });
    }

    @FXML
    private void handleEditRestaurant() {
        Restaurant selected = restaurantsTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        RestaurantDialog.showEditDialog(selected).ifPresent(restaurant -> {
            try {
                restaurantRepo.update(restaurant);
                restaurantsTable.refresh();
                statusLabel.setText("Restaurant updated successfully");
            } catch (Exception e) {
                showError("Update Error", "Failed to update restaurant", e.getMessage());
            }
        });
    }

    @FXML
    private void handleDeleteRestaurant() {
        Restaurant selected = restaurantsTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Restaurant: " + selected.getName());
        confirm.setContentText("Are you sure? This action cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    restaurantRepo.delete(selected);
                    restaurants.remove(selected);
                    statusLabel.setText("Restaurant deleted successfully");
                } catch (Exception e) {
                    showError("Delete Error", "Failed to delete restaurant", e.getMessage());
                }
            }
        });
    }

    // Menu Item actions
    @FXML
    private void handleRefreshMenuItems() {
        try {
            List<com.naujokaitis.maistas.model.MenuItem> data = menuItemRepo.findAll();
            menuItems.clear();
            menuItems.addAll(data);
            statusLabel.setText("Loaded " + data.size() + " menu items");
        } catch (Exception e) {
            showError("Load Error", "Failed to load menu items", e.getMessage());
        }
    }

    @FXML
    private void handleAddMenuItem() {
        MenuItemDialog.showAddDialog().ifPresent(menuItem -> {
            try {
                menuItemRepo.save(menuItem);
                menuItems.add(menuItem);
                statusLabel.setText("Menu item added successfully");
            } catch (Exception e) {
                showError("Add Error", "Failed to add menu item", e.getMessage());
            }
        });
    }

    @FXML
    private void handleEditMenuItem() {
        com.naujokaitis.maistas.model.MenuItem selected = menuItemsTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        MenuItemDialog.showEditDialog(selected).ifPresent(menuItem -> {
            try {
                menuItemRepo.update(menuItem);
                menuItemsTable.refresh();
                statusLabel.setText("Menu item updated successfully");
            } catch (Exception e) {
                showError("Update Error", "Failed to update menu item", e.getMessage());
            }
        });
    }

    @FXML
    private void handleDeleteMenuItem() {
        com.naujokaitis.maistas.model.MenuItem selected = menuItemsTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Menu Item: " + selected.getName());
        confirm.setContentText("Are you sure?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    menuItemRepo.delete(selected);
                    menuItems.remove(selected);
                    statusLabel.setText("Menu item deleted successfully");
                } catch (Exception e) {
                    showError("Delete Error", "Failed to delete menu item", e.getMessage());
                }
            }
        });
    }

    // Order actions
    @FXML
    private void handleRefreshOrders() {
        try {
            List<Order> data = orderRepo.findAll();
            orders.clear();
            orders.addAll(data);
            statusLabel.setText("Loaded " + data.size() + " orders");
        } catch (Exception e) {
            showError("Load Error", "Failed to load orders", e.getMessage());
        }
    }

    @FXML
    private void handleFilterOrders() {
        OrderStatus filterStatus = orderStatusFilter.getValue();
        if (filterStatus == null) {
            handleRefreshOrders();
            return;
        }

        try {
            List<Order> allOrders = orderRepo.findAll();
            List<Order> filtered = allOrders.stream()
                    .filter(o -> o.getCurrentStatus() == filterStatus)
                    .toList();
            orders.clear();
            orders.addAll(filtered);
            statusLabel.setText("Filtered: " + filtered.size() + " orders with status " + filterStatus);
        } catch (Exception e) {
            showError("Filter Error", "Failed to filter orders", e.getMessage());
        }
    }

    // User actions
    @FXML
    private void handleRefreshUsers() {
        try {
            List<User> data = userRepo.findAll();
            users.clear();
            users.addAll(data);
            statusLabel.setText("Loaded " + data.size() + " users");
        } catch (Exception e) {
            showError("Load Error", "Failed to load users", e.getMessage());
        }
    }

    @FXML
    private void handleAddUser() {
        showInfo("Coming Soon", "Add User feature will be implemented next");
    }

    @FXML
    private void handleEditUser() {
        showInfo("Coming Soon", "Edit User feature will be implemented next");
    }

    @FXML
    private void handleDeleteUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete User: " + selected.getUsername());
        confirm.setContentText("Are you sure?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    userRepo.delete(selected);
                    users.remove(selected);
                    statusLabel.setText("User deleted successfully");
                } catch (Exception e) {
                    showError("Delete Error", "Failed to delete user", e.getMessage());
                }
            }
        });
    }

    // Utility methods
    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
