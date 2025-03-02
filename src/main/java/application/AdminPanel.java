package application;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import models.Question;
import utils.BinaryFileManager;
import utils.TextFileManager;
import exceptions.InvalidQuestionFormatException;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminPanel extends Application {
    private List<Question> questions = new ArrayList<>();
    private ListView<String> questionListView = new ListView<>();
    private TextField questionField = new TextField();
    private TextField choiceField = new TextField();
    private TextField answerIndexField = new TextField();
    private Button saveButton = new Button("Save Question");
    private Button updateButton = new Button("Update Question");
    private Button deleteButton = new Button("Delete Question");
    private Button importButton = new Button("Import Questions");
    private Button exportButton = new Button("Export Questions");

    private int selectedQuestionIndex = -1;

    @Override
    public void start(Stage primaryStage) {
        loadQuestions();

        saveButton.setOnAction(e -> saveNewQuestion());
        updateButton.setOnAction(e -> updateSelectedQuestion());
        deleteButton.setOnAction(e -> deleteSelectedQuestion());
        importButton.setOnAction(e -> importQuestions(primaryStage));
        exportButton.setOnAction(e -> exportQuestions(primaryStage));

        questionListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            int index = newVal.intValue();
            if (index >= 0 && index < questions.size()) {
                selectedQuestionIndex = index;
                loadSelectedQuestion(questions.get(index));
            }
        });

        Label titleLabel = new Label("Admin Panel - Manage Questions");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.add(new Label("Question:"), 0, 0);
        gridPane.add(questionField, 1, 0);

        gridPane.add(new Label("Choices (comma separated):"), 0, 1);
        gridPane.add(choiceField, 1, 1);

        gridPane.add(new Label("Correct Answer Index:"), 0, 2);
        gridPane.add(answerIndexField, 1, 2);

        HBox buttonBox = new HBox(10, saveButton, updateButton, deleteButton);
        HBox importExportBox = new HBox(10, importButton, exportButton);

        VBox layout = new VBox(20, titleLabel, gridPane, buttonBox, importExportBox, new Label("Saved Questions:"), questionListView);
        layout.setStyle("-fx-padding: 20px; -fx-alignment: center;");

        applySlideInEffect(layout);
        Scene scene = new Scene(layout, 450, 550);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Admin Panel");
        primaryStage.show();
    }

    private void loadQuestions() {
        questions = BinaryFileManager.loadQuestions();
        updateQuestionListView();
    }

    private void updateQuestionListView() {
        questionListView.getItems().clear();
        for (Question q : questions) {
            questionListView.getItems().add(q.getQuestionText() + " (Correct: " + q.getChoices().get(q.getCorrectAnswerIndex()) + ")");
        }
    }

    private void loadSelectedQuestion(Question question) {
        questionField.setText(question.getQuestionText());
        choiceField.setText(String.join(",", question.getChoices()));
        answerIndexField.setText(String.valueOf(question.getCorrectAnswerIndex()));
    }

    private void saveNewQuestion() {
        try {
            List<String> choices = Arrays.asList(choiceField.getText().split(","));
            int answerIndex = Integer.parseInt(answerIndexField.getText());

            Question question = new Question(questionField.getText(), choices, answerIndex);
            BinaryFileManager.validateQuestion(question);

            questions.add(question);
            BinaryFileManager.saveQuestions(questions);
            updateQuestionListView();

            new Alert(Alert.AlertType.INFORMATION, "Question Saved!").show();
        } catch (InvalidQuestionFormatException ex) {
            new Alert(Alert.AlertType.ERROR, "Invalid Question Format: " + ex.getMessage()).show();
        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.ERROR, "Error: Answer index must be a number.").show();
        }
    }

    private void updateSelectedQuestion() {
        if (selectedQuestionIndex == -1) {
            new Alert(Alert.AlertType.WARNING, "No question selected!").show();
            return;
        }

        try {
            List<String> choices = Arrays.asList(choiceField.getText().split(","));
            int answerIndex = Integer.parseInt(answerIndexField.getText());

            Question updatedQuestion = new Question(questionField.getText(), choices, answerIndex);
            BinaryFileManager.validateQuestion(updatedQuestion);

            questions.set(selectedQuestionIndex, updatedQuestion);
            BinaryFileManager.saveQuestions(questions);
            updateQuestionListView();

            new Alert(Alert.AlertType.INFORMATION, "Question Updated!").show();
        } catch (InvalidQuestionFormatException ex) {
            new Alert(Alert.AlertType.ERROR, "Invalid Question Format: " + ex.getMessage()).show();
        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.ERROR, "Error: Answer index must be a number.").show();
        }
    }

    private void deleteSelectedQuestion() {
        if (selectedQuestionIndex == -1) {
            new Alert(Alert.AlertType.WARNING, "No question selected!").show();
            return;
        }

        questions.remove(selectedQuestionIndex);
        BinaryFileManager.saveQuestions(questions);
        updateQuestionListView();

        new Alert(Alert.AlertType.INFORMATION, "Question Deleted!").show();
    }

    private void importQuestions(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Questions");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            List<Question> importedQuestions = TextFileManager.importQuestions(file);
            if (!importedQuestions.isEmpty()) {
                questions.addAll(importedQuestions);
                BinaryFileManager.saveQuestions(questions);
                updateQuestionListView();
                new Alert(Alert.AlertType.INFORMATION, "Questions Imported Successfully!").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "No valid questions found in file!").show();
            }
        }
    }

    private void exportQuestions(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Questions");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            TextFileManager.exportQuestions(file, questions);
            new Alert(Alert.AlertType.INFORMATION, "Questions Exported Successfully!").show();
        }
    }
    private void applySlideInEffect(VBox layout) {
        layout.setTranslateX(-400); // Start off-screen
        TranslateTransition slideIn = new TranslateTransition(Duration.seconds(0.7), layout);
        slideIn.setToX(0); // Move to normal position
        slideIn.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
