package application;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Question;
import utils.BinaryFileManager;
import utils.FileManager;
import models.QuizResult;
import javafx.animation.ScaleTransition;
import javafx.scene.text.Text;

import java.util.List;

public class QuizUI extends Application {
    private int currentQuestionIndex = 0;
    private int score = 0;
    private String username;
    private List<Question> questions;

    private Label questionLabel = new Label();
    private Label progressLabel = new Label();
    private ProgressBar progressBar = new ProgressBar(0);
    private ToggleGroup answerGroup = new ToggleGroup();
    private VBox optionsBox = new VBox();
    private Button nextButton = new Button("Next");

    @Override
    public void start(Stage primaryStage) {
        questions = BinaryFileManager.loadQuestions();
        if (questions.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "No questions available!").show();
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("User Identification");
        dialog.setHeaderText("Enter your name:");
        dialog.setContentText("Name:");
        dialog.showAndWait().ifPresent(name -> username = name);

        // Styling
        questionLabel.getStyleClass().add("question-label");
        nextButton.getStyleClass().add("next-button");

        HBox progressBox = new HBox(progressLabel, progressBar);
        progressBox.setSpacing(10);
        progressBox.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(questionLabel, progressBox, optionsBox, nextButton);
        vBox.setSpacing(15);
        vBox.setPadding(new Insets(20));
        vBox.setAlignment(Pos.CENTER);


        Scene scene = new Scene(vBox, 400, 350);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Quiz");
        primaryStage.setResizable(false);
        primaryStage.show();

        nextButton.setOnAction(e -> loadNextQuestion());
        loadNextQuestion();
    }

    private void loadNextQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            QuizResult result = new QuizResult(username, score, questions.size());
            FileManager.saveResult(result);
//            new Alert(Alert.AlertType.INFORMATION, "Quiz Completed! Your score: " + score + "/" + questions.size()).show();
            showFinalScore();
            return;
        }

        Question question = questions.get(currentQuestionIndex);
        questionLabel.setText(question.getQuestionText());
        optionsBox.getChildren().clear();

        for (int i = 0; i < question.getChoices().size(); i++) {
            RadioButton button = new RadioButton(question.getChoices().get(i));
            button.setToggleGroup(answerGroup);
            button.getStyleClass().add("option-button");

            int finalI = i;
            button.setOnAction(e -> {
                if (finalI == question.getCorrectAnswerIndex()) {
                    score++;
                }
            });

            optionsBox.getChildren().add(button);
        }

        double progress = (double) (currentQuestionIndex + 1) / questions.size();
        progressBar.setProgress(progress);
        progressLabel.setText("Question " + (currentQuestionIndex + 1) + " of " + questions.size());

        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), questionLabel);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        currentQuestionIndex++;
    }

    private void showFinalScore() {
        Text scoreText = new Text("Congratulations! Your final score is: " + score + "/" + questions.size() + "!");
        scoreText.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        ScaleTransition scale = new ScaleTransition(Duration.seconds(1), scoreText);
        scale.setFromX(0);
        scale.setToX(1);
        scale.setFromY(0);
        scale.setToY(1);
        scale.play();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getDialogPane().setContent(scoreText);
        alert.setHeaderText(null);
        alert.setTitle("Quiz Completed!");
        alert.setHeight(150);
        alert.setWidth(550);
        alert.setResizable(false);
        alert.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
