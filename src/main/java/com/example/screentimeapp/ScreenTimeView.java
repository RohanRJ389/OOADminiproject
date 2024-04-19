package com.example.screentimeapp;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

import com.example.screentimeapp.ScreenTimeModel.*;

public class ScreenTimeView implements Observer {

    private Stage primaryStage;
    private Scene homeScene;
    private Scene appWiseScene;
    private Scene todoListScene; // Add Scene for todo list
    private Label totalScreenTimeLabel;
    private Button switchToAppWiseButton;
    private Button backButton;
    private Button todoListButton; // Add button for todo list
    private PieChart appWisePieChart;
    private TableView<AppUsage> appWiseTable;
    private TableView<TodoTask> todoListTable; // Add TableView for todo list
    private VBox goalsAndRewardsGroup; // Add TableView for todo list
    private Label goalsAndRewardsLabel; // Add TableView for todo list
    private Button newGoalButton; // Add TableView for todo list
    private List<String> allgoals;
    private Timeline pomodoroTimer;
    private Button pomodoroInButton;
    private int pomodoroDurationInMinutes = 25;
    private int breakDurationInMinutes = 5;
    private Scene pomodoroScene;
    private Timeline stopwatchTimeline;
    private Label stopwatchLabel;
    private int elapsedTimeInSeconds;
    private Button journalingButton;
    private TextArea journalTextArea;
    private ListView<String> journalListView;
    private Scene journalScene;

    public ScreenTimeView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initialize();
    }

    private void initialize() {
        allgoals = new ArrayList<>();
        primaryStage.setTitle("Screen Time App");

        // Create components for home scene (total screen time)
        totalScreenTimeLabel = new Label();
        switchToAppWiseButton = new Button("App-wise Analytics");
        switchToAppWiseButton.setOnAction(event -> primaryStage.setScene(appWiseScene));

        todoListButton = new Button("Todo List");
        todoListButton.setOnAction(event -> primaryStage.setScene(todoListScene));

        pomodoroInButton = new Button("Pomodoro Timer");
        pomodoroInButton.setOnAction(event -> primaryStage.setScene(pomodoroScene));

        journalingButton = new Button("Journaling");

        goalsAndRewardsGroup = new VBox();
        goalsAndRewardsLabel = new Label("Your goals and rewards:");
        newGoalButton = new Button(" + New goal");
        newGoalButton.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Enter new task");
            dialog.setHeaderText("Enter task name here:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(restext -> {
                allgoals.add(restext);
                goalsAndRewardsGroup.getChildren().add(new Label(restext));
            });
        });
        goalsAndRewardsGroup.getChildren().addAll(goalsAndRewardsLabel, newGoalButton);

        // Create components for app-wise scene
        appWisePieChart = new PieChart();
        Button appWiseBackButton = new Button("Back to Home");
        appWiseBackButton.setOnAction(event -> primaryStage.setScene(homeScene));

        // Create TableView for app-wise analytics
        TableColumn<AppUsage, String> appNameColumn = new TableColumn<>("App Name");
        appNameColumn.setCellValueFactory(new PropertyValueFactory<>("appName"));

        TableColumn<AppUsage, Integer> screenTimeColumn = new TableColumn<>("Screen Time");
        screenTimeColumn.setCellValueFactory(new PropertyValueFactory<>("screenTime"));

        appWiseTable = new TableView<>();
        appWiseTable.getColumns().addAll(appNameColumn, screenTimeColumn);

        // Create components for todo list scene
        Button todoListBackButton = new Button("Back to Home");
        todoListBackButton.setOnAction(event -> primaryStage.setScene(homeScene));

        // Create TableView for todo list
        TableColumn<TodoTask, String> taskColumn = new TableColumn<>("Task");
        taskColumn.setCellValueFactory(new PropertyValueFactory<>("task"));

        TableColumn<TodoTask, Boolean> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<TodoTask, String> deadlineColumn = new TableColumn<>("Deadline");
        deadlineColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));

        todoListTable = new TableView<>();
        todoListTable.getColumns().addAll(taskColumn, statusColumn, deadlineColumn);

        Scene goalsscene = new Scene(goalsAndRewardsGroup, 300, 200);

        Button rewardspagebutton = new Button("Goals & Rewards");
        rewardspagebutton.setOnAction(event -> primaryStage.setScene(goalsscene));

        // Layout for home scene
        VBox homeLayout = new VBox(20); // Increased spacing
        homeLayout.getChildren().addAll(
                totalScreenTimeLabel,
                switchToAppWiseButton,
                todoListButton,
                rewardspagebutton,
                pomodoroInButton,
                journalingButton // Add journaling button
        );
        homeScene = new Scene(homeLayout, 400, 300); // Increased width and height

        // Layout for app-wise scene
        VBox appWiseLayout = new VBox(20); // Increased spacing
        appWiseLayout.getChildren().addAll(appWisePieChart, appWiseTable, appWiseBackButton);
        appWiseScene = new Scene(appWiseLayout, 600, 400);

        // Layout for todo list scene
        VBox todoListLayout = new VBox(20); // Increased spacing
        todoListLayout.getChildren().addAll(todoListTable, todoListBackButton);
        todoListScene = new Scene(todoListLayout, 600, 400);

        stopwatchLabel = new Label("00:00:00");

        // Layout for pomodoro
        VBox pomodoroLayout = new VBox(20); // Increased spacing
        Button pomodoroButton = new Button("Start Pomodoro");
        pomodoroButton.setOnAction(event -> startPomodoro());

        Button breakButton = new Button("Take a Break");
        breakButton.setOnAction(event -> startBreak());

        Button endSessionButton = new Button("End Session");
        endSessionButton.setOnAction(event -> endSession());

        pomodoroLayout.getChildren().addAll(pomodoroButton, breakButton, endSessionButton, stopwatchLabel);
        pomodoroScene = new Scene(pomodoroLayout, 600, 400);

        journalTextArea = new TextArea();
        journalTextArea.setPromptText("Write your journal entry here...");
        journalListView = new ListView<>();

        // Create button for adding an entry
        Button addEntryButton = new Button("Add Entry");
        addEntryButton.setOnAction(event -> {
            String entry = journalTextArea.getText();
            // Add the entry to the list view
            journalListView.getItems().add(entry);
            journalTextArea.clear(); // Clear the text area after adding the entry
        });

        // Create VBox for journaling
        VBox journalingLayout = new VBox(20); // Increased spacing
        Button backToHomeButton = new Button("Back to Home");
        backToHomeButton.setOnAction(event -> primaryStage.setScene(homeScene));

        journalingLayout.getChildren().addAll(
                journalTextArea,
                addEntryButton,
                journalListView,
                backToHomeButton
        );

        Scene journalScene = new Scene(journalingLayout, 600, 400);
        journalingButton.setOnAction(event -> primaryStage.setScene(journalScene));
//        String cssFile = getClass().getResource("styles.css").toExternalForm();
//        homeScene.getStylesheets().add(cssFile);
//        appWiseScene.getStylesheets().add(cssFile);
//        todoListScene.getStylesheets().add(cssFile);
//        pomodoroScene.getStylesheets().add(cssFile);
//        journalScene.getStylesheets().add(cssFile);
        primaryStage.setScene(homeScene);
    }

    public void show() {
        primaryStage.show();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof ScreenTimeModel) {
            ScreenTimeModel model = (ScreenTimeModel) o;
            totalScreenTimeLabel.setText("Total Screen Time: " + model.getTotalScreenTime() + " minutes");
            updateAppWisePieChart(model.getAppNames(), model.getAppUsages());
            updateAppWiseTable(model.getAppNames(), model.getAppUsages());
            populateTodoListTable(); // Populate todo list table
        }
    }

    private void populateTodoListTable() {
        // Fetch data from the database and populate todoListTable
        ObservableList<TodoTask> todoListData = FXCollections.observableArrayList();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:src/main/resources/com/example/screentimeapp/digital_wellbeing.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT task, status, deadline FROM TodoList")) {

            while (rs.next()) {
                String task = rs.getString("task");
                boolean status = rs.getBoolean("status");
                String deadline = rs.getString("deadline");
                todoListData.add(new TodoTask(task, status, deadline));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        todoListTable.setItems(todoListData);
    }

    private void updateAppWisePieChart(List<String> appNames, List<Integer> appUsages) {
        appWisePieChart.getData().clear();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (int i = 0; i < appNames.size(); i++) {
            String appName = appNames.get(i);
            int currentUsage = appUsages.get(i);
            pieChartData.add(new PieChart.Data(appName, currentUsage));
        }
        appWisePieChart.setData(pieChartData);
    }

    private void updateAppWiseTable(List<String> appNames, List<Integer> appUsages) {
        ObservableList<AppUsage> tableData = FXCollections.observableArrayList();
        for (int i = 0; i < appNames.size(); i++) {
            String appName = appNames.get(i);
            int screenTime = appUsages.get(i);
            tableData.add(new AppUsage(appName, screenTime));
        }
        appWiseTable.setItems(tableData);
    }

    void startPomodoro() {
        // Start the stopwatch
        elapsedTimeInSeconds = 0;
        stopwatchTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            elapsedTimeInSeconds++;
            updateStopwatchDisplay();
        }));
        stopwatchTimeline.setCycleCount(Animation.INDEFINITE); // Repeat indefinitely
        stopwatchTimeline.play();
    }

    void startBreak() {
        // Stop the stopwatch and reset to 0
        if (stopwatchTimeline != null) {
            stopwatchTimeline.stop();
            elapsedTimeInSeconds = 0;
            updateStopwatchDisplay();
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Break Time");
        alert.setHeaderText("Enjoy your break!");
        alert.showAndWait();
    }

    void updateStopwatchDisplay() {
        int hours = elapsedTimeInSeconds / 3600;
        int minutes = (elapsedTimeInSeconds % 3600) / 60;
        int seconds = elapsedTimeInSeconds % 60;
        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        stopwatchLabel.setText(timeString);
    }

    void endSession() {
        // Stop the pomodoro timer and return to the home scene
        if (pomodoroTimer != null) {
            pomodoroTimer.stop();
        }
        primaryStage.setScene(homeScene);
    }

}
