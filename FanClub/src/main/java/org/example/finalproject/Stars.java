package org.example.finalproject;
import java.time.LocalDate;

/**
 * Represents information about a star.
 */

public class Stars {
    /**
     * The name of the star.
     */
    private String name;
    /**
     * The birthday of the star.
     */
    private LocalDate birthday;
    /**
     * The hometown of the star.
     */
    private String hometown;
    /**
     * The gender of the star.
     */
    private Gender gender;
    /**
     * The favorite color of the star.
     */
    private String color;
    /**
     * Enumeration representing gender.
     */
    public enum Gender {
        Female, Male, Others
    }

    /**
     * Constructs a Stars object with default values.
     */
    public Stars(){
    }
    /**
     * Constructs a Stars object with the specified attributes.
     *
     * @param name     The name of the star.
     * @param birthday The birthday of the star.
     * @param hometown The hometown of the star.
     * @param gender   The gender of the star.
     * @param color    The favorite color of the star.
     */
    public Stars(String name, LocalDate birthday, String hometown, Gender gender, String color){
        this.name = name;
        this.birthday = birthday;
        this.hometown = hometown;
        this.gender = gender;
        this.color = color;
    }
    /**
     * Sets the name of the star.
     *
     * @param name The name of the star.
     */
    public void setName(String name){
        this.name = name;
    }
    /**
     * Retrieves the name of the star.
     *
     * @return The name of the star.
     */
    public String getName(){
        return name;
    }
    /**
     * Sets the birthday of the star.
     *
     * @param birthday The birthday of the star.
     */
    public void setDate(LocalDate birthday){
        this.birthday = birthday;
    }
    /**
     * Retrieves the birthday of the star.
     *
     * @return The birthday of the star.
     */
    public LocalDate getDate(){
        return birthday;
    }
    /**
     * Sets the hometown of the star.
     *
     * @param hometown The hometown of the star.
     */
    public void setHometown(String hometown){
        this.hometown = hometown;
    }
    /**
     * Retrieves the hometown of the star.
     *
     * @return The hometown of the star.
     */
    public String getHometown(){
        return hometown;
    }
    /**
     * Sets the gender of the star.
     *
     * @param gender The gender of the star.
     */
    public void setGender(Gender gender){
        this.gender = gender;
    }
    /**
     * Retrieves the gender of the star.
     *
     * @return The gender of the star.
     */
    public Gender getGender(){
        return gender;
    }
    /**
     * Sets the favorite color of the star.
     *
     * @param color The favorite color of the star.
     */
    public void setColor(String color){
        this.color = color;
    }
    /**
     * Retrieves the favorite color of the star.
     *
     * @return The favorite color of the star.
     */
    public String getColor(){
        return color;
    }

}
