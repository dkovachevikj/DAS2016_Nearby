package com.dmgremlins.nearby;

import java.io.Serializable;

/*
    Review contains the elements of a review for a
    specific location that is put in the database
 */
public class Review implements Serializable{

    private String userName;
    private float rating;
    private String desc;

    public Review(String userName, float rating, String desc) {
        this.userName = userName;
        this.rating = rating;
        this.desc = desc;
    }

    public String getUserName() {
        return userName;
    }

    public float getRating() {
        return rating;
    }

    public String getDesc() {
        return desc;
    }
}
