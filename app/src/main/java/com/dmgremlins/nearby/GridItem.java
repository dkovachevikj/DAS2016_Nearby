package com.dmgremlins.nearby;

/**
 * Created by User on 11/27/2016.
 */

public class GridItem {
    private int image;
    private String title;

    public GridItem(int image, String title) {
        this.image = image;
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

}