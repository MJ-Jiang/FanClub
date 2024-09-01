package org.example.finalproject;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Introduction:
 * This is a link-up game, there will be a 10*10 matrix showing 100 pictures, each picture is a tile to be removed.
 * When the player finds 2 identical tiles, he or she selects these 2 tiles using mouse, then if there is a path connecting
 * these 2 tiles with no more than 3 lines (The lines can't go across other tiles.), then they can be removed from the pane.
 * After being removed, their former location is replaced with space area, making way for removing other tiles.
 * If all the tiles are removed, the player wins and he or she can see the total time used. The time is also recorded in scores.
 */

/** NB!
 If you run this program on your computer, be careful with the location of the 10 image files and the location of "scores.txt".
 You may need to modify the path to locate these files correctly.
 */

public class LinkGame extends Application {
    Image[] images = new Image[10];
    int[][] matrix;
    Pane root = new Pane();
    Pane pane = new Pane();
    Label timerLabel;
    Timeline timelineTimer;
    int centiSecondsElapsed;
    Button backToMenu;
    boolean isRemovable = false;
    Scene gameScene;
    boolean isWinning;
    Scene scoreScene;
    String gameResult;
    ArrayList<String> scores;

    public void start(Stage primaryStage) throws IOException {
        //create the menu scene for the game.
        Pane menuPane = new Pane();
        Button startButton = new Button("Start Game");
        Button ruleButton = new Button("Game Rules");
        Button scoreButton = new Button("Scores");
        VBox vBox = new VBox(30);
        vBox.setPadding(new Insets(30));
        vBox.setAlignment(Pos.CENTER);
        vBox.setLayoutX(460);
        vBox.setLayoutY(300);
        vBox.getChildren().addAll(startButton, ruleButton, scoreButton);
        menuPane.getChildren().add(vBox);
        Scene menuScene = new Scene(menuPane, 1000, 768);
        //when user click the start button, the game starts and shows the game scene.
        startButton.setOnMouseClicked(e -> {
            primaryStage.setScene(gameScene);
            /*create a MatrixWithRandomNumbers object and call the generateMatrix() method to generate a 12*12 matrix.
            This matrix contains two parts:
            its center area, which is a 10*10 matrix, consists of 100 integers from 0-9, ten for each number.
            The 100 integers are arranged in random order.
            The center area is surrounded with the number -1, making it a 12*12 matrix.
             */
            MatrixWithRandomNumbers matrixWithRandomNumbers = new MatrixWithRandomNumbers();
            matrix = matrixWithRandomNumbers.generateMatrix();
            paintPics(); //paint the game pictures for the first time
            startTimer(); //start the timer
        });
        //when user click the rule button, shows the rule scene.
        ruleButton.setOnMouseClicked(e -> {
            Pane rulePane = new Pane();
            VBox ruleLayout = new VBox(20);
            Text rule = new Text("1. Find 2 identical tiles.\n\n" + "2. Click these 2 tiles using your mouse.\n\n"
                    + "3. If there is a path connecting these 2 tiles with no more than 3 lines, they can be removed.\n\n"
                    + "4. Repeat step 1-3 until you remove all the tiles. Try to be as quick as possible!"
            );
            Button ruleToMenu = new Button("Back to menu");
            ruleLayout.getChildren().addAll(rule, ruleToMenu);
            ruleLayout.setLayoutX(50);
            ruleLayout.setLayoutY(50);
            rulePane.getChildren().add(ruleLayout);
            Scene ruleScene = new Scene(rulePane, 1000, 768);
            ruleToMenu.setOnMouseClicked(event -> {
                primaryStage.setScene(menuScene);
            });
            primaryStage.setScene(ruleScene);
        });
        //when user click the score button, shows the score scene.
        scoreButton.setOnMouseClicked(e -> {
            Pane scorePane = new Pane();
            VBox scoreLayout = new VBox(20);
            Label scoreLabel = new Label("History scores");
            Button scoreToMenu = new Button("Back to menu");
            try {
                scores = new ArrayList<>(fileReader());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            ListView<String> lv = new ListView<>(FXCollections.observableArrayList(scores));
            ScrollPane scoreList = new ScrollPane(lv);
            scoreLayout.setAlignment(Pos.CENTER);
            scoreLayout.getChildren().addAll(scoreLabel, scoreList, scoreToMenu);
            scoreLayout.setLayoutX(360);
            scoreLayout.setLayoutY(100);
            scorePane.getChildren().add(scoreLayout);
            scoreScene = new Scene(scorePane, 1000, 768);
            scoreToMenu.setOnMouseClicked(event -> {
                primaryStage.setScene(menuScene);
            });
            primaryStage.setScene(scoreScene);
        });
        //create an array to save 10 pictures, each picture has a size of 64*64.
        for (int i = 0; i < 10; i++) {
            images[i] = new Image("D:/maura_computer science/BPIT/1s java/documents/others/liu" +i + ".png", 64, 64, false, true);
        }

        root.getChildren().addAll(pane, drawGameControl()); //combine the game pane and the game control together.
        int[] imagePosition = {-1, -1, -1, -1}; //create an array to save the coordinates of pictures, each picture has 2 coordinates, so 4 in total.

        pane.setOnMouseClicked(e -> {
            int x = (int) e.getX();
            int y = (int) e.getY();
            if ((64 <= x && x <= 704) && (64 <= y && y <= 704)) { //Only when the user clicks on a picture, this counts as a meaningful action.
                int j = x / 64; //get the picture's column coordinate in the matrix.
                int i = y / 64; //get the picture's row coordinate in the matrix.
                Rectangle rectangle = new Rectangle(j * 64, i * 64, 64, 64); //draw a rectangle for the picture the user clicks on.
                rectangle.setStroke(Color.BLACK);
                rectangle.setFill(Color.TRANSPARENT);
                pane.getChildren().add(rectangle);
                //if imagePosition[0] == -1, this means the user hasn't chosen the first picture, so save the picture's coordinates in the first 2 positions.
                if (imagePosition[0] == -1) {
                    imagePosition[0] = i;
                    imagePosition[1] = j;
                }
                //if the first 2 positions are already used, then save the coordinates of the second picture in the 3rd and 4th position.
                else {
                    imagePosition[2] = i;
                    imagePosition[3] = j;
                }
                if (imagePosition[3] != -1) { //if imagePosition[3] != -1, this means the user has chosen 2 pictures, it is time to decide if the 2 pictures can be removed.
                    checkRemovable(imagePosition[0], imagePosition[1], imagePosition[2], imagePosition[3]); //run the checkRemovable method to check if the 2 pictures can be removed.
                    if (isRemovable) {
                        //if the 2 pictures can be removed, then change their matrix value to -1, so that the 2 pictures won't be drawn again.
                        matrix[imagePosition[0]][imagePosition[1]] = -1;
                        matrix[imagePosition[2]][imagePosition[3]] = -1;
                        //initialize the imagePosition array to save coordinates of future pictures.
                        imagePosition[0] = -1;
                        imagePosition[1] = -1;
                        imagePosition[2] = -1;
                        imagePosition[3] = -1;
                        checkWin(); //Check if the user has removed all the pictures.
                        isRemovable = false; //initialize the isRemovable flag.
                    } else {
                        //if the 2 pictures can't be removed, then initialize the imagePosition array to save coordinates of future pictures.
                        imagePosition[0] = -1;
                        imagePosition[1] = -1;
                        imagePosition[2] = -1;
                        imagePosition[3] = -1;
                        isRemovable = false;
                    }
                    //repaint the pictures, and use animation to show the removing process.
                    javafx.animation.Timeline timeline = new javafx.animation.Timeline(new javafx.animation.KeyFrame(Duration.seconds(0.1), event -> {
                        paintPics();
                    }));
                    timeline.setCycleCount(1);
                    timeline.play();
                }
            }
        });
        //When the user clicks on the back to menu button during a game or after a game, the game returns to menu scene and stop recording time.
        backToMenu.setOnMouseClicked(e -> {
            isWinning = false;
            stopTimer();
            primaryStage.setScene(menuScene);
        });


        gameScene = new Scene(root, 1000, 768);
        primaryStage.setScene(menuScene);
        primaryStage.setTitle("Link-up Game");
        primaryStage.show();
    }


    //this is a method to show some animation then drawing the lines.
    private void showAnimation(Line... lines) {
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(new javafx.animation.KeyFrame(Duration.seconds(0.1), event -> {
            pane.getChildren().removeAll(lines);
        }));
        timeline.setCycleCount(1);
        timeline.play();
    }

    //this is a method to draw lines between 2 pictures, given the 4 coordinates of 2 pictures.
    private void drawLines(int x1, int y1, int x2, int y2) {
        Line line = new Line(64 * y1 + 32, 64 * x1 + 32, 64 * y2 + 32, 64 * x2 + 32);
        pane.getChildren().add(line);
        showAnimation(line);
    }

    //this is a method to draw a matrix of 10*10 pictures, it is called every time 2 pictures are removed from the pane.
    public void paintPics() {
        pane.getChildren().clear(); //clear everything in the pane.
        for (int i = 1; i < 11; i++) {
            for (int j = 1; j < 11; j++) {
                if (matrix[i][j] != -1) { //only when the number is not -1, a picture can be drawn.
                    ImageView imageView = new ImageView(images[matrix[i][j]]);
                    imageView.setLayoutX(64 * j);
                    imageView.setLayoutY(64 * i);
                    pane.getChildren().add(imageView);
                }
            }
        }
        if (isWinning) { //if the user has removed all the pictures, the isWinning flag is set to true. Then draw the winning hint in the pane.
            gameResult = timerLabel.getText();
            fileWriter(gameResult); //write the time used in the scores.txt.
            Label winningText = new Label("You win! Total time: " + gameResult);
            winningText.setLayoutX(350);
            winningText.setLayoutY(300);
            pane.getChildren().add(winningText);
            stopTimer(); //stop the timer.
        }
    }

    //this is a method to check if the 2 pictures are removable given the 4 coordinates of 2 pictures. And this is very long, it needs a lot of patience to understand.
    private void checkRemovable(int x1, int y1, int x2, int y2) {
        boolean basicCondition = false; //to make basicCondition to be true, the 2 pictures should have the same value in the matrix and should be different pictures.
        if (matrix[x1][y1] == matrix[x2][y2] && !(x1 == x2 && y1 == y2)) {
            basicCondition = true;
        }
        /*Since the user can select pictures in any order, this will cause many troubles if not handled.
        So if x2 < x1 (x represents the row number), this means the 2nd picture is in the upper row than the 1st picture. So I swap the 2 pictures to make sure
        the 2nd picture is always in the lower row than the 1st picture.
        If x1 = x2, this means the 2 pictures are in the same row, then I compare the value of y1 and y2, to make sure
        the 2nd picture is always on the right side of the 1st picture.
         */
        int maxX = x2;
        int maxY = y2;
        if (x2 < x1) {
            maxX = x1;
            maxY = y1;
            x1 = x2;
            y1 = y2;
            x2 = maxX;
            y2 = maxY;
        } else if (x1 == x2) {
            if (y1 > y2) {
                maxX = x1;
                maxY = y1;
                x1 = x2;
                y1 = y2;
                x2 = maxX;
                y2 = maxY;
            }
        }
        //if the 2 pictures are next to each other.
        if (x1 == x2 && (y2 - y1 == 1) && basicCondition) {
            isRemovable = true;
            drawLines(x1, y1, x2, y2);
        } else if (y1 == y2 && (x2 - x1 == 1) && basicCondition) {
            isRemovable = true;
            drawLines(x1, y1, x2, y2);
        }
        //if the 2 pictures are in the same row but not next to each other.
        else if (x1 == x2 && (y2 - y1 > 1) && basicCondition) {
            boolean allSpace1 = false; //allSpace1 is true when the 1 line between the 2 pictures are all space.
            boolean allSpace3Up = false; //allSpace3Up is true when there exists 3 lines that are above the 2 pictures and these 3 lines don't go across other pictures.
            boolean allSpace3Down = false; //allSpace3Down is true when there exists 3 lines that are below the 2 pictures and these 3 lines don't go across other pictures.
            boolean checkVertical = false;
            boolean hasDrawn = false; //there may be more than one possible cases to link 2 pictures, I only need one case.
            //check if allSpace1 is true or not.
            for (int i = y1 + 1; i < y2; i++) {
                if (matrix[x1][i] != -1) {
                    allSpace1 = false;
                    break;
                } else {
                    allSpace1 = true;
                }
            }
            if (allSpace1) {
                drawLines(x1, y1, x2, y2);
                hasDrawn = true; //set hasDrawn to true, so if there is other possible cases, no other lines will be drawn.
            }
            //check if allSpace3Up is true or not.
            for (int xi = 0; xi < x1; xi++) {
                for (int i = xi; i < x1; i++) {
                    if (matrix[i][y1] != -1 || matrix[i][y2] != -1) {
                        allSpace3Up = false;
                        checkVertical = false;
                        break;
                    } else {
                        checkVertical = true;
                    }
                }
                if (checkVertical) {
                    for (int i = y1; i <= y2; i++) {
                        if (matrix[xi][i] != -1) {
                            allSpace3Up = false;
                            break;
                        } else {
                            allSpace3Up = true;
                        }
                    }
                }
                if (allSpace3Up && !hasDrawn) {
                    drawLines(x1, y1, xi, y1);
                    drawLines(xi, y1, xi, y2);
                    drawLines(xi, y2, x2, y2);
                    hasDrawn = true;
                    break;
                }
            }
            //check if allSpace3Down is true or not.
            for (int xi = 11; xi > x1; xi--) {
                for (int i = xi; i > x1; i--) {
                    if (matrix[i][y1] != -1 || matrix[i][y2] != -1) {
                        allSpace3Down = false;
                        checkVertical = false;
                        break;
                    } else {
                        checkVertical = true;
                    }
                }
                if (checkVertical) {
                    for (int i = y1; i <= y2; i++) {
                        if (matrix[xi][i] != -1) {
                            allSpace3Down = false;
                            break;
                        } else {
                            allSpace3Down = true;
                        }
                    }
                }
                if (allSpace3Down && !hasDrawn) {
                    drawLines(x1, y1, xi, y1);
                    drawLines(xi, y1, xi, y2);
                    drawLines(xi, y2, x2, y2);
                    hasDrawn = true;
                    break;
                }
            }
            //if any one of these 3 flags is true, then set isRemovable to true, this means the 2 pictures are removable.
            if (allSpace1) {
                isRemovable = true;
            }
            if (allSpace3Up) {
                isRemovable = true;
            }
            if (allSpace3Down) {
                isRemovable = true;
            }
        }
        //if the 2 pictures are in the same column but not next to each other.
        else if (y1 == y2 && (x2 - x1 > 1) && basicCondition) {
            boolean allSpace1 = false; //allSpace1 is true when the 1 line between the 2 pictures are all space.
            boolean allSpace3Left = false; //allSpace3Left is true when there exists 3 lines that are on the left side of the 2 pictures and these 3 lines don't go across other pictures.
            boolean allSpace3Right = false; //allSpace3Right is true when there exists 3 lines that are on the right side of the 2 pictures and these 3 lines don't go across other pictures.
            boolean checkHorizontal = false;
            boolean hasDrawn = false;
            //check if allSpace1 is true or not.
            for (int i = x1 + 1; i < x2; i++) {
                if (matrix[i][y1] != -1) {
                    allSpace1 = false;
                    break;
                } else {
                    allSpace1 = true;
                }
            }
            if (allSpace1) {
                drawLines(x1, y1, x2, y2);
                hasDrawn = true;
            }
            //check if allSpace3Left is true or not.
            for (int yi = 0; yi < y1; yi++) {
                for (int i = yi; i < y1; i++) {
                    if (matrix[x1][i] != -1 || matrix[x2][i] != -1) {
                        allSpace3Left = false;
                        checkHorizontal = false;
                        break;
                    } else {
                        checkHorizontal = true;
                    }
                }
                if (checkHorizontal) {
                    for (int i = x1; i <= x2; i++) {
                        if (matrix[i][yi] != -1) {
                            allSpace3Left = false;
                            break;
                        } else {
                            allSpace3Left = true;
                        }
                    }
                }
                if (allSpace3Left && !hasDrawn) {
                    drawLines(x1, y1, x1, yi);
                    drawLines(x1, yi, x2, yi);
                    drawLines(x2, yi, x2, y2);
                    hasDrawn = true;
                    break;
                }
            }
            //check if allSpace3Right is true or not.
            for (int yi = 11; yi > y1; yi--) {
                for (int i = yi; i > y1; i--) {
                    if (matrix[x1][i] != -1 || matrix[x2][i] != -1) {
                        allSpace3Right = false;
                        checkHorizontal = false;
                        break;
                    } else {
                        checkHorizontal = true;
                    }
                }
                if (checkHorizontal) {
                    for (int i = x1; i <= x2; i++) {
                        if (matrix[i][yi] != -1) {
                            allSpace3Right = false;
                            break;
                        } else {
                            allSpace3Right = true;
                        }
                    }
                }
                if (allSpace3Right && !hasDrawn) {
                    drawLines(x1, y1, x1, yi);
                    drawLines(x1, yi, x2, yi);
                    drawLines(x2, yi, x2, y2);
                    hasDrawn = true;
                    break;
                }
            }
            //if any one of these 3 flags is true, then set isRemovable to true, this means the 2 pictures are removable.
            if (allSpace1) {
                isRemovable = true;
            }
            if (allSpace3Left) {
                isRemovable = true;
            }
            if (allSpace3Right) {
                isRemovable = true;
            }
        }
        //if the 2 pictures are neither in the same row, nor in the same column.
        else if (x2 != x1 && y1 != y2 && basicCondition) {
            boolean allSpace2DownSide = false; //allSpace2DownSide is true when there exists 2 lines that are on the downer side of the 2 pictures and these 2 lines don't go across other pictures.
            boolean allSpace2UpSide = false; //allSpace2UpSide is true when there exists 2 lines that are on the upper side of the 2 pictures and these 2 lines don't go across other pictures.
            boolean checkVerticalDownSide = false;
            boolean checkVerticalUpSide = false;
            boolean allSpace3Up = false; //allSpace3Up is true when there exists 3 lines that are above the 2 pictures and these 3 lines don't go across other pictures.
            boolean allSpace3Down = false; //allSpace3Down is true when there exists 3 lines that are below the 2 pictures and these 3 lines don't go across other pictures.
            boolean allSpace3Left = false; //allSpace3Left is true when there exists 3 lines that are on the left side of the 2 pictures and these 3 lines don't go across other pictures.
            boolean allSpace3Right = false; //allSpace3Right is true when there exists 3 lines that are on the right side of the 2 pictures and these 3 lines don't go across other pictures.
            boolean allSpace3InsideHorizontal = false; //allSpace3InsideHorizontal is true when there exists 3 lines that are inside the rectangle that 2 pictures form and these 3 lines don't go across other pictures. Horizontal means the 2nd line is horizontal.
            boolean allSpace3InsideVertical = false; //allSpace3InsideVertical is true when there exists 3 lines that are inside the rectangle that 2 pictures form and these 3 lines don't go across other pictures. Vertical means the 2nd line is vertical.
            boolean checkVertical = false;
            boolean checkHorizontal = false;
            boolean hasDrawn = false;
            //check if allSpace2DownSide is true or not.
            for (int i = x1 + 1; i <= x2; i++) {
                if (matrix[i][y1] != -1) {
                    allSpace2DownSide = false;
                    checkVerticalDownSide = false;
                    break;
                } else {
                    checkVerticalDownSide = true;
                }
            }
            if (checkVerticalDownSide) {
                if (y2 > y1) {
                    for (int j = y1; j < y2; j++) {
                        if (matrix[x2][j] != -1) {
                            allSpace2DownSide = false;
                            break;
                        } else {
                            allSpace2DownSide = true;
                        }
                    }
                } else {
                    for (int j = y2 + 1; j <= y1; j++) {
                        if (matrix[x2][j] != -1) {
                            allSpace2DownSide = false;
                            break;
                        } else {
                            allSpace2DownSide = true;
                        }
                    }
                }
            }
            if (allSpace2DownSide && !hasDrawn) {
                drawLines(x1, y1, x2, y1);
                drawLines(x2, y1, x2, y2);
                hasDrawn = true;
            }
            //check if allSpace2UpSide is true or not.
            for (int i = x1; i < x2; i++) {
                if (matrix[i][y2] != -1) {
                    allSpace2UpSide = false;
                    checkVerticalUpSide = false;
                    break;
                } else {
                    checkVerticalUpSide = true;
                }
            }
            if (checkVerticalUpSide) {
                if (y2 > y1) {
                    for (int j = y1 + 1; j <= y2; j++) {
                        if (matrix[x1][j] != -1) {
                            allSpace2UpSide = false;
                            break;
                        } else {
                            allSpace2UpSide = true;
                        }
                    }
                } else {
                    for (int j = y2; j < y1; j++) {
                        if (matrix[x1][j] != -1) {
                            allSpace2UpSide = false;
                            break;
                        } else {
                            allSpace2UpSide = true;
                        }
                    }
                }

            }
            if (allSpace2UpSide && !hasDrawn) {
                drawLines(x1, y1, x1, y2);
                drawLines(x1, y2, x2, y2);
                hasDrawn = true;
            }
            //check if allSpace3Up is true or not.
            for (int xi = 0; xi < x1; xi++) {
                for (int i = xi; i < x1; i++) {
                    if (matrix[i][y1] != -1) {
                        allSpace3Up = false;
                        checkVertical = false;
                        break;
                    } else {
                        checkVertical = true;
                    }
                }
                if (checkVertical) {
                    for (int i = xi; i < x2; i++) {
                        if (matrix[i][y2] != -1) {
                            allSpace3Up = false;
                            checkVertical = false;
                            break;
                        } else {
                            checkVertical = true;
                        }
                    }
                }
                if (checkVertical) {
                    for (int i = min(y1, y2); i <= max(y1, y2); i++) {
                        if (matrix[xi][i] != -1) {
                            allSpace3Up = false;
                            break;
                        } else {
                            allSpace3Up = true;
                        }
                    }
                }
                if (allSpace3Up && !hasDrawn) {
                    drawLines(x1, y1, xi, y1);
                    drawLines(xi, y1, xi, y2);
                    drawLines(xi, y2, x2, y2);
                    hasDrawn = true;
                    break;
                }
            }
            //check if allSpace3Down is true or not.
            for (int xi = 11; xi > x2; xi--) {
                for (int i = xi; i > x2; i--) {
                    if (matrix[i][y2] != -1) {
                        allSpace3Down = false;
                        checkVertical = false;
                        break;
                    } else {
                        checkVertical = true;
                    }
                }
                if (checkVertical) {
                    for (int i = xi; i > x1; i--) {
                        if (matrix[i][y1] != -1) {
                            allSpace3Down = false;
                            checkVertical = false;
                            break;
                        } else {
                            checkVertical = true;
                        }
                    }
                }
                if (checkVertical) {
                    for (int i = min(y1, y2); i <= max(y1, y2); i++) {
                        if (matrix[xi][i] != -1) {
                            allSpace3Down = false;
                            break;
                        } else {
                            allSpace3Down = true;
                        }
                    }
                }
                if (allSpace3Down && !hasDrawn) {
                    drawLines(x1, y1, xi, y1);
                    drawLines(xi, y1, xi, y2);
                    drawLines(xi, y2, x2, y2);
                    hasDrawn = true;
                    break;
                }
            }
            //check if allSpace3Left is true or not.
            for (int yi = 0; yi < min(y1, y2); yi++) {
                for (int i = yi; i < y1; i++) {
                    if (matrix[x1][i] != -1) {
                        allSpace3Left = false;
                        checkHorizontal = false;
                        break;
                    } else {
                        checkHorizontal = true;
                    }
                }
                if (checkHorizontal) {
                    for (int i = yi; i < y2; i++) {
                        if (matrix[x2][i] != -1) {
                            allSpace3Left = false;
                            checkHorizontal = false;
                            break;
                        } else {
                            checkHorizontal = true;
                        }
                    }
                }
                if (checkHorizontal) {
                    for (int i = x1; i <= x2; i++) {
                        if (matrix[i][yi] != -1) {
                            allSpace3Left = false;
                            break;
                        } else {
                            allSpace3Left = true;
                        }
                    }
                }
                if (allSpace3Left && !hasDrawn) {
                    drawLines(x1, y1, x1, yi);
                    drawLines(x1, yi, x2, yi);
                    drawLines(x2, yi, x2, y2);
                    hasDrawn = true;
                    break;
                }
            }
            //check if allSpace3Right is true or not.
            for (int yi = 11; yi > max(y1, y2); yi--) {
                for (int i = yi; i > y1; i--) {
                    if (matrix[x1][i] != -1) {
                        allSpace3Right = false;
                        checkHorizontal = false;
                        break;
                    } else {
                        checkHorizontal = true;
                    }
                }
                if (checkHorizontal) {
                    for (int i = yi; i > y2; i--) {
                        if (matrix[x2][i] != -1) {
                            allSpace3Right = false;
                            checkHorizontal = false;
                            break;
                        } else {
                            checkHorizontal = true;
                        }
                    }
                }
                if (checkHorizontal) {
                    for (int i = x1; i <= x2; i++) {
                        if (matrix[i][yi] != -1) {
                            allSpace3Right = false;
                            break;
                        } else {
                            allSpace3Right = true;
                        }
                    }
                }
                if (allSpace3Right && !hasDrawn) {
                    drawLines(x1, y1, x1, yi);
                    drawLines(x1, yi, x2, yi);
                    drawLines(x2, yi, x2, y2);
                    hasDrawn = true;
                    break;
                }
            }
            //check if allSpace3InsideHorizontal is true or not.
            for (int xi = x1 + 1; xi < x2; xi++) {
                for (int i = x1 + 1; i <= xi; i++) {
                    if (matrix[i][y1] != -1) {
                        allSpace3InsideHorizontal = false;
                        checkVertical = false;
                        break;
                    } else {
                        checkVertical = true;
                    }
                }
                if (checkVertical) {
                    for (int i = xi; i < x2; i++) {
                        if (matrix[i][y2] != -1) {
                            allSpace3InsideHorizontal = false;
                            checkVertical = false;
                            break;
                        } else {
                            checkVertical = true;
                        }
                    }
                }
                if (checkVertical) {
                    for (int i = min(y1, y2); i <= max(y1, y2); i++) {
                        if (matrix[xi][i] != -1) {
                            allSpace3InsideHorizontal = false;
                            break;
                        } else {
                            allSpace3InsideHorizontal = true;
                        }
                    }
                }
                if (allSpace3InsideHorizontal && !hasDrawn) {
                    drawLines(x1, y1, xi, y1);
                    drawLines(xi, y1, xi, y2);
                    drawLines(xi, y2, x2, y2);
                    hasDrawn = true;
                    break;
                }
            }
            if (y1 < y2) {
                for (int yi = y1 + 1; yi < y2; yi++) {
                    for (int i = y1 + 1; i <= yi; i++) {
                        if (matrix[x1][i] != -1) {
                            allSpace3InsideVertical = false;
                            checkHorizontal = false;
                            break;
                        } else {
                            checkHorizontal = true;
                        }
                    }
                    if (checkHorizontal) {
                        for (int i = yi; i < y2; i++) {
                            if (matrix[x2][i] != -1) {
                                allSpace3InsideVertical = false;
                                checkHorizontal = false;
                                break;
                            } else {
                                checkHorizontal = true;
                            }
                        }
                    }
                    if (checkHorizontal) {
                        for (int i = x1; i <= x2; i++) {
                            if (matrix[i][yi] != -1) {
                                allSpace3InsideVertical = false;
                                break;
                            } else {
                                allSpace3InsideVertical = true;
                            }
                        }
                    }
                    if (allSpace3InsideVertical && !hasDrawn) {
                        drawLines(x1, y1, x1, yi);
                        drawLines(x1, yi, x2, yi);
                        drawLines(x2, yi, x2, y2);
                        hasDrawn = true;
                        break;
                    }
                }
            }
            //check if allSpace3InsideVertical is true or not.
            else {
                for (int yi = y2 + 1; yi < y1; yi++) {
                    for (int i = y2 + 1; i <= yi; i++) {
                        if (matrix[x2][i] != -1) {
                            allSpace3InsideVertical = false;
                            checkHorizontal = false;
                            break;
                        } else {
                            checkHorizontal = true;
                        }
                    }
                    if (checkHorizontal) {
                        for (int i = yi; i < y1; i++) {
                            if (matrix[x1][i] != -1) {
                                allSpace3InsideVertical = false;
                                checkHorizontal = false;
                                break;
                            } else {
                                checkHorizontal = true;
                            }
                        }
                    }
                    if (checkHorizontal) {
                        for (int i = x1; i <= x2; i++) {
                            if (matrix[i][yi] != -1) {
                                allSpace3InsideVertical = false;
                                break;
                            } else {
                                allSpace3InsideVertical = true;
                            }
                        }
                    }
                    if (allSpace3InsideVertical && !hasDrawn) {
                        drawLines(x1, y1, x1, yi);
                        drawLines(x1, yi, x2, yi);
                        drawLines(x2, yi, x2, y2);
                        hasDrawn = true;
                        break;
                    }
                }
            }
            //if any one of these flags is true, then set isRemovable to true, this means the 2 pictures are removable.
            if (allSpace2DownSide || allSpace2UpSide || allSpace3Up || allSpace3Down || allSpace3Left || allSpace3Right || allSpace3InsideHorizontal || allSpace3InsideVertical) {
                isRemovable = true;
            }
        }
    }

    //this is a method to return the max value.
    private int max(int x, int y) {
        if (x > y) {
            return x;
        } else {
            return y;
        }
    }

    //this is a method to return the min value.
    private int min(int x, int y) {
        if (x > y) {
            return y;
        } else {
            return x;
        }
    }

    //this is a method to check if the user wins the game.
    private void checkWin() {
        isWinning = true;
        boolean fullLineSpace = true;
        for (int i = 0; i < 12; i++) {
            if (fullLineSpace) {
                for (int j = 0; j < 12; j++) {
                    if (matrix[i][j] != -1) {
                        fullLineSpace = false;
                        break;
                    } else {
                        fullLineSpace = true;
                    }
                }
            } else {
                isWinning = false;
            }
        }
    }

    //this is a method to draw the gameControl part in the game scene. This includes a timerLabel and a button to return to menu.
    private VBox drawGameControl() {
        VBox gameControl = new VBox(5);
        gameControl.setPadding(new Insets(30));
        timerLabel = new Label("0.00 seconds");
        backToMenu = new Button("Back to menu");

        gameControl.getChildren().addAll(timerLabel, backToMenu);
        gameControl.setLayoutX(770);
        return gameControl;
    }

    //this is a method to create a timeline to update the timer label every 10 milliseconds.
    private void startTimer() {
        timelineTimer = new Timeline(new KeyFrame(Duration.millis(10), event -> {
            centiSecondsElapsed++;
            double seconds = centiSecondsElapsed / 100.0;
            timerLabel.setText(String.format("%.2f seconds", seconds));
        }));
        timelineTimer.setCycleCount(Animation.INDEFINITE);
        timelineTimer.play();
    }

    //this is a method to stop the timeline.
    private void stopTimer() {
        if (timelineTimer != null) {
            timelineTimer.stop();
            centiSecondsElapsed = 0;
        }
    }

    //this is a method to read the information in the scores.txt.
    public static ArrayList<String> fileReader() throws IOException {
        ArrayList<String> items = new ArrayList<>();
        File myFile = new File("F:\\Program Files\\java_files\\javaFX\\scores.txt");
        Scanner reader = new Scanner(myFile);
        while (reader.hasNext()) {
            String str = reader.nextLine();
            items.add(str);
        }
        return items;
    }

    //this is a method to write the game result to the scores.txt.
    private void fileWriter(String score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("scores.txt", true))) {
            writer.write(score);
            writer.newLine();
            //System.out.println("Number added: " + score);
        } catch (IOException e) {
            System.err.println("Error!");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
