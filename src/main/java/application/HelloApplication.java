package application;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

public class HelloApplication extends Application {
    final double WIDTH = Screen.getPrimary().getBounds().getWidth()*0.40;
    final double HEIGHT = Screen.getPrimary().getBounds().getHeight()*0.40;
    @Override
    public void start(Stage primaryStage) {
        Button adminButton = new Button("Admin Mode");
        Button userButton = new Button("User Mode");

        adminButton.setOnAction(e -> new AdminPanel().start(new Stage()));
        userButton.setOnAction(e -> new QuizUI().start(new Stage()));

        FlowPane flowPane = new FlowPane();
        flowPane.getChildren().addAll(adminButton, userButton);
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setHgap(30);
        flowPane.setStyle("-fx-background-color: #DAC09B");
        Scene scene = new Scene(flowPane, WIDTH, HEIGHT);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Quiz Application");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
