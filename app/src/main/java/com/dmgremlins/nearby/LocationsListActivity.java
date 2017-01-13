package com.dmgremlins.nearby;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dmgremlins.nearby.command_pattern.GetPlaceDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class LocationsListActivity extends AppCompatActivity {

    //contains reference to the ListView object in the .xml file
    ListView locationsListView;
    /*
        locations HashMap contains the locations's name and id
        so when the user selects a location it's id can be quickly acquired and
        sent to the EventHandler
     */
    private HashMap<String, String> locations;
    //reference to EventHandler
    private EventHandler eventHandler;
    //refernce to GetPlaceDetails command subclass
    private GetPlaceDetails getPlaceDetails;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations_list);
        eventHandler = EventHandler.getInstance();
        eventHandler.setActivity(LocationsListActivity.this);
        setUpLocationsList();
    }

    //sets up the locations list, gets the locations's name and id from the intent extras
    private void setUpLocationsList() {
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
            ArrayAdapter<String> adapter = new LocationsListAdapter(this, locationsArray);
            locationsListView = (ListView) findViewById(R.id.locations_list);
            locationsListView.setAdapter(adapter);

            setListClickListener();
        }
    }

    /*
        listens for a tap on the locations list
        and sends the location id to the EventHandler (through command subclass
     */
    private void setListClickListener() {
        locationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = (String) parent.getItemAtPosition(position);
                String idString = locations.get(title);
                getPlaceDetails = new GetPlaceDetails(eventHandler, idString);
                getPlaceDetails.execute();
                /** Intent intent = new Intent(LocationsListActivity.this, PlaceDetailsActivity.class);
                intent.putExtra("id", idString);
                startActivity(intent); **/
                 }
        });
    }


}
