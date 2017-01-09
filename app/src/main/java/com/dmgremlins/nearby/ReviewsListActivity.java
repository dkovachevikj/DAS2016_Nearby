package com.dmgremlins.nearby;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Nacev on 08.01.2017.
 */

public class ReviewsListActivity extends AppCompatActivity {

    ListView listView;
    Review[] reviews;
    ArrayList<Review> pomosna;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("ReviewsListActivity","Received broadcast");
            pomosna=(ArrayList<Review>)intent.getExtras().get("Reviews");
            reviews = new Review[pomosna.size()];
            for(int i = 0; i < pomosna.size(); i++) {
                //String userName = "John Smith";
                String userName=pomosna.get(i).getUserName();
                //float rating = 2.5f;
                float rating=pomosna.get(i).getRating();
                String desc=pomosna.get(i).getDesc();
                //String desc = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua...";
                reviews[i] = new Review(userName, rating, desc);
            }
            ReviewsListAdapter adapter = new ReviewsListAdapter(getApplicationContext(), reviews);
            listView = (ListView) findViewById(R.id.reviewsListView);
            listView.setAdapter(adapter);
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews_list);
        new TestConnection(this).getReviews();
        Bundle extras=getIntent().getExtras();
       /* if(extras!=null) {
            pomosna = (ArrayList<Review>) extras.get("Reviews");

            //pomosna=new TestConnection(this).getReviews();
            reviews = new Review[pomosna.size()];
            for(int i = 0; i < pomosna.size(); i++) {
                //String userName = "John Smith";
                String userName=pomosna.get(i).getUserName();
                //float rating = 2.5f;
                float rating=pomosna.get(i).getRating();
                String desc=pomosna.get(i).getDesc();
                //String desc = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua...";
                reviews[i] = new Review(userName, rating, desc);
            }
            ReviewsListAdapter adapter = new ReviewsListAdapter(getApplicationContext(), reviews);
            listView = (ListView) findViewById(R.id.reviewsListView);
            listView.setAdapter(adapter);
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("ReviewsFilledWithInformation");
        registerReceiver(broadcastReceiver, filter);
        Log.d("ReviewsListActivity","Broadcast registered");
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        Log.d("ReviewsListActivity","Broadcast unregistered");
    }
}
