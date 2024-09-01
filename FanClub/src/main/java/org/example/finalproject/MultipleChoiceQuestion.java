package org.example.finalproject;
import java.util.List;
/**
 * Represents a multiple choice question.
 */

public class MultipleChoiceQuestion extends Question {
    /**
     * The options for the multiple choice question.
     * Each multiple choice question contains 3 options.
     */
    private List<String> options;
    /**
     * Constructs a multiple choice question with the specified attributes.
     *
     * @param question    The text of the question.
     * @param answer      The answer to the question.
     * @param picturePath The path to the picture associated with the question.
     * @param options     The options for the multiple choice question.
     */
    public MultipleChoiceQuestion(String question, String answer, String picturePath, List<String> options) {
        super(question, answer, picturePath);
        this.options = options;
    }
    /**
     * Retrieves the options for the multiple choice question.
     *
     * @return The options for the multiple choice question.
     */
    public List<String> getOptions() {
        return options;
    }
    /**
     * Sets the options for the multiple choice question.
     *
     * @param options The options for the multiple choice question.
     */
    public void setOptions(List<String> options) {
        this.options = options;
    }
    /**
     * Retrieves the type of the question.
     *
     * @return The type of the question.
     */
    @Override
    public String getType() {
        return "Multiple Choice";
    }
}
