package com.dmgremlins.nearby;

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

public class WriteReviewActivity extends AppCompatActivity{

    /*
        these reference elements from the interface
        such as a text box (EditText) or a rating bar
     */
    private EditText reviewText;
    private String placeId;
    private EditText usernameText;
    private RatingBar rating;
    private Button sendReview;
    private EventHandler eventHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        placeId = getIntent().getExtras().getString("id");

        reviewText = (EditText) findViewById(R.id.reviewEditText);
        usernameText = (EditText) findViewById(R.id.usernameEditText);
        rating= (RatingBar) findViewById(R.id.ratingBar2);
        sendReview=(Button) findViewById(R.id.sendReviewButton);

        setReviewTextTextChangedListener();
        setSendReviewClickListener();

    }

    /*
        sets a listener for a tap on the Send button
        then sends the values from the text boxes and ratings bar
        to EventHandler which will send them to the database through DBAccessPoint
     */
    private void setSendReviewClickListener() {
        sendReview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                eventHandler = EventHandler.getInstance();
                eventHandler.setActivity(WriteReviewActivity.this);
                eventHandler.updateDB(usernameText.getText().toString(),rating.getRating(),reviewText.getText().toString(),placeId);

            }
        });
    }

    /*
        checks if the review text gets over the limit
        of 100 characters, if so it informs the user
        (the limit is set in the .xml file code)
     */
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
