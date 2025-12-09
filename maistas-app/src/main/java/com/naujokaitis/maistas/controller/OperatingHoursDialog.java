package com.naujokaitis.maistas.controller;

import com.naujokaitis.maistas.model.OperatingSchedule;
import com.naujokaitis.maistas.model.TimeRange;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

public class OperatingHoursDialog extends Dialog<OperatingSchedule> {

    private final Map<DayOfWeek, TextField> startFields = new HashMap<>();
    private final Map<DayOfWeek, TextField> endFields = new HashMap<>();

    public OperatingHoursDialog(OperatingSchedule currentSchedule) {
        setTitle("Operating Hours");
        setHeaderText("Set operating hours for each day (Format: HH:mm)");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Day"), 0, 0);
        grid.add(new Label("Open"), 1, 0);
        grid.add(new Label("Close"), 2, 0);

        int row = 1;
        for (DayOfWeek day : DayOfWeek.values()) {
            grid.add(new Label(day.name()), 0, row);

            TextField startField = new TextField();
            startField.setPromptText("09:00");
            startFields.put(day, startField);
            grid.add(startField, 1, row);

            TextField endField = new TextField();
            endField.setPromptText("22:00");
            endFields.put(day, endField);
            grid.add(endField, 2, row);

            if (currentSchedule != null && currentSchedule.getHours(day) != null) {
                TimeRange range = currentSchedule.getHours(day);
                startField.setText(range.getStartTime().toString());
                endField.setText(range.getEndTime().toString());
            }

            row++;
        }

        getDialogPane().setContent(grid);

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return createSchedule();
            }
            return null;
        });
    }

    private OperatingSchedule createSchedule() {
        OperatingSchedule schedule = new OperatingSchedule();
        for (DayOfWeek day : DayOfWeek.values()) {
            String startText = startFields.get(day).getText();
            String endText = endFields.get(day).getText();

            if (startText != null && !startText.isBlank() && endText != null && !endText.isBlank()) {
                try {
                    LocalTime start = LocalTime.parse(startText);
                    LocalTime end = LocalTime.parse(endText);
                    schedule.setHours(day, new TimeRange(start, end));
                } catch (DateTimeParseException e) {
                    // Ignore invalid times
                }
            }
        }
        return schedule;
    }
}
