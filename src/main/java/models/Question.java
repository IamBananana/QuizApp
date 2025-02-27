package models;
import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {
    private static final long serialVersionUID = 1L;
    private String questionText;
    private List<String> choices;
    private int correctAnswerIndex;

    public Question(String questionText, List<String> choices, int correctAnswerIndex) {
        if (choices.size() < 2 || choices.size() > 6) throw new IllegalArgumentException("Choices must be between 2 and 6");
        this.questionText = questionText;
        this.choices = choices;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getQuestionText() { return questionText; }
    public List<String> getChoices() { return choices; }
    public int getCorrectAnswerIndex() { return correctAnswerIndex; }
}
