package com.example.screentimeapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ScreenTimeView implements Observer {

    private Stage primaryStage;
    private Scene homeScene;
    private Scene appWiseScene;
    private Label totalScreenTimeLabel;
    private Button switchToAppWiseButton;
    private Button backButton;
    private PieChart appWisePieChart;
    private TableView<AppUsage> appWiseTable; // Add TableView for app-wise analytics

    public ScreenTimeView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initialize();
    }

    //    private void initialize() {
//        primaryStage.setTitle("Screen Time App");
//
//        // Create components for home scene (total screen time)
//        totalScreenTimeLabel = new Label();
//        switchToAppWiseButton = new Button("App-wise Analytics");
//        switchToAppWiseButton.setOnAction(event -> primaryStage.setScene(appWiseScene));
//
//        // Create components for app-wise scene
//        appWisePieChart = new PieChart();
//        backButton = new Button("Back to Home");
//        backButton.setOnAction(event -> primaryStage.setScene(homeScene));
//
//        // Create TableView for app-wise analytics
//        TableColumn<AppUsage, String> appNameColumn = new TableColumn<>("App Name");
//        appNameColumn.setCellValueFactory(new PropertyValueFactory<>("appName"));
//
//        TableColumn<AppUsage, Integer> screenTimeColumn = new TableColumn<>("Screen Time");
//        screenTimeColumn.setCellValueFactory(new PropertyValueFactory<>("screenTime"));
//
//        appWiseTable = new TableView<>();
//        appWiseTable.getColumns().addAll(appNameColumn, screenTimeColumn);
//
//        // Layout for home scene
//        VBox homeLayout = new VBox(10);
//        homeLayout.getChildren().addAll(totalScreenTimeLabel, switchToAppWiseButton);
//        homeScene = new Scene(homeLayout, 300, 200);
//
//        // Layout for app-wise scene
//        VBox appWiseLayout = new VBox(10);
//        appWiseLayout.getChildren().addAll(appWisePieChart, appWiseTable, backButton); // Add TableView to layout
//        appWiseScene = new Scene(appWiseLayout, 600, 400);
//
//        // Set home scene as initial scene
//        primaryStage.setScene(homeScene);
//    }
    private void initialize() {
        primaryStage.setTitle("Screen Time App");

        // Create components for home scene (total screen time)
        totalScreenTimeLabel = new Label();
        switchToAppWiseButton = new Button("App-wise Analytics");
        switchToAppWiseButton.setOnAction(event -> primaryStage.setScene(appWiseScene));

        // Create components for app-wise scene
        appWisePieChart = new PieChart();
        backButton = new Button("Back to Home");
        backButton.setOnAction(event -> primaryStage.setScene(homeScene));

        // Create TableView for app-wise analytics
        TableColumn<AppUsage, String> appNameColumn = new TableColumn<>("App Name");
        appNameColumn.setCellValueFactory(new PropertyValueFactory<>("appName"));

        TableColumn<AppUsage, Integer> screenTimeColumn = new TableColumn<>("Screen Time");
        screenTimeColumn.setCellValueFactory(new PropertyValueFactory<>("screenTime"));

        appWiseTable = new TableView<>();
        appWiseTable.getColumns().addAll(appNameColumn, screenTimeColumn);

        // Layout for home scene
        VBox homeLayout = new VBox(10);
        homeLayout.getChildren().addAll(totalScreenTimeLabel, switchToAppWiseButton);
        homeScene = new Scene(homeLayout, 300, 200);

        // Layout for app-wise scene
        VBox appWiseLayout = new VBox(10);
        appWiseLayout.getChildren().addAll(appWisePieChart, appWiseTable, backButton); // Add TableView to layout
        appWiseScene = new Scene(appWiseLayout, 600, 400);

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
            updateAppWiseTable(model.getAppNames(), model.getAppUsages()); // Update TableView
        }
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


}
