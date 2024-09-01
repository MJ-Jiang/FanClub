package org.example.finalproject;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * JavaFX application for presenting questions for a fan club and asking users to answer.
 * <p>
 *      FanQuiz class represents a JavaFX application for presenting questions for a star
 *      and allowing users to answer them. After completing 10 questions, users can leave their contact information.
 *      The application prompts users to input the star's name and presents questions accordingly.
 *      Users can input their answers through multiple choice options or fill-in-the-blank fields.
 *      Upon completing the quiz, users receive feedback on their performance and are prompted to leave
 *      their contact information for future communication.
 * </p>
 *
 * @author JMJ
 * @version 1.0
 * @see QuizReader
 * @see CreateQuestions
 * @see MultipleChoiceQuestion
 * @see FillInBlankQuestion
 */
public class FanQuiz extends Application {
    /**
     * The main container for the UI elements.
     */
    private VBox wholePane;
    /**
     * GridPane for organizing UI elements.
     */
    private GridPane gridPane;
    /**
     * Button for navigating to the next page.
     */
    private Button nextButton;
    /**
     * The current question number.
     */
    private int questionNumber = 1;
    /**
     * ImageView for displaying images.
     */
    private ImageView imageView;
    /**
     * Number of correct answers.
     */
    private int correctAnswers = 0;
    /**
     * Button for check feedback.
     */
    private Button checkButton;
    /**
     * List to store fill-in-the-blank questions.
     */
    private ArrayList<FillInBlankQuestion> blankQuestions;
    /**
     * List to store multiple choice questions.
     */
    private ArrayList<MultipleChoiceQuestion> multipleChoiceQuestions;
    /**
     * The star's name.
     */
    private String name;
    /**
     * The current question text to present.
     */
    private String questionText;

    /**
     * The label for displaying questions.
     */
    private Label questionLabel;
    /**
     * List to store user answers.
     */
    private ArrayList<String> userAnswersList = new ArrayList<>();

    /**
     * Entry point for the JavaFX application.
     * <p>
     *     There is a welcome message on the page, prompting users to enter the name of the star.
     *     After clicking the "Next" button, users can enter the question presentation page.
     *     At this point, the data related to the questions is read from Class QuizReader.
     * </p>
     *
     * @param primaryStage The primary stage for the application.
     */
    @Override
    public void start(Stage primaryStage) {
        // Initialize the UI
        wholePane = new VBox();
        wholePane.setAlignment(Pos.CENTER);
        wholePane.setSpacing(20);
        wholePane.setStyle("-fx-background-color: #FFFFF0;");

        Label welcome = new Label("Welcome to FanApp!");
        welcome.setStyle("-fx-font-family: Verdana; -fx-font-size: 40; -fx-text-fill: #696969;");
        Label introduction = new Label("Input your star's name and complete 10 questions, then you have the chance to join fan club!");
        introduction.setStyle("-fx-font-family: Verdana; -fx-font-size: 12; -fx-text-fill: #696969;");

        // Create a GridPane to layout input fields
        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10, 5, 10, 10));
        gridPane.setHgap(6);
        gridPane.setVgap(15);

        // Add labels and text fields in the GridPane
        Label starNameLabel = new Label("Star's Name: ");
        starNameLabel.setStyle("-fx-font-family: Verdana;-fx-text-fill: #696969;-fx-font-size: 13;");
        gridPane.add(starNameLabel, 0, 0);

        TextField nameTextField = new TextField();
        gridPane.add(nameTextField, 1, 0);

        // Create the "Next" button to go to next page
        nextButton = new Button("Next");
        HBox nextHBox = new HBox(nextButton);
        nextHBox.setAlignment(Pos.BASELINE_RIGHT);
        nextButton.setStyle("-fx-background-color:#FFDEAD; -fx-text-fill: #696969;");
        gridPane.add(nextHBox, 1, 5);

        // Add all UI elements to the overall VBox
        wholePane.getChildren().addAll(welcome, introduction, gridPane);

        nextButton.setOnAction(e -> {
            name = nameTextField.getText();

            // Check if the star's name is provided.
            if (name.isEmpty()) {
                showAlert("Error", "Please fill in the name.", Alert.AlertType.ERROR);
            }else{
                // Create a QuizReader instance and read questions from the file.
                QuizReader reader = new QuizReader();
                reader.readFile(name);
                if (reader.isFileReadSuccessfully()) {
                    // Retrieve questions from the QuizReader.
                    blankQuestions = reader.getBlankQuestions();
                    multipleChoiceQuestions = reader.getMultipleChoiceQuestions();
                    // Display the questions to the user.
                    displayQuestions();
                } else {
                    // Show an error message if the file doesn't exist.
                    showAlert("Error", "File doesn't exist.", Alert.AlertType.ERROR);
                }
            }
        });

        // Create the scene and set it to the primary stage
        Scene scene = new Scene(wholePane, 750, 600);
        primaryStage.setTitle("Fan Club Quiz");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Displays an alert dialog with the specified title, message, and type.
     *
     * @param title     The title of the alert dialog.
     * @param message   The message to display in the alert dialog.
     * @param alertType The type of the alert dialog (e.g., information, error).
     */
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     *  Displays the questions for the user to answer.
     *  <p>
     *      Each page consists of an image on the left and a question on the right. Multiple-choice questions are
     *      presented first, followed by fill-in-the-blank questions once all multiple-choice questions are displayed.
     *      After all questions are completed, a "check" button is provided to obtain feedback.
     *  </p>
     */
    private void displayQuestions() {
        // Clear existing UI elements
        wholePane.getChildren().clear();
        gridPane.getChildren().clear();

        // Set padding and spacing for the grid pane
        gridPane.setPadding(new Insets(10));
        gridPane.setVgap(10);
        gridPane.setHgap(80);

        // Create a VBox to hold the question and answer sections
        VBox questionAndAnswer = new VBox();
        questionAndAnswer.setAlignment(Pos.CENTER_LEFT);
        questionAndAnswer.setSpacing(20);

        questionText = "";
        Label numberLabel = new Label("The NO." + questionNumber + " question: ");
        questionLabel = new Label(questionText);
        numberLabel.setStyle("-fx-font-family: Verdana; -fx-font-size: 13; -fx-text-fill: #696969;");
        questionLabel.setStyle("-fx-font-family: Verdana; -fx-font-size: 13; -fx-text-fill: #696969;");

        // Add question number and text labels to the VBox
        questionAndAnswer.getChildren().addAll(numberLabel, questionLabel);

        // Display multiple choice or fill-in-the-blank question based on the current question number
        if (questionNumber <= multipleChoiceQuestions.size()) {
            displayMultipleChoiceQuestion(questionAndAnswer);
        }else if (questionNumber <= blankQuestions.size() + multipleChoiceQuestions.size()){
            displayFillInBlankQuestion(questionAndAnswer);
        }

        // Add image, question, and next button to the grid pane
        gridPane.add(imageView, 0, 1);
        gridPane.add(questionAndAnswer, 1, 1);
        gridPane.add(nextButton, 1, 5);
        wholePane.getChildren().add(gridPane);
        wholePane.setStyle("-fx-background-color: #FFFFF0;");
        nextButton.setStyle("-fx-background-color:#FFDEAD; -fx-text-fill: #696969;");

        // Display feedback when all questions are answered
        if(questionNumber > blankQuestions.size() + multipleChoiceQuestions.size()) {
            displayFeedback();
        }
    }

    /**
     * Displays a multiple-choice question on the user interface.
     * <p>
     *      Retrieves the current multiple-choice question from the list of questions.
     *      Sets up the layout to display the question and its options.
     *      Adds radio buttons for each option to allow the user to select their answer.
     *      Sets the question text, options, and image based on the current question.
     *      Associates the radio buttons with a toggle group to ensure single selection.
     *      Displays the question image.
     *      Sets up the action for the next button to proceed to the next question.
     * </p>
     *
     * @param questionAndAnswer The VBox container to hold the question and answer options.
     */
    private  void displayMultipleChoiceQuestion(VBox questionAndAnswer) {
        // Retrieve the current multiple-choice question
        MultipleChoiceQuestion currentQuestion = multipleChoiceQuestions.get(questionNumber - 1);
        questionText = currentQuestion.getQuestion();

        // Create a VBox to hold the answer options
        VBox optionsBox = new VBox();
        optionsBox.setSpacing(10);

        // Create a toggle group for radio buttons
        ToggleGroup optionGroup = new ToggleGroup();
        for (String option: currentQuestion.getOptions()){
            // Create a radio button for each option
            RadioButton optionButton = new RadioButton(option);
            optionButton.setStyle("-fx-font-family: Verdana;-fx-text-fill: #696969;-fx-font-size: 13;");
            optionButton.setToggleGroup(optionGroup);
            // Add the radio button to the options VBox
            optionsBox.getChildren().add(optionButton);
        }

        // Set the question text
        questionLabel.setText(questionText);
        // Add the options VBox to the main VBox
        questionAndAnswer.getChildren().add(optionsBox);

        // Display the question image
        displayImage(currentQuestion.getPicturePath());
        // Set the action for the next button based on the selected option
        setNextButtonAction(optionGroup, currentQuestion.getAnswer());
    }

    /**
     * Displays the current fill-in-the-blank question.
     * <p>
     *     Sets up the layout to display the question and an input field for the user's answer.
     *     Retrieves the current fill-in-the-blank question from the list of questions.
     *     Sets the question text based on the current question.
     *     Adds a text field for the user to input their answer.
     *     Displays the question image.
     *     Sets up the action for the next button to proceed to the next question.
     * </p>
     *
     * @param questionAndAnswer The VBox container to which the question and input field will be added.
     */
    private  void displayFillInBlankQuestion(VBox questionAndAnswer) {
        // Retrieve the current fill-in-the-blank question
        FillInBlankQuestion currentQuestion = blankQuestions.get(questionNumber - multipleChoiceQuestions.size() - 1);

        // Set the question text
        questionText = currentQuestion.getQuestion();
        questionLabel.setText(questionText);

        TextField answerField = new TextField();
        questionAndAnswer.getChildren().add(answerField);

        // Display the question image
        displayImage(currentQuestion.getPicturePath());
        // Set up the action for the next button
        setNextButtonAction(answerField, currentQuestion.getAnswer());

    }

    /**
     * Displays an image associated with the current question.
     * <p>
     *      Creates a rectangular clipping area for the image to ensure it fits within specified dimensions.
     *      Loads the image using the provided image path and constructs an ImageView object.
     *      Sets the dimensions of the ImageView to fit within the specified width and height.
     *      Applies the rectangular clipping area to the ImageView to ensure it maintains its dimensions.
     * </p>
     *
     * @param imagePath The file path or URL of the image to be displayed.
     */
    private void displayImage(String imagePath) {
        // Create a rectangular clipping area
        Rectangle clip = new Rectangle(220, 220);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        clip.setFill(Color.WHITE);

        imageView = new ImageView(new Image(imagePath));

        imageView.setFitWidth(220);
        imageView.setFitHeight(220);

        // Apply the rectangular clipping area to the ImageView
        imageView.setClip(clip);
    }

    /**
     * Sets up the action for the next button to proceed to the next question based on the user's input.
     * <p>
     *     Determines the type of input source (ToggleGroup or TextField) to retrieve the user's answer.
     *     Validates if the user has provided an answer, displaying an error message if the answer is empty.
     *     Adds the user's answer to the list of user answers.
     *     Increments the question number and updates the display to show the next question.
     *     Checks if the user's answer matches the correct answer and updates the count of correct answers accordingly.
     * </p>
     *
     * @param actionSource The source of user input (ToggleGroup for multiple-choice questions or TextField for fill-in-the-blank questions).
     * @param correctAnswer The correct answer to the current question.
     */
    private void setNextButtonAction(Object actionSource, String correctAnswer){
        nextButton.setOnAction(e ->{
            String userAnswer = "";

            // Check the type of input source and retrieve the user's answer accordingly
            if (actionSource instanceof ToggleGroup){
                RadioButton selectedButton = (RadioButton) ((ToggleGroup) actionSource).getSelectedToggle();
                if(selectedButton != null){
                    userAnswer = selectedButton.getText();
                }

            }else if (actionSource instanceof TextField){
                userAnswer = ((TextField) actionSource).getText();
            }

            // Validate if the user has provided an answer
            if (userAnswer.isEmpty()){
                showAlert("Error", "Please provide an answer", Alert.AlertType.ERROR);
                return;
            }

            // Add the user's answer to the list of user answers
            userAnswersList.add(userAnswer);

            // Move to the next question and update the display
            questionNumber++;
            displayQuestions();

            // Check if the user's answer matches the correct answer and update the count of correct answers
            if (userAnswer.equalsIgnoreCase(correctAnswer)){
                correctAnswers++;
            }
        });
    }

    /**
     * Displays feedback to the user based on their performance in the quiz.
     * <p>
     *     Clears the existing UI elements from the main pane and grid pane.
     *     Creates a "Check" button for the user to proceed and check feedback.
     *     Sets up the action for the "Check" button to display feedback and collect user contact information.
     *     Provides feedback on the number of questions answered correctly and prompts the user to leave their contact information.
     *     Creates a grid pane to collect user's name and phone number for further contact.
     * </p>
     */
    private void displayFeedback() {
        // Clear existing UI elements
        wholePane.getChildren().clear();
        gridPane.getChildren().clear();

        // Create and style the "Check" button
        checkButton = new Button("Check");
        wholePane.getChildren().add(checkButton);
        wholePane.setStyle("-fx-background-color: #FFFFF0;");
        checkButton.setStyle("-fx-background-color:#FFDEAD; -fx-text-fill: #696969; -fx-min-width: 100px; -fx-min-height: 50px;-fx-font-size: 16px;");

        // Set action for the "Check" button
        checkButton.setOnAction(e->{
            // Display feedback to the user
            Label feedback = new Label();
            wholePane.getChildren().add(feedback);
            feedback.setText("You answered " + correctAnswers + " questions correctly.\n\nLeave your contact information and we will contact you later.");
            feedback.setStyle("-fx-font-family: Verdana; -fx-font-size: 14; -fx-text-fill: #696969;");

            // Create a grid pane to collect user's contact information
            GridPane userDataPane = createUserDataPane();
            wholePane.getChildren().add(userDataPane);
            // Hide the "Check" button after clicking
            checkButton.setVisible(false);
        });
    }

    /**
     * Creates a GridPane for capturing user's contact information.
     *<p>
     *     Sets alignment, padding, and gaps for the grid pane.
     *     Adds labels and text fields for user's name and phone number input.
     *     Adds a "Submit" button to submit the user's information.
     *     Sets up the action for the "Submit" button to validate the input and record user data.
     *</p>
     * @return The GridPane containing input fields for user's contact information.
     */
    private GridPane createUserDataPane(){
        // Create a new grid pane
        GridPane userDataPane = new GridPane();
        userDataPane.setAlignment(Pos.CENTER);
        userDataPane.setPadding(new Insets(10));
        userDataPane.setVgap(10);
        userDataPane.setHgap(10);

        // Label and text field for user's name
        Label fanNameLabel = new Label("Your Name: ");
        fanNameLabel.setStyle("-fx-font-family: Verdana;-fx-text-fill: #696969;-fx-font-size: 13;");
        userDataPane.add(fanNameLabel,0,0);

        TextField fanNameField = new TextField();
        userDataPane.add(fanNameField,1,0);

        // Label and text field for user's phone number
        Label phoneLabel = new Label("Your phone number: ");
        phoneLabel.setStyle("-fx-font-family: Verdana;-fx-text-fill: #696969;-fx-font-size: 13;");
        userDataPane.add(phoneLabel, 0, 1);

        TextField phoneField = new TextField();
        userDataPane.add(phoneField,1,1);

        // "Submit" button to submit user's information
        Button submitButton = new Button("Submit");
        userDataPane.add(submitButton, 1, 5);

        // Set action for the "Submit" button
        setSubmitButtonAction(submitButton, fanNameField, phoneField);

        return userDataPane;
    }

    /**
     * Sets the action for the submit button to save user's contact information.
     * <p>
     *     Validates if the user's name and phone number fields are not empty.
     *     If any field is empty, shows an error message.
     *     If all fields are filled, shows a success message and disables the submit button.
     *     Calls the method to save user data.
     * </p>
     *
     * @param submitButton The submit button.
     * @param fanNameField The text field for entering user's name.
     * @param phoneField   The text field for entering user's phone number.
     */
    private void setSubmitButtonAction(Button submitButton, TextField fanNameField, TextField phoneField){
        submitButton.setStyle("-fx-background-color:#FFDEAD; -fx-text-fill: #696969;");
        submitButton.setOnAction(e ->{
            if (fanNameField.getText().isEmpty() || phoneField.getText().isEmpty()) {
                showAlert("Error", "Please fill in all blanks.", Alert.AlertType.ERROR);}
            else{
                showAlert("Success", "Your data has been recorded.", Alert.AlertType.INFORMATION);
                submitButton.setDisable(true);
                saveUserDate(fanNameField.getText(), phoneField.getText());
            }
        });
    }

    /**
     * Saves the user's contact information to a file.
     * <p>
     *      Constructs a string containing user information and submission time.
     *      Appends each user's answer to the string.
     *      Gets the current date and time as the submission time.
     *      Formats the submission time according to the specified pattern.
     *      Writes the user information to a text file named after the star's name followed by "fan.txt".
     * </p>
     *
     * @param fanName     The user's name.
     * @param phoneNumber The user's phone number.
     */
    private void saveUserDate(String fanName, String phoneNumber){
        try{
            String fanInfo = "Name: " + fanName + ", " +
                        "Phone Number: " + phoneNumber + ", " +
                        "Correct Answers: " + correctAnswers + ", ";
            for (String answer : userAnswersList){
                fanInfo += answer + ", ";
            }
            LocalDateTime submitTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            fanInfo += submitTime.format(formatter) + "\n";
            FileWriter writer = new FileWriter(name +  " fan.txt", true);
            writer.write(fanInfo);
            writer.close();
        }catch (IOException ei){
            ei.printStackTrace();
        }
    }

    /**
     * Main method to launch the application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args){
        Application.launch(args);
    }
}
