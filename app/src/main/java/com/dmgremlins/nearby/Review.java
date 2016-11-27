package com.dmgremlins.nearby;

/**
 * Created by User on 11/28/2016.
 */

public class Review {

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
