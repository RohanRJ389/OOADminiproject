// ScreenTimeApp.java
package com.example.screentimeapp;
//checking
import javafx.application.Application;
import javafx.stage.Stage;

public class ScreenTimeApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        ScreenTimeModel model = new ScreenTimeModel();
        ScreenTimeView view = new ScreenTimeView(primaryStage);
        ScreenTimeController controller = new ScreenTimeController(model, view);

        view.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
