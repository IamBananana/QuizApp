package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Question;
import utils.BinaryFileManager;
import exceptions.InvalidQuestionFormatException;

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

    private int selectedQuestionIndex = -1; // Track selected question

    @Override
    public void start(Stage primaryStage) {
        Button refreshButton = new Button("View Saved Questions");

        loadQuestions(); // Load existing questions

        saveButton.setOnAction(e -> saveNewQuestion());
        updateButton.setOnAction(e -> updateSelectedQuestion());
        deleteButton.setOnAction(e -> deleteSelectedQuestion());
        refreshButton.setOnAction(e -> updateQuestionListView());

        // Handle question selection
        questionListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            int index = newVal.intValue();
            if (index >= 0 && index < questions.size()) {
                selectedQuestionIndex = index;
                loadSelectedQuestion(questions.get(index));
            }
        });

        VBox layout = new VBox(10,
                new Label("Question:"), questionField,
                new Label("Choices (comma separated):"), choiceField,
                new Label("Correct Answer Index (0-based index):"), answerIndexField,
                saveButton, updateButton, deleteButton, refreshButton,
                new Label("Saved Questions:"), questionListView
        );

        primaryStage.setScene(new Scene(layout, 400, 500));
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

    public static void main(String[] args) {
        launch(args);
    }
}
