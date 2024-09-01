package org.example.finalproject;
/**
 * Represents a generic question.
 */
public abstract class Question {
    /**
     * The text of the question.
     */
    private String question;
    /**
     * The answer to the question.
     */
    private String answer;
    /**
     * The path to the picture associated with the question.
     * when showing a question, an associated picture will also be presented at the same time if needed.
     */
    private String picturePath;
    /**
     * Constructs a question with the specified attributes.
     *
     * @param question    The text of the question.
     * @param answer      The answer to the question.
     * @param picturePath The path to the picture associated with the question.
     */
    public Question(String question, String answer, String picturePath) {
        this.question = question;
        this.answer = answer;
        this.picturePath = picturePath;
    }
    /**
     * Retrieves the text of the question.
     *
     * @return The text of the question.
     */
    public String getQuestion() {
        return question;
    }
    /**
     * Sets the text of the question.
     *
     * @param question The text of the question.
     */
    public void setQuestion(String question) {
        this.question = question;
    }
    /**
     * Retrieves the answer to the question.
     *
     * @return The answer to the question.
     */
    public String getAnswer() {
        return answer;
    }
    /**
     * Sets the answer to the question.
     *
     * @param answer The answer to the question.
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    /**
     * Retrieves the path to the picture associated with the question.
     *
     * @return The path to the picture associated with the question.
     */
    public String getPicturePath() {
        return picturePath;
    }
    /**
     * Sets the path to the picture associated with the question.
     *
     * @param picturePath The path to the picture associated with the question.
     */
    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }
    /**
     * Retrieves the type of the question.
     * Subclasses must implement this method to specify the type of the question.
     *
     * @return The type of the question.
     */
    public abstract String getType();

}
