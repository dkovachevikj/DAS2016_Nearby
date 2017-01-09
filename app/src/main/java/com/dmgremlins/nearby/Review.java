package com.dmgremlins.nearby;

import java.io.Serializable;

/**
 * Created by User on 11/28/2016.
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
