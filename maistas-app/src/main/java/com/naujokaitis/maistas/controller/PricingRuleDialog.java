package com.naujokaitis.maistas.controller;

import com.naujokaitis.maistas.model.DemandLevel;
import com.naujokaitis.maistas.model.PricingRule;
import com.naujokaitis.maistas.model.TimeRange;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalTime;
import java.util.UUID;

public class PricingRuleDialog extends Dialog<PricingRule> {

    private final TextField nameField;
    private final TextField startTimeField;
    private final TextField endTimeField;
    private final ComboBox<DemandLevel> demandLevelBox;
    private final TextField modifierField;

    public PricingRuleDialog(PricingRule rule) {
        setTitle(rule == null ? "Add Pricing Rule" : "Edit Pricing Rule");
        setHeaderText("Configure dynamic pricing rule");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        nameField = new TextField();
        nameField.setPromptText("Rule Name");

        startTimeField = new TextField();
        startTimeField.setPromptText("HH:mm");

        endTimeField = new TextField();
        endTimeField.setPromptText("HH:mm");

        demandLevelBox = new ComboBox<>(FXCollections.observableArrayList(DemandLevel.values()));

        modifierField = new TextField();
        modifierField.setPromptText("1.2 for +20%, 0.8 for -20%");

        if (rule != null) {
            nameField.setText(rule.getName());
            startTimeField.setText(rule.getTimeRange().getStartTime().toString());
            endTimeField.setText(rule.getTimeRange().getEndTime().toString());
            demandLevelBox.setValue(rule.getDemandLevel());
            modifierField.setText(String.valueOf(rule.getPriceModifier()));
        }

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Start Time:"), 0, 1);
        grid.add(startTimeField, 1, 1);
        grid.add(new Label("End Time:"), 0, 2);
        grid.add(endTimeField, 1, 2);
        grid.add(new Label("Demand Level:"), 0, 3);
        grid.add(demandLevelBox, 1, 3);
        grid.add(new Label("Price Modifier:"), 0, 4);
        grid.add(modifierField, 1, 4);

        getDialogPane().setContent(grid);

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String name = nameField.getText();
                    if (name == null || name.trim().isEmpty()) {
                         Alert alert = new Alert(Alert.AlertType.ERROR);
                         alert.setContentText("Name is required");
                         alert.show();
                         return null;
                    }
                    return new PricingRule(
                            rule == null ? UUID.randomUUID() : rule.getId(),
                            name,
                            new TimeRange(LocalTime.parse(startTimeField.getText()),
                                    LocalTime.parse(endTimeField.getText())),
                            demandLevelBox.getValue(),
                            Double.parseDouble(modifierField.getText()));
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Invalid input: " + e.getMessage());
                    alert.show();
                    return null;
                }
            }
            return null;
        });
    }
}
