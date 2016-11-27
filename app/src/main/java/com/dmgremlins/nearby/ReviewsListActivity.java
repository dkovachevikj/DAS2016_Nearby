package com.dmgremlins.nearby;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by User on 11/27/2016.
 */

public class ReviewsListActivity extends AppCompatActivity {

    ListView listView;
    Review[] reviews;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews_list);

        reviews = new Review[5];

        for(int i = 0; i < 5; i++) {
            String userName = "John Smith";
            float rating = 2.5f;
            String desc = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua...";
            reviews[i] = new Review(userName, rating, desc);
        }
        ReviewsListAdapter adapter = new ReviewsListAdapter(this, reviews);
        listView = (ListView) findViewById(R.id.reviewsListView);
        listView.setAdapter(adapter);
    }
}
