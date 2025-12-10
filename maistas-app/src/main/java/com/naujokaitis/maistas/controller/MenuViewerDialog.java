package com.naujokaitis.maistas.controller;

import com.naujokaitis.maistas.database.CustomHibernate;
import com.naujokaitis.maistas.model.Menu;
import com.naujokaitis.maistas.model.MenuItem;
import com.naujokaitis.maistas.model.Restaurant;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;

public class MenuViewerDialog extends Dialog<Void> {

    public MenuViewerDialog(Restaurant restaurant) {
        setTitle("Menu: " + restaurant.getName());
        setHeaderText(restaurant.getName() + " - Menu Items");

        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().add(closeButtonType);

        TableView<MenuItem> table = new TableView<>();
        
        TableColumn<MenuItem, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<MenuItem, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(250);

        TableColumn<MenuItem, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        catCol.setPrefWidth(100);

        TableColumn<MenuItem, BigDecimal> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(80);
        priceCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("€%.2f", price));
                }
            }
        });

        table.getColumns().addAll(nameCol, descCol, catCol, priceCol);

        // Load menu items
        // We need to fetch the menu items eagerly or use a custom query if the session is closed
        // But assuming restaurant might have detached menu, let's try to reload via CustomHibernate if needed
        // or just check if menu is loaded.
        
        if (restaurant.getMenu() != null) {
             // To be safe, let's rely on what we have, or fetch fresh if empty/lazy
             // For now, assume loaded or fetch fresh
             CustomHibernate repo = new CustomHibernate();
             Restaurant loaded = repo.findRestaurantWithPricingRules(restaurant.getId()); // This also fetches menu items usually if configured
             if (loaded != null && loaded.getMenu() != null) {
                 table.setItems(FXCollections.observableArrayList(loaded.getMenu().getItems()));
             }
        } else {
            table.setPlaceholder(new Label("No menu assigned to this restaurant."));
        }

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.getChildren().add(table);
        table.setPrefHeight(400);
        table.setPrefWidth(650);

        getDialogPane().setContent(content);
    }
}
