// ScreenTimeModel.java
package com.example.screentimeapp.screentimeapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class ScreenTimeModel extends Observable {

    private static ScreenTimeModel instance;
    private int totalScreenTime;
    private List<String> appNames;
    private List<Integer> appUsages;

    public ScreenTimeModel() {
        appNames = new ArrayList<>();
        appUsages = new ArrayList<>();
    }

    public static synchronized ScreenTimeModel getInstance() {
        if (instance == null) {
            instance = new ScreenTimeModel();
        }
        return instance;
    }

    public int getTotalScreenTime() {
        return totalScreenTime;
    }

    public void setTotalScreenTime(int totalScreenTime) {
        this.totalScreenTime = totalScreenTime;
        setChanged();
        notifyObservers();
    }

    public List<String> getAppNames() {
        return appNames;
    }

    public List<Integer> getAppUsages() {
        return appUsages;
    }

    public void updateScreenTimeData(List<String> appNames, List<Integer> appUsages) {
        this.appNames = appNames;
        this.appUsages = appUsages;
        setChanged();
        notifyObservers();
    }

    public static void addTask(String taskName) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:src/main/resources/digital_wellbeing.db");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO TodoList (task, status, deadline) VALUES (?, ?, ?)")) {
            // Set parameters for the prepared statement
            stmt.setString(1, taskName);
            stmt.setBoolean(2, false); // Assuming status is initially false for new tasks
            // Set default deadline or prompt user to input deadline
            stmt.setString(3, "19-04-2024"); // Example default deadline
            // Execute the update
            stmt.executeUpdate();
            // Refresh data after adding the task
//            fetchDataFromDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static class AppUsage {
        private String appName;
        private int screenTime;

        public AppUsage(String appName, int screenTime) {
            this.appName = appName;
            this.screenTime = screenTime;
        }

        public String getAppName() {
            return appName;
        }

        public int getScreenTime() {
            return screenTime;
        }
    }
    public static class TodoTask {
        private String task;
        private boolean status;
        private String deadline;

        public TodoTask(String task, boolean status, String deadline) {
            this.task = task;
            this.status = status;
            this.deadline = deadline;
        }

        public String getTask() {
            return task;
        }

        public boolean getStatus() {
            return status;
        }

        public String getDeadline() {
            return deadline;
        }
    }




}
