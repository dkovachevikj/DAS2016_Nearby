package com.dmgremlins.nearby;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Random;


/*
    takes care of filling the list that will contain
    locations from a specific category with those locations
 */
public class LocationsListAdapter extends ArrayAdapter<String> {

    Random random;

    public LocationsListAdapter(Context context, String[] titles) {
        super(context, R.layout.list_row, titles);
        random = new Random();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.list_row, parent, false);

        String titleString = getItem(position);

        TextView title = (TextView) customView.findViewById(R.id.listLocationTitle);
        TextView distance = (TextView) customView.findViewById(R.id.listLocationDistance);

        title.setText(titleString);
        distance.setText(String.format("%.1f km", (random.nextInt(10000) + 100) / (float) 1000));

        return customView;
     }
}
