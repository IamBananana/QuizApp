package utils;

import models.Question;
import exceptions.InvalidQuestionFormatException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BinaryFileManager {
    private static final String FILE_NAME = "questions.dat";

    public static void saveQuestions(List<Question> questions) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(questions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Question> loadQuestions() {
        List<Question> questions = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            questions = (List<Question>) ois.readObject();

            for (Question q : questions) {
                validateQuestion(q);
            }

        } catch (InvalidQuestionFormatException e) {
            System.err.println("Invalid question format: " + e.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading questions: " + e.getMessage());
        }
        return questions;
    }

    public static void validateQuestion(Question q) throws InvalidQuestionFormatException {
        if (q.getQuestionText() == null || q.getQuestionText().trim().isEmpty()) {
            throw new InvalidQuestionFormatException("Question text cannot be empty.");
        }
        if (q.getChoices() == null || q.getChoices().size() < 2 || q.getChoices().size() > 6) {
            throw new InvalidQuestionFormatException("A question must be between 2 and 6 choices.");
        }
        if (q.getCorrectAnswerIndex() < 0 || q.getCorrectAnswerIndex() >= q.getChoices().size()) {
            throw new InvalidQuestionFormatException("Correct answer index is out of bounds.");
        }
    }
}
