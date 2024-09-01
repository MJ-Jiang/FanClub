package org.example.finalproject;
/**
 * Represents a fill-in-the-blank question.
 */
public class FillInBlankQuestion extends Question {
    /**
     * Constructs a fill-in-the-blank question with the specified attributes.
     *
     * @param question    The text of the question.
     * @param answer      The answer to the question.
     * @param picturePath The path to the picture associated with the question.
     */
    public FillInBlankQuestion(String question, String answer, String picturePath) {
        super(question, answer, picturePath);
    }
    /**
     * Retrieves the type of the question.
     *
     * @return The type of the question.
     */
    @Override
    public String getType() {
        return "Fill in the Blank";
    }
}


