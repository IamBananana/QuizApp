package application;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.application.Application;
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
    private Timeline timer;

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

        Button nextButton = new Button("Next");
        nextButton.setOnAction(e -> loadNextQuestion());

        HBox hBox = new HBox(progressLabel, progressBar);
        hBox.setSpacing(10);
        VBox vBox = new VBox(questionLabel, hBox, optionsBox, nextButton);
        VBox layout = new VBox(hBox, vBox);
        Scene scene = new Scene(layout, 350, 300);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Quiz");
        primaryStage.show();

        loadNextQuestion();
    }

    private void loadNextQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            // Save quiz results
            QuizResult result = new QuizResult(username, score, questions.size());
            FileManager.saveResult(result);

            new Alert(Alert.AlertType.INFORMATION, "Quiz Completed! Your score: " + score + "/" + questions.size()).show();
            return;
        }

        Question question = questions.get(currentQuestionIndex);
        questionLabel.setText(question.getQuestionText());
        optionsBox.getChildren().clear();

        for (int i = 0; i < question.getChoices().size(); i++) {
            RadioButton button = new RadioButton(question.getChoices().get(i));
            button.setToggleGroup(answerGroup);
            int finalI = i;
            button.setOnAction(e -> {
                if (finalI == question.getCorrectAnswerIndex()) {
                    score++;
                }
            });
            optionsBox.getChildren().add(button);
        }

        double progress = (double) currentQuestionIndex / questions.size();
        progressBar.setProgress(progress);
        progressLabel.setText("Question " + (currentQuestionIndex + 1) + " of " + questions.size());

        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), questionLabel);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        // Timer (10 seconds per question)
//        if (timer != null) timer.stop();
//        timer = new Timeline(new KeyFrame(Duration.seconds(10), e -> loadNextQuestion()));
//        timer.setCycleCount(1);
//        timer.play();

        currentQuestionIndex++;
    }
}
