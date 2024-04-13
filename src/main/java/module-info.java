module com.example.screentimeapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.datatransfer;
    requires java.sql;


    opens com.example.screentimeapp to javafx.fxml;
    exports com.example.screentimeapp;
//    exports com.example.screentimeapp.controller;
//    opens com.example.screentimeapp.controller to javafx.fxml;
//    exports com.example.screentimeapp.model;
//    opens com.example.screentimeapp.model to javafx.fxml;
//    exports com.example.screentimeapp.view;
//    opens com.example.screentimeapp.view to javafx.fxml;
}