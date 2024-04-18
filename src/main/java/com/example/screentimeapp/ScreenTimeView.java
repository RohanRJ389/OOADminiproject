package com.example.screentimeapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.*;
import java.sql.*;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javafx.scene.control.TextInputDialog;

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
    public ScreenTimeView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initialize();
    }

    private void initialize() {
        allgoals= new ArrayList();
        primaryStage.setTitle("Screen Time App");

        // Create components for home scene (total screen time)
        totalScreenTimeLabel = new Label();
        switchToAppWiseButton = new Button("App-wise Analytics");
        switchToAppWiseButton.setOnAction(event -> primaryStage.setScene(appWiseScene));
        todoListButton = new Button("Todo List"); // Create button for todo list
        todoListButton.setOnAction(event -> primaryStage.setScene(todoListScene)); // Set action for todo list button


        goalsAndRewardsGroup = new VBox();
        goalsAndRewardsLabel = new Label("Your goals and rewards:");
        newGoalButton = new Button(" + New goal");
        newGoalButton.setOnAction(event -> {
            
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Enter new task");
            dialog.setHeaderText("Enter task name here:");
            
            Optional<String> result = dialog.showAndWait();
            String restext = result.get();
            allgoals.add(restext);
            
            goalsAndRewardsGroup.getChildren().add(new Label(restext));
            System.out.println(goalsAndRewardsGroup.getChildren());
            goalsAndRewardsGroup.resize(400, 400);
        });
        goalsAndRewardsGroup.getChildren().addAll(goalsAndRewardsLabel, newGoalButton);
        
      


        // Create components for app-wise scene
        appWisePieChart = new PieChart();
        Button appWiseBackButton = new Button("Back to Home");
        appWiseBackButton.setOnAction(event -> primaryStage.setScene(homeScene)); // Set action for app-wise back button

        // Create TableView for app-wise analytics
        TableColumn<AppUsage, String> appNameColumn = new TableColumn<>("App Name");
        appNameColumn.setCellValueFactory(new PropertyValueFactory<>("appName"));

        TableColumn<AppUsage, Integer> screenTimeColumn = new TableColumn<>("Screen Time");
        screenTimeColumn.setCellValueFactory(new PropertyValueFactory<>("screenTime"));

        appWiseTable = new TableView<>();
        appWiseTable.getColumns().addAll(appNameColumn, screenTimeColumn);

        // Create components for todo list scene
        Button todoListBackButton = new Button("Back to Home");
        todoListBackButton.setOnAction(event -> primaryStage.setScene(homeScene)); // Set action for todo list back button

        // Create TableView for todo list
        TableColumn<TodoTask, String> taskColumn = new TableColumn<>("Task");
        taskColumn.setCellValueFactory(new PropertyValueFactory<>("task"));

        TableColumn<TodoTask, Boolean> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<TodoTask, String> deadlineColumn = new TableColumn<>("Deadline");
        deadlineColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));

        todoListTable = new TableView<>();
        todoListTable.getColumns().addAll(taskColumn, statusColumn, deadlineColumn);

        Scene goalsscene = new Scene(goalsAndRewardsGroup, 300, 200); // Width and height of the scene
        
        Button rewardspagebutton = new Button("Goals & Rewards");
        rewardspagebutton.setOnAction(event -> primaryStage.setScene(goalsscene)); // Set action for todo list back button

        
        // Layout for home scene
        VBox homeLayout = new VBox(10);
        homeLayout.getChildren().addAll(totalScreenTimeLabel, switchToAppWiseButton, todoListButton,rewardspagebutton); // Add todo list button
        homeScene = new Scene(homeLayout, 300, 200);



        // Layout for app-wise scene
        VBox appWiseLayout = new VBox(10);
        appWiseLayout.getChildren().addAll(appWisePieChart, appWiseTable, appWiseBackButton); // Add TableView to layout
        appWiseScene = new Scene(appWiseLayout, 600, 400);

        // Layout for todo list scene
        VBox todoListLayout = new VBox(10);
        todoListLayout.getChildren().addAll(todoListTable, todoListBackButton); // Add TableView to layout
        todoListScene = new Scene(todoListLayout, 600, 400);

        // Set home scene as initial scene
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
             ResultSet rs = ((Statement) stmt).executeQuery("SELECT task, status, deadline FROM TodoList")) {

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

    // Define a class to represent app usage
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

    // Define a class to represent todo list tasks
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
