package com.dmgremlins.nearby;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

/**
 * Created by User on 12/11/2016.
 */

public class WriteReviewActivity extends AppCompatActivity{

    private EditText reviewText;
    private String placeId;
    private EditText usernameText;
    private RatingBar rating;
    private Button sendReview;
    private int reviewWordCount;
    private Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);
        activity=this;

        placeId = getIntent().getExtras().getString("id");

        reviewText = (EditText) findViewById(R.id.reviewEditText);
        usernameText = (EditText) findViewById(R.id.usernameEditText);
        rating= (RatingBar) findViewById(R.id.ratingBar2);
        sendReview=(Button) findViewById(R.id.sendReviewButton);
        setReviewTextTextChangedListener();

        sendReview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                new DBAccessPoint(activity).insertReview(usernameText.getText().toString(),rating.getRating(),reviewText.getText().toString(),placeId);
                Toast.makeText(WriteReviewActivity.this, "REVIEW SAVED!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setReviewTextTextChangedListener() {
        reviewText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() >= 100) {
                    Toast.makeText(WriteReviewActivity.this, "You have reached the limit", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
