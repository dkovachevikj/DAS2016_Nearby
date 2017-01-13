package com.dmgremlins.nearby;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;


/*
    an adapter that takes care of filling the list that
    will contain the reviews for a specific location with those reviews
 */
public class ReviewsListAdapter extends ArrayAdapter<Review> {

    public ReviewsListAdapter(Context context, Review[] reviews) {
        super(context, R.layout.review_list_row, reviews);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.review_list_row, parent, false);

        TextView userName = (TextView) customView.findViewById(R.id.reviewUserName);
        TextView desc = (TextView) customView.findViewById(R.id.reviewDesc);
        RatingBar ratingBar = (RatingBar) customView.findViewById(R.id.reviewRatingBar);

        Review review = getItem(position);
        userName.setText(review.getUserName());
        desc.setText(review.getDesc());
        ratingBar.setRating(review.getRating());

        return customView;

    }
}
