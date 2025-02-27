package utils;
import java.io.*;
import models.QuizResult;

public class FileManager {
    private static final String RESULTS_FILE = "results.txt";

    public static void saveResult(QuizResult result) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RESULTS_FILE, true))) {
            writer.write(result.toString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
