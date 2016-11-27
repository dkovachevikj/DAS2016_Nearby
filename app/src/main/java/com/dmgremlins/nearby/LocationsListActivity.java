package com.dmgremlins.nearby;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionApi;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by User on 11/27/2016.
 */


public class LocationsListActivity extends AppCompatActivity {

    ListView locationsListView;
    private HashMap<String, String> locations;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations_list);
        locations = new HashMap<>();
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            for(int i=0; i<extras.size(); i+=2) {
                locations.put(extras.getString(String.valueOf(i)), extras.getString(String.valueOf(i + 1)));
            }
            List<String> locationsList = new ArrayList<>();
            locationsList.addAll(locations.keySet());
            String[] locationsArray = new String[locations.keySet().size()];
            for (int i=0; i<locationsArray.length; i++) {
                locationsArray[i] = locationsList.get(i);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.activity_locations_list, R.id.textView, locationsArray);
            locationsListView = (ListView) findViewById(R.id.locations_list);
            locationsListView.setAdapter(adapter);

            setListClickListener();
         }
        }

    private void setListClickListener() {
        locationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = (String) parent.getItemAtPosition(position);
                String idString = locations.get(title);
                Intent intent = new Intent(LocationsListActivity.this, PlaceDetailsActivity.class);
                intent.putExtra("id", idString);
                startActivity(intent);
            }
        });
    }


}
