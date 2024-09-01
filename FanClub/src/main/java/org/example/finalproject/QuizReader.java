package org.example.finalproject;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The QuizReader class is responsible for reading quiz questions from a file.
 * It extracts questions from the file and saves them as different types of question objects.
 *
 * @author JMJ
 * @version 1.0
 * @see Stars
 * @see Question
 * @see MultipleChoiceQuestion
 * @see FillInBlankQuestion
 */

public class QuizReader {
    private ArrayList<FillInBlankQuestion> blankQuestions;
    private ArrayList<MultipleChoiceQuestion> multipleChoiceQuestions;
    private boolean fileReadSuccessfully;

    /**
     * Constructs a new QuizReader object.
     */
    public  QuizReader(){
        blankQuestions = new ArrayList<>();
        multipleChoiceQuestions = new ArrayList<>();
        fileReadSuccessfully = false;
    }

    /**
     * Retrieves the list of fill-in-the-blank questions read from the file.
     *
     * @return ArrayList of FillInBlankQuestion objects.
     */
    public  ArrayList<FillInBlankQuestion> getBlankQuestions(){
        return  blankQuestions;
    }

    /**
     * Retrieves the list of multiple-choice questions read from the file.
     *
     * @return ArrayList of MultipleChoiceQuestion objects.
     */
    public ArrayList<MultipleChoiceQuestion> getMultipleChoiceQuestions() {
        return multipleChoiceQuestions;
    }
    /**
     * Reads quiz questions from the specified file and populates the lists of fill-in-the-blank
     * questions and multiple-choice questions accordingly.
     *
     * @param name The name of the file (without extension) containing the quiz questions.
     */
    public  void readFile(String name){
        try{
            // Open the file for reading
            File myFile = new File(name + " quiz.txt");
            Scanner myReader = new Scanner(myFile);

            // Iterate through each line in the file
            while (myReader.hasNext()){
                String line = myReader.nextLine();

                // Split the line into parts based on comma separator
                String[] parts = line.split(",");
                // Remove quotes and trim leading/trailing spaces from each part
                for (int i = 0; i < parts.length; i++){
                    parts[i] = parts[i].replaceAll("\"","").trim();
                }
                // Check the question type and create the appropriate question object
                if (parts[0].equals("Fill in the Blank")){
                    // Create a FillInBlankQuestion object and add it to the list
                    FillInBlankQuestion question = new FillInBlankQuestion(parts[1], parts[2], parts[3]);
                    blankQuestions.add(question);
                }else if (parts[0].equals("Multiple Choice")){
                    // Extract the options from the parts and create a MultipleChoiceQuestion object
                    ArrayList<String> optionList = new ArrayList<>();
                    optionList.add(parts[4].replaceAll("\"","").trim());
                    optionList.add(parts[5].replaceAll("\"","").trim());
                    optionList.add(parts[6].replaceAll("\"","").trim());
                    MultipleChoiceQuestion question = new MultipleChoiceQuestion(parts[1], parts[2], parts[3], optionList);
                    multipleChoiceQuestions.add(question);
                }
            }
            myReader.close();
            fileReadSuccessfully = true;
        }catch (FileNotFoundException e){
            System.err.println("File not found: " + name + " quiz.txt");
        }
    }

    /**
     * Returns whether the file was successfully read.
     *
     * @return true if the file was read successfully, false otherwise.
     */
    public boolean isFileReadSuccessfully() {
        return fileReadSuccessfully;
    }
}
