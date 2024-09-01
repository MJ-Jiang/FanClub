package org.example.finalproject;



import java.util.ArrayList;

/**
 * Introduction:
 * This class will use the NumbersInRandomOrder class and get an arraylist.
 * Then it will use the numbers in this arraylist and turns it into a matrix.
 * So, first we can get a 10*10 matrix which consists of 100 integers from 0-9, ten for each number.
 * Further, turn this matrix into a 12*12 matrix by surrounding the former matrix with the number -1.
 */

public class MatrixWithRandomNumbers {
    NumbersInRandomOrder numbersInRandomOrder;

    public MatrixWithRandomNumbers() {
        this.numbersInRandomOrder = new NumbersInRandomOrder();
    }

    public int[][] generateMatrix() {
        ArrayList<Integer> integers = numbersInRandomOrder.generateNumbers();
        int[][] index = new int[10][10];
        int[][] matrix = new int[12][12];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                index[i][j] = integers.get(i * 10 + j);
                //System.out.print(index[i][j] + " ");
            }
            //System.out.println();
        }
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                matrix[i][j] = -1;
            }
        }
        for (int i = 1; i < 11; i++) {
            for (int j = 1; j < 11; j++) {
                matrix[i][j] = index[i - 1][j - 1];
            }
        }
        /*for(int i = 0; i < 12; i++){
            for(int j = 0; j < 12; j++){
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }*/
        return matrix;
    }
    /*public static void main(String[] args) {
        MatrixWithRandomNumbers matrixWithRandomNumbers = new MatrixWithRandomNumbers();
        int[][] matrix = matrixWithRandomNumbers.generateMatrix();
    }*/
}
