package com.example.screentimeapp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.TextInputDialog;
import javafx.util.Duration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScreenTimeController {

    private ScreenTimeModel model;
    private ScreenTimeView view;

    public ScreenTimeController(ScreenTimeModel model, ScreenTimeView view) {
        this.model = model;
        this.view = view;
        model.addObserver(view);
        setupTimeline();
        fetchDataFromDB();
    }

    private void setupTimeline() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            fetchDataFromDB();
            System.out.println("Data updated");
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void fetchDataFromDB() {
        List<String> appNames = new ArrayList<>();
        List<Integer> appUsages = new ArrayList<>();
        int totalScreenTime = 0;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:src/main/resources/com/example/screentimeapp/digital_wellbeing.db");
             PreparedStatement stmt = conn.prepareStatement("SELECT app_name, current_usage FROM Digital_Wellbeing");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String appName = rs.getString("app_name");
                int currentUsage = rs.getInt("current_usage");
                appNames.add(appName);
                appUsages.add(currentUsage);
                totalScreenTime += currentUsage;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.setTotalScreenTime(totalScreenTime);
        model.updateScreenTimeData(appNames, appUsages);
    }

    public void addTaskDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Task");
        dialog.setHeaderText("Enter task details:");
        dialog.setContentText("Task Name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(taskName -> {
            // Get task details from dialog and add to the model
            model.addTask(taskName);
        });
    }

    public void startPomodoro() {
        // Start pomodoro timer logic
        view.startPomodoro();
    }

    public void startBreak() {
        // Start break timer logic
        view.startBreak();
    }

    public void updateStopwatchDisplay() {
        // Update stopwatch display logic
        view.updateStopwatchDisplay();
    }

    public void endSession() {
        // End pomodoro session logic
        view.endSession();
    }
}
