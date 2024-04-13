// ScreenTimeController.java
package com.example.screentimeapp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
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
}
