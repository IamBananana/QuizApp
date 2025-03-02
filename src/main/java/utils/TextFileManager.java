package utils;

import models.Question;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextFileManager {
    public static void exportQuestions(File file, List<Question> questions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Question q : questions) {
                writer.write(q.getQuestionText() + ";" + String.join(",", q.getChoices()) + ";" + q.getCorrectAnswerIndex());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Question> importQuestions(File file) {
        List<Question> importedQuestions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 3) {
                    String questionText = parts[0];
                    List<String> choices = Arrays.asList(parts[1].split(","));
                    int correctIndex = Integer.parseInt(parts[2]);
                    importedQuestions.add(new Question(questionText, choices, correctIndex));
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return importedQuestions;
    }
}
