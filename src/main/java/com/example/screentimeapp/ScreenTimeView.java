// ScreenTimeView.java
package com.example.screentimeapp;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Observable;
import java.util.Observer;
import java.util.*;

public class ScreenTimeView implements Observer {

    private Stage primaryStage;
    private Label totalScreenTimeLabel;
    private VBox appWiseRoot;

    public ScreenTimeView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initialize();
    }

    private void initialize() {
        primaryStage.setTitle("Screen Time App");

        totalScreenTimeLabel = new Label();
        appWiseRoot = new VBox(10);

        VBox root = new VBox(10);
        root.getChildren().addAll(totalScreenTimeLabel, appWiseRoot);

        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
    }

    public void show() {
        primaryStage.show();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof ScreenTimeModel) {
            ScreenTimeModel model = (ScreenTimeModel) o;
            totalScreenTimeLabel.setText("Total Screen Time: " + model.getTotalScreenTime() + " minutes");
            updateAppWiseLabels(model.getAppNames(), model.getAppUsages());
        }
    }

    private void updateAppWiseLabels(List<String> appNames, List<Integer> appUsages) {
        appWiseRoot.getChildren().clear();
        Label appWiseLabel = new Label("App-wise Split of Screen Time:");
        appWiseRoot.getChildren().add(appWiseLabel);

        for (int i = 0; i < appNames.size(); i++) {
            String appName = appNames.get(i);
            int currentUsage = appUsages.get(i);
            Label appLabel = new Label(appName + ": " + currentUsage + " minutes");
            appWiseRoot.getChildren().add(appLabel);
        }
    }
}
