package org.example.finalproject;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.List;
import java.util.Arrays;

/**
 * JavaFX application for creating questions for a star fan club.
 * This application allows the fan club leader to create a questionnaire for fans to answer questions.
 * <p>
 *     Firstly, the leader should input information about their favorite star, including their name, birthday, hometown,
 *     and favorite color. Then, a document about the star will be automatically generated. Simultaneously,
 *     a quiz document will also be created, comprising three multiple-choice questions about the star's birthday, hometown, and favorite color.
 * </p>
 * <p>
 *     The questionnaire supports both blank and multiple-choice question types. Each question must include the question itself, the correct answer,
 *     and the pathway to the associated picture. If a multiple-choice type is chosen, three options are required.
 *     Upon submission, all these seven questions along with the initial three questions will constitute the quiz document.
 * </p>
 * @author JMJ
 * @version 1.0
 * @see Stars
 * @see Question
 * @see MultipleChoiceQuestion
 * @see FillInBlankQuestion
 */


public class CreateQuestions extends Application {
    /**
     * This is the initialization number of the problem serial number.
     */
    private int questionNumber = 1;
    /**
     * The main container for the UI elements.
     */
    private VBox wholePane;
    /**
     * A panel used to present star information and questions for a beautiful interface.
     */
    private GridPane gridPane;
    /**
     * The button to go to next page.
     */
    private Button nextButton;
    private HBox nextHBox;
    private TextField questionField;
    private TextField answerTextField;
    private TextField picTextField;
    /**
     * Text box for entering options for multiple choice questions.
     */
    private TextField option1Field;
    private TextField option2Field;
    private TextField option3Field;
    private String name;
    private  Stars star;

    /**
     * Starts the JavaFX application.
     *
     * @param primaryStage The primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage){
        // Create a VBox to contain all UI elements
        wholePane = new VBox();
        wholePane.setAlignment(Pos.CENTER);
        wholePane.setSpacing(20);

        // Create a welcome label and set the font
        Label welcome = new Label("Welcome to StarApp!");
        welcome.setFont(new Font("Arial", 20));

        // Create introduction labels
        Label introduction1 = new Label("※ You can create 10 questions for your favorite star (3 of which are automatically generated). "); 
        Label introduction2 = new Label("※ Fans must answer all questions correctly before they can leave their contact information. ");
        Label introduction3 = new Label("※ Then you can contact them to join the fan club!");
        Label introduction4 = new Label("※ Note: name/hometown/favorite color can only be represented by letters.");

        // Create a GridPane to layout input fields
        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10, 5, 10, 10));
        gridPane.setHgap(6);
        gridPane.setVgap(15);

        // Add labels and text fields in the GridPane
        gridPane.add(new Label("Star Name: "), 0, 0);
        TextField nameTextField = new TextField();
        gridPane.add(nameTextField, 1 ,0);

        gridPane.add(new Label("Birthday: "), 0, 1);
        DatePicker birthPicker = new DatePicker();
        gridPane.add(birthPicker, 1 ,1);

        gridPane.add(new Label("Hometown:"), 0, 2);
        TextField homeField = new TextField();
        gridPane.add(homeField, 1, 2);

        gridPane.add(new Label("Gender:"), 0, 3);
        ComboBox<Stars.Gender> cboGender = new ComboBox<>();
        cboGender.getItems().addAll(Stars.Gender.values());
        gridPane.add(cboGender, 1, 3);

        gridPane.add(new Label("Favorite color:"), 0, 4);
        TextField color = new TextField();
        gridPane.add(color, 1, 4);

        // Create the "Next" button to go to next page
        nextButton = new Button("Next");
        nextHBox = new HBox(nextButton);
        nextHBox.setAlignment(Pos.BASELINE_RIGHT);
        gridPane.add(nextHBox, 1, 5);

        // Add all UI elements to the overall VBox
        wholePane.getChildren().addAll(welcome, introduction1, introduction2, introduction3, introduction4, gridPane);

        // Set the action event handler for the "Next" button
        nextButton.setOnAction(e -> {

            // Get user input from input fields
            name = nameTextField.getText();
            LocalDate birthday = birthPicker.getValue();
            String hometown = homeField.getText();
            Stars.Gender gender = cboGender.getValue();
            String favoriteColor = color.getText();

            //Check if all required fields have been filled in.
            if (name.isEmpty() || birthday == null || hometown.isEmpty() || gender == null || favoriteColor.isEmpty()) {
                showAlert("Error", "Please fill in all the fields.", AlertType.ERROR);
                return;
            }else if (!name.matches("[a-zA-Z\\s]+") || !hometown.matches("[a-zA-Z\\s]+") || !favoriteColor.matches("[a-zA-Z\\s]+")){
                showAlert("Error", "Name/hometown/favorite color can only be represented by letters.", AlertType.ERROR);
                return;
            }

            //Create a new instance of the Stars class with the provided information.
            star = new Stars(name, birthday, hometown, gender, favoriteColor);

            //Write the star information to a file.
            writeToFile(star);

            //Generate and write initial questions about the star to a file.
            writeInitialQuestionsToFile(name, birthday, hometown, favoriteColor);

            //Display the input fields for creating additional questions.
            inputQuestions(wholePane);
        });

        // Create the scene and set it to the primary stage
        Scene scene = new Scene(wholePane,700, 600);
        primaryStage.setTitle("Stars");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Writes star information to a file.
     *
     * @param star The Stars object containing information about the star.
     */
    private void writeToFile(Stars star) {
        try{
            //// Define a formatter to format dates in the "yyyy-MM-dd" pattern
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedBirthday = star.getDate().format(formatter);
            FileWriter myWriter = new FileWriter(name + ".txt");
            myWriter.write("\"" + name + "\","+
                    "\"" + formattedBirthday + "\"," +
                    "\"" + star.getHometown() + "\"," +
                    "\"" + star.getGender() + "\"," +
                    "\"" + star.getColor() + "\"\n");
            myWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    /**
     * Writes the initial questions about the star to a file.
     *
     * @param name The name of the star.
     * @param birthday The birthday of the star.
     * @param hometown The hometown of the star.
     * @param favoriteColor The favorite color of the star.
     */
    private void writeInitialQuestionsToFile(String name, LocalDate birthday, String hometown, String favoriteColor) {
        try {
            FileWriter writer = new FileWriter(name + " quiz.txt");
            // First question
            String[] birthdays = generateRandomDates(birthday);
            String birthdayString = String.join(",", birthdays);
            writer.write(String.format("\"Multiple Choice\",\"what's %s's birthday?\",\"%s\",\"1.jpg\",\"%s\"%n", name, birthday, birthdayString));
            // Second question
            String[] hometowns = generateRandomHometown(hometown);
            String hometownsString = String.join(",", hometowns);
            writer.write(String.format("\"Multiple Choice\",\"where is %s's hometown?\",\"%s\",\"1.jpg\",\"%s\"%n", name, hometown, hometownsString));
            // Third question
            String[] colors = generateRandomColors(favoriteColor);
            String colorsString = String.join(",", colors);
            writer.write(String.format("\"Multiple Choice\",\"what's %s's favorite color?\",\"%s\",\"1.jpg\",\"%s\"%n", name, favoriteColor, colorsString));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates random dates for multiple-choice question options about star's birthday.
     *
     * @param birthday The star's birthday.
     * @return An array of three different dates.
     */
    private  String[] generateRandomDates(LocalDate birthday){
        Random random = new Random();
        int month;
        // Generate a random month until it's different from the star's birth month
        do{
            month = random.nextInt(12) + 1;
        } while (month == birthday.getMonthValue());

        // Generate a date string for the first option by adding 1 year to the star's birth year
        String date1 = String.format("%d-%02d-%02d", birthday.getYear() + 1, birthday.getMonthValue(), birthday.getDayOfMonth());

        // Generate a date string for the second option with the newly generated month and the star's birth year
        String date2 = String.format("%d-%02d-%02d", birthday.getYear(), month, birthday.getDayOfMonth());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedBirthday = birthday.format(formatter);
        return new String[]{ date1, date2, formattedBirthday };
    }
    /**
     * Generates random hometowns for multiple-choice question options about star's hometown.
     *
     * @param hometown The star's hometown.
     * @return An array of three different hometowns.
     */
    private String[] generateRandomHometown(String hometown){
        String[] cities = {"Xuzhou", "Nanjing", "Shanghai", "Beijing", "Xiamen", "Hangzhou"};
        Random random = new Random();
        String city1, city2;
        // Randomly select a city from the array of cities until a city different from the star's hometown is chosen
        do{
            city1 = cities[random.nextInt(cities.length)];
        } while (city1.equalsIgnoreCase(hometown));

        do{
            city2 = cities[random.nextInt(cities.length)];
        } while (city2.equalsIgnoreCase(hometown) || city2.equalsIgnoreCase(city1));
        return  new String[]{city1, hometown, city2};
    }
    /**
     * Generates random colors for multiple-choice question options about star's favourite color.
     *
     * @param favoriteColor The star's favorite color.
     * @return An array of three different colors.
     */
    private  String[] generateRandomColors(String favoriteColor){
        String[] colors = {"Red", "Blue", "Black", "Green", "Orange", "Yellow", "White"};
        Random random = new Random();
        String color1, color2;
        // Randomly select a color from the array of colors until a color different from the star's favorite color is chosen
        do{
            color1 = colors[random.nextInt(colors.length)];
        } while (color1.equalsIgnoreCase(favoriteColor));
        do{
            color2 = colors[random.nextInt(colors.length)];
        } while (color2.equalsIgnoreCase(favoriteColor) || color2.equalsIgnoreCase(color1));
        return new String[] {color1, color2, favoriteColor};
    }

    /**
     * Displays the input fields for creating questions.
     *
     * @param wholePane The main VBox containing the UI elements.
     */
    private void inputQuestions(VBox wholePane){
        wholePane.getChildren().clear();
        gridPane.getChildren().clear();

        // Labels providing instructions to the user
        Label illustration1 = new Label("※ You should choose question type first.");
        Label illustration2 = new Label("※ If you choose multiple choice question, please write three options and the correct option number in answer field.");
        Label illustration3 = new Label("※ Each box needs to be filled in.");

        // HBox to display the question number and question type selection
        HBox hBoxNum = new HBox();
        Label numberLabel = new Label("The NO." + questionNumber + " question: ");

        // ComboBox for selecting the question type
        ObservableList<String> questionTypes = FXCollections.observableArrayList();
        questionTypes.add("Multiple Choice");
        questionTypes.add("Fill in the Blank");
        ComboBox<String> cboQuestion = new ComboBox<>();
        cboQuestion.setItems(questionTypes);
        hBoxNum.getChildren().addAll(numberLabel, cboQuestion);
        hBoxNum.setAlignment(Pos.CENTER);

        // Event handler for the ComboBox to update the UI based on the selected question type
        cboQuestion.setOnAction(e ->{
            String selectedQuestion = cboQuestion.getValue();
            gridPane.getChildren().clear();
            updateUIForQuestionType(selectedQuestion);
        });

        // Add all UI elements to the wholePane VBox
        wholePane.getChildren().addAll(illustration1, illustration2, illustration3, hBoxNum);
    }
    /**
     * Displays an alert dialog with the given title, message, and alert type.
     *
     * @param title The title of the alert dialog.
     * @param message The message to be displayed in the alert dialog.
     * @param alertType The type of the alert dialog.
     */
    private void showAlert(String title, String message, AlertType alertType){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    /**
     * Updates the UI elements based on the selected question type.
     *
     * @param selectedQuestion The selected question type.
     */
    private void updateUIForQuestionType(String selectedQuestion) {
        // Add labels and text fields for question, answer, picture pathway, and options
        gridPane.add(new Label("Question: "), 0, 0);
        questionField = new TextField();
        gridPane.add(questionField, 1, 0);

        gridPane.add(new Label("Answer: "), 0, 1);
        answerTextField = new TextField();
        gridPane.add(answerTextField, 1 ,1);

        gridPane.add(new Label("Picture pathway: "), 0, 2);
        picTextField = new TextField();
        picTextField.setPrefWidth(400);
        gridPane.add(picTextField, 1, 2);

        Label option1Label = new Label("Option 1: ");
        Label option2Label = new Label("Option 2: ");
        Label option3Label = new Label("Option 3: ");
        gridPane.add(option1Label, 0, 3);
        gridPane.add(option2Label, 0, 4);
        gridPane.add(option3Label, 0, 5);

        option1Field = new TextField();
        option2Field = new TextField();
        option3Field = new TextField();
        gridPane.add(option1Field, 1, 3);
        gridPane.add(option2Field, 1, 4);
        gridPane.add(option3Field, 1, 5);
        gridPane.add(nextHBox, 1, 7);

        // Show the gridPane if not already contained in wholePane
        if(!wholePane.getChildren().contains(gridPane))
            wholePane.getChildren().addAll(gridPane);

        // Hide option fields and labels for Fill in the Blank questions
        if (selectedQuestion.equals("Fill in the Blank")){
            gridPane.setVisible(true);
            option1Field.setVisible(false);
            option2Field.setVisible(false);
            option3Field.setVisible(false);
            option1Label.setVisible(false);
            option2Label.setVisible(false);
            option3Label.setVisible(false);
        }

        // Set action for the next button
        setNextButtonAction(selectedQuestion);
    }
    /**
     * Sets the action for the next button based on the selected question type.
     * The button is used to go to next page.
     * @param selectedQuestion The selected question type.
     */
    private void setNextButtonAction(String selectedQuestion){
        nextButton.setOnAction(e -> {

            // Retrieve text from input fields
            String questionText = questionField.getText();
            String answer = answerTextField.getText();
            String option1 = option1Field.getText();
            String option2 = option2Field.getText();
            String option3 = option3Field.getText();
            String picPath = picTextField.getText();

            // Check if selected question type is Fill in the Blank
            if (selectedQuestion.equals("Fill in the Blank")){
                if (questionText.isEmpty() || answer.isEmpty() || picPath.isEmpty()) {
                    showAlert("Error", "Please fill in all spaces", AlertType.ERROR);
                    return;
                }
                // Create Fill in the Blank question object
                Question question = new FillInBlankQuestion(questionText, answer, picPath);

                // Write question to file
                writeToFile(name, question);

            }else {
                if (questionText.isEmpty() || answer.isEmpty() || picPath.isEmpty() || option1.isEmpty() || option2.isEmpty() || option3.isEmpty()) {
                    showAlert("Error", "Please fill in all spaces", AlertType.ERROR);
                    return;
                }
                List<String> options = Arrays.asList(option1, option2, option3);

                // Create Multiple Choice question object
                Question question = new MultipleChoiceQuestion(questionText, answer, picPath, options);

                // Write question to file
                writeToFile(name, question);
            }
            questionNumber++;

            if (questionNumber > 7){
                showAlert("Success", "All questions have been recorded.", AlertType.INFORMATION);
                nextButton.setDisable(true);
            }else{
                inputQuestions(wholePane);
            }
        });
    }
    /**
     * Writes a question object to a file.
     *
     * @param name The name of the star.
     * @param question The question object to be written to the file.
     */
    private void writeToFile(String name, Question question){
        try{
            // Create a FileWriter object to write content to the file. If the file does not exist, a new file will be created;
            // if the file already exists, its content will be cleared.
            FileWriter myWriter = new FileWriter(name + " quiz.txt",true);

            // If it's a fill-in-the-blank question, write the question, answer, and picture path to the file
            if (question instanceof FillInBlankQuestion){
                myWriter.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\"%n", question.getType(),question.getQuestion(),
                        question.getAnswer(),question.getPicturePath()));

            }
            // If it's a multiple-choice question, write the question, answer, picture path, and options to the file
            else if (question instanceof MultipleChoiceQuestion){
                MultipleChoiceQuestion multipleChoiceQuestion =(MultipleChoiceQuestion) question;
                List<String> options = multipleChoiceQuestion.getOptions();

                // Convert the options list to a comma-separated string for writing to the file
                String optionsString = String.join(",", options.subList(0, options.size() - 1));
                optionsString += "," + options.get(options.size() - 1);

                myWriter.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n", question.getType(),
                        multipleChoiceQuestion.getQuestion(), multipleChoiceQuestion.getAnswer(), multipleChoiceQuestion.getPicturePath(), optionsString));
            }
            myWriter.close();
        } catch (IOException e){
            e.printStackTrace();
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
