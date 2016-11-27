package com.dmgremlins.nearby;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by User on 11/27/2016.
 */

public class CustomLocation {

    private TextView title;
    private TextView distance;

    public CustomLocation(String title) {

    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }


    public TextView getDistance() {
        return distance;
    }

    public void setDistance(TextView distance) {
        this.distance = distance;
    }
}
