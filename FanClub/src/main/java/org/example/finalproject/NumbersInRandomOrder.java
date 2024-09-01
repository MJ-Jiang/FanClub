package org.example.finalproject;


import java.util.ArrayList;
import java.util.Collections;

/**
 * Introduction:
 * This class generates a randomly arranged arraylist that contains several integers, each integer appears the same number of times that is pre-defined.
 * For example, if not specially assign parameter value to the generateValue() method, this method will generate an arraylist that consists of 100 integers from 0-9, ten for each number.
 */

public class NumbersInRandomOrder {
    public int numberRange;
    public int occurrenceTimes;
    ArrayList<Integer> integers = new ArrayList<>();

    public NumbersInRandomOrder() {
        this.numberRange = 10;
        this.occurrenceTimes = 10;
    }

    public NumbersInRandomOrder(int numberRange, int occurrenceTimes) {
        this.numberRange = numberRange;
        this.occurrenceTimes = occurrenceTimes;
    }

    public int getNumberRange() {
        return numberRange;
    }

    public int getOccurrenceTimes() {
        return occurrenceTimes;
    }

    public ArrayList<Integer> generateNumbers() {
        for (int i = 0; i < numberRange; i++) {
            for (int j = 0; j < occurrenceTimes; j++) {
                integers.add(i);
            }
        }
        Collections.shuffle(integers);
        return integers;
    }
}
