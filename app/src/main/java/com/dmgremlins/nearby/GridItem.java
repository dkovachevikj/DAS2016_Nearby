package com.dmgremlins.nearby;

/*
    this class contains the name of a location category
    and an associated image
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