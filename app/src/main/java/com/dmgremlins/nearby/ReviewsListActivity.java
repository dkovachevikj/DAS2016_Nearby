package com.dmgremlins.nearby;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

public class ReviewsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews_list);

        Review[] reviews = (Review[]) getIntent().getExtras().get("reviews");
        ReviewsListAdapter adapter = new ReviewsListAdapter(getApplicationContext(), reviews);
        ListView listView = (ListView) findViewById(R.id.reviewsListView);
        listView.setAdapter(adapter);
    }
}
