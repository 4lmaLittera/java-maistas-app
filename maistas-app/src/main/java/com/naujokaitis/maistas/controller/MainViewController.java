package com.naujokaitis.maistas.controller;

import com.naujokaitis.maistas.App;
import com.naujokaitis.maistas.database.GenericHibernate;
import com.naujokaitis.maistas.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
    private Tab menusTab;
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
    private TextField restaurantSearchField;
    @FXML
    private ComboBox<Integer> restaurantRatingFilter;
    @FXML
    private Button addRestaurantBtn;
    @FXML
    private Button editRestaurantBtn;
    @FXML
    private Button deleteRestaurantBtn;
    @FXML
    private Button reviewsBtn;

    // Menus Tab
    @FXML
    private TableView<com.naujokaitis.maistas.model.Menu> menusTable;
    @FXML
    private TableColumn<com.naujokaitis.maistas.model.Menu, String> menuIdCol;
    @FXML
    private TableColumn<com.naujokaitis.maistas.model.Menu, String> menuNameCol;
    @FXML
    private TableColumn<com.naujokaitis.maistas.model.Menu, Integer> menuItemCountCol;
    @FXML
    private ComboBox<Restaurant> menuRestaurantFilter;
    @FXML
    private Button addMenuBtn;
    @FXML
    private Button editMenuBtn;
    @FXML
    private Button deleteMenuBtn;

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
    private ComboBox<com.naujokaitis.maistas.model.Menu> menuItemMenuFilter;
    @FXML
    private ComboBox<MenuCategory> menuItemCategoryFilter;
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
    @FXML
    private Button addOrderBtn;
    @FXML
    private Button editOrderBtn;
    @FXML
    private Button deleteOrderBtn;
    @FXML
    private Button chatBtn;

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
    private TableColumn<User, String> userAvgRatingCol;
    @FXML
    private ComboBox<UserRole> userRoleFilter;
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
    private final FilteredList<Restaurant> filteredRestaurants = new FilteredList<>(restaurants, p -> true);

    private final ObservableList<com.naujokaitis.maistas.model.Menu> menus = FXCollections.observableArrayList();
    private final FilteredList<com.naujokaitis.maistas.model.Menu> filteredMenus = new FilteredList<>(menus, p -> true);

    private final ObservableList<com.naujokaitis.maistas.model.MenuItem> menuItems = FXCollections
            .observableArrayList();
    private final FilteredList<com.naujokaitis.maistas.model.MenuItem> filteredMenuItems = new FilteredList<>(menuItems,
            p -> true);

    private final ObservableList<Order> orders = FXCollections.observableArrayList();

    private final ObservableList<User> users = FXCollections.observableArrayList();
    private final FilteredList<User> filteredUsers = new FilteredList<>(users, p -> true);

    // Database access
    private final GenericHibernate<Restaurant> restaurantRepo = new GenericHibernate<>(Restaurant.class);
    private final GenericHibernate<com.naujokaitis.maistas.model.Menu> menuRepo = new GenericHibernate<>(
            com.naujokaitis.maistas.model.Menu.class);
    private final GenericHibernate<com.naujokaitis.maistas.model.MenuItem> menuItemRepo = new GenericHibernate<>(
            com.naujokaitis.maistas.model.MenuItem.class);
    private final GenericHibernate<Order> orderRepo = new GenericHibernate<>(Order.class);
    private final GenericHibernate<User> userRepo = new GenericHibernate<>(User.class);

    @FXML
    private void initialize() {
        setupTables();
        setupFilters();
        configureRoleBasedAccess();
        loadAllData();
        setupTableSelectionListeners();
        updateUserInfo();
        setupTabChangeListener();
    }

    private void setupTabChangeListener() {
        mainTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            updateStatusForCurrentTab();
        });
    }

    private void updateStatusForCurrentTab() {
        Tab currentTab = mainTabPane.getSelectionModel().getSelectedItem();
        if (currentTab == restaurantsTab) {
            statusLabel.setText("Loaded " + restaurants.size() + " restaurants");
        } else if (currentTab == menusTab) {
            statusLabel.setText("Loaded " + menus.size() + " menus");
        } else if (currentTab == menuItemsTab) {
            statusLabel.setText("Loaded " + menuItems.size() + " menu items");
        } else if (currentTab == ordersTab) {
            statusLabel.setText("Loaded " + orders.size() + " orders");
        } else if (currentTab == usersTab) {
            statusLabel.setText("Loaded " + users.size() + " users");
        }
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
        restaurantsTable.setItems(filteredRestaurants);

        // Menus table
        menuNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        menuIdCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getId().toString().substring(0, 8)));
        menuItemCountCol.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getItems().size())
                        .asObject());
        menusTable.setItems(filteredMenus);

        // Menu Items table
        menuItemNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        menuItemCategoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        menuItemPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        menuItemInventoryCol.setCellValueFactory(new PropertyValueFactory<>("inventoryCount"));
        menuItemsTable.setItems(filteredMenuItems);

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
        userAvgRatingCol.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            if (user instanceof RestaurantOwner) {
                // Calculate average rating from owned restaurants
                double avgRating = restaurants.stream()
                        .filter(r -> r.getOwner() != null && r.getOwner().getId().equals(user.getId()))
                        .filter(r -> r.getRating() != null)
                        .mapToDouble(Restaurant::getRating)
                        .average()
                        .orElse(0.0);
                if (avgRating > 0) {
                    return new javafx.beans.property.SimpleStringProperty(String.format("%.1f", avgRating));
                }
                return new javafx.beans.property.SimpleStringProperty("No ratings");
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });
        usersTable.setItems(filteredUsers);

        // Setup order status filter with "All" option (null represents all)
        List<OrderStatus> statusOptions = new java.util.ArrayList<>();
        statusOptions.add(null); // "All" option
        statusOptions.addAll(java.util.Arrays.asList(OrderStatus.values()));
        orderStatusFilter.setItems(FXCollections.observableArrayList(statusOptions));
        orderStatusFilter.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(OrderStatus item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : (item == null ? "All" : item.toString()));
            }
        });
        orderStatusFilter.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(OrderStatus item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "All" : (item == null ? "All" : item.toString()));
            }
        });
        orderStatusFilter.setValue(null); // Default to "All"
    }

    private void setupFilters() {
        // Restaurant filters
        restaurantRatingFilter.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));

        restaurantSearchField.textProperty().addListener((obs, oldVal, newVal) -> updateRestaurantFilter());
        restaurantRatingFilter.valueProperty().addListener((obs, oldVal, newVal) -> updateRestaurantFilter());

        // Menu restaurant filter
        menuRestaurantFilter.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Restaurant item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        menuRestaurantFilter.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Restaurant item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "All Restaurants" : item.getName());
            }
        });
        menuRestaurantFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                filteredMenus.setPredicate(p -> true);
            } else {
                filteredMenus.setPredicate(menu -> {
                    // Check if the restaurant has this menu
                    return newVal.getMenu() != null && newVal.getMenu().getId().equals(menu.getId());
                });
            }
        });

        // Menu Item menu filter
        menuItemMenuFilter.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(com.naujokaitis.maistas.model.Menu item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        menuItemMenuFilter.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(com.naujokaitis.maistas.model.Menu item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "All Menus" : item.getName());
            }
        });
        menuItemMenuFilter.valueProperty().addListener((obs, oldVal, newVal) -> updateMenuItemFilter());

        // Menu Item filters
        menuItemCategoryFilter.setItems(FXCollections.observableArrayList(MenuCategory.values()));
        menuItemCategoryFilter.valueProperty().addListener((obs, oldVal, newVal) -> updateMenuItemFilter());

        // User filters
        userRoleFilter.setItems(FXCollections.observableArrayList(UserRole.values()));
        userRoleFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                filteredUsers.setPredicate(p -> true);
            } else {
                filteredUsers.setPredicate(user -> user.getRole() == newVal);
            }
        });
    }

    private void updateRestaurantFilter() {
        String searchText = restaurantSearchField.getText();
        Integer minRating = restaurantRatingFilter.getValue();

        filteredRestaurants.setPredicate(restaurant -> {
            boolean matchesName = true;
            boolean matchesRating = true;

            if (searchText != null && !searchText.isEmpty()) {
                matchesName = restaurant.getName().toLowerCase().contains(searchText.toLowerCase());
            }

            if (minRating != null) {
                matchesRating = restaurant.getRating() != null && restaurant.getRating() >= minRating;
            }

            return matchesName && matchesRating;
        });
    }

    private void updateMenuItemFilter() {
        com.naujokaitis.maistas.model.Menu selectedMenu = menuItemMenuFilter.getValue();
        MenuCategory selectedCategory = menuItemCategoryFilter.getValue();

        filteredMenuItems.setPredicate(item -> {
            boolean matchesMenu = true;
            boolean matchesCategory = true;

            if (selectedMenu != null) {
                matchesMenu = item.getMenu() != null && item.getMenu().getId().equals(selectedMenu.getId());
            }

            if (selectedCategory != null) {
                matchesCategory = item.getCategory() == selectedCategory;
            }

            return matchesMenu && matchesCategory;
        });
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
            reviewsBtn.setDisable(!selected);
        });

        menusTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean selected = newVal != null;
            editMenuBtn.setDisable(!selected);
            deleteMenuBtn.setDisable(!selected);
        });

        menuItemsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean selected = newVal != null;
            editMenuItemBtn.setDisable(!selected);
            deleteMenuItemBtn.setDisable(!selected);
        });

        ordersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean selected = newVal != null;
            editOrderBtn.setDisable(!selected);
            deleteOrderBtn.setDisable(!selected);
            chatBtn.setDisable(!selected);
        });

        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean selected = newVal != null;
            editUserBtn.setDisable(!selected);
            deleteUserBtn.setDisable(!selected);
        });
    }

    private void loadAllData() {
        handleRefreshRestaurants();
        handleRefreshMenus();
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

            // Update the menu restaurant filter ComboBox
            Restaurant selectedFilter = menuRestaurantFilter.getValue();
            menuRestaurantFilter.setItems(FXCollections.observableArrayList(data));
            if (selectedFilter != null) {
                // Try to re-select the same restaurant
                data.stream()
                        .filter(r -> r.getId().equals(selectedFilter.getId()))
                        .findFirst()
                        .ifPresent(menuRestaurantFilter::setValue);
            }

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

    @FXML
    private void handleShowReviews() {
        Restaurant selected = restaurantsTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        new ReviewDialog(selected).showAndWait();
    }

    // Menu actions
    @FXML
    private void handleRefreshMenus() {
        try {
            List<com.naujokaitis.maistas.model.Menu> data = menuRepo.findAll();
            menus.clear();
            menus.addAll(data);

            // Update the menu item menu filter ComboBox
            com.naujokaitis.maistas.model.Menu selectedFilter = menuItemMenuFilter.getValue();
            menuItemMenuFilter.setItems(FXCollections.observableArrayList(data));
            if (selectedFilter != null) {
                // Try to re-select the same menu
                data.stream()
                        .filter(m -> m.getId().equals(selectedFilter.getId()))
                        .findFirst()
                        .ifPresent(menuItemMenuFilter::setValue);
            }

            statusLabel.setText("Loaded " + data.size() + " menus");
        } catch (Exception e) {
            showError("Load Error", "Failed to load menus", e.getMessage());
        }
    }

    @FXML
    private void handleAddMenu() {
        MenuDialog.showCreateDialog().ifPresent(menu -> {
            // Menu is already saved via Restaurant cascade in MenuDialog
            menus.add(menu);
            statusLabel.setText("Menu created successfully");
            handleRefreshRestaurants(); // Refresh restaurants to show updated menu
        });
    }

    @FXML
    private void handleEditMenu() {
        com.naujokaitis.maistas.model.Menu selected = menusTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        MenuDialog.showEditDialog(selected).ifPresent(menu -> {
            try {
                menuRepo.update(menu);
                menusTable.refresh();
                statusLabel.setText("Menu updated successfully");
            } catch (Exception e) {
                showError("Update Error", "Failed to update menu", e.getMessage());
            }
        });
    }

    @FXML
    private void handleDeleteMenu() {
        com.naujokaitis.maistas.model.Menu selected = menusTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Menu?");
        confirm.setContentText("This will also delete all menu items. Are you sure?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    menuRepo.delete(selected);
                    menus.remove(selected);
                    statusLabel.setText("Menu deleted successfully");
                } catch (Exception e) {
                    showError("Delete Error", "Failed to delete menu", e.getMessage());
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

    @FXML
    private void handleAddOrder() {
        OrderDialog.showCreateDialog().ifPresent(order -> {
            try {
                orderRepo.save(order);
                orders.add(order);
                statusLabel.setText("Order created successfully");
            } catch (Exception e) {
                showError("Add Error", "Failed to create order", e.getMessage());
            }
        });
    }

    @FXML
    private void handleEditOrder() {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        OrderStatusDialog dialog = new OrderStatusDialog(selected);
        dialog.showAndWait().ifPresent(newStatus -> {
            try {
                User currentUser = Session.getInstance().getCurrentUser();
                selected.updateStatus(newStatus, currentUser, dialog.getNote());
                orderRepo.update(selected);
                ordersTable.refresh();
                statusLabel.setText("Order status updated to " + newStatus);
            } catch (Exception e) {
                showError("Update Error", "Failed to update order status", e.getMessage());
            }
        });
    }

    @FXML
    private void handleDeleteOrder() {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Order #" + selected.getId().toString().substring(0, 8));
        confirm.setContentText("Are you sure? This action cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    orderRepo.delete(selected);
                    orders.remove(selected);
                    statusLabel.setText("Order deleted successfully");
                } catch (Exception e) {
                    showError("Delete Error", "Failed to delete order", e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleChat() {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        new ChatDialog(selected).showAndWait();
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
        UserDialog.showCreateDialog().ifPresent(user -> {
            try {
                userRepo.save(user);
                users.add(user);
                statusLabel.setText("User created successfully");
            } catch (Exception e) {
                showError("Add Error", "Failed to create user", e.getMessage());
            }
        });
    }

    @FXML
    private void handleEditUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        UserDialog.showEditDialog(selected).ifPresent(user -> {
            try {
                userRepo.update(user);
                usersTable.refresh();
                statusLabel.setText("User updated successfully");
            } catch (Exception e) {
                showError("Update Error", "Failed to update user", e.getMessage());
            }
        });
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
