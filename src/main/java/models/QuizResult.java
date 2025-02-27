package models;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class QuizResult {
    private String username;
    private int score;
    private int totalQuestions;
    private LocalDateTime timestamp;

    public QuizResult(String username, int score, int totalQuestions) {
        this.username = username;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.timestamp = LocalDateTime.now();
    }

    public String getUsername() { return username; }
    public int getScore() { return score; }
    public int getTotalQuestions() { return totalQuestions; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("User: %s | Score: %d/%d | Date: %s",
                username, score, totalQuestions, timestamp.format(formatter));
    }
}
