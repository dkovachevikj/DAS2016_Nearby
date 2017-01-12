package com.dmgremlins.nearby;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import javax.xml.datatype.Duration;

/**
 * Created by User on 12/11/2016.
 */

public class WriteReviewActivity extends AppCompatActivity{

    private EditText reviewText;
    private Button sendReviewButton;
    private String placeId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        placeId = getIntent().getExtras().getString("id");

        reviewText = (EditText) findViewById(R.id.reviewEditText);
        sendReviewButton = (Button) findViewById(R.id.sendReviewButton);

        setReviewTextTextChangedListener();
        setSendReviewButtonListener();
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

    private void setSendReviewButtonListener() {
        sendReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
