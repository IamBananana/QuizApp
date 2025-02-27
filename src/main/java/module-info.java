module com.example.quizapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens application to javafx.fxml;
    exports application;
    exports models;
    opens models to javafx.fxml;
    exports utils;
    opens utils to javafx.fxml;
    exports exceptions;
    opens exceptions to javafx.fxml;
}