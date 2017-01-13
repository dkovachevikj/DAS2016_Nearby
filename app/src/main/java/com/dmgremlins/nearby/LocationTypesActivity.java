package com.dmgremlins.nearby;

import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.dmgremlins.nearby.command_pattern.GetLocations;

import java.util.ArrayList;

public class LocationTypesActivity extends AppCompatActivity {

    //references the grid that contains the GridItem elements
    GridView grid;
    //ArrayList containing GridItem elements
    private ArrayList<GridItem> items;
    //reference to EventHandler
    private EventHandler eventHandler;
    //reference to GetLocations subclass
    private GetLocations getLocations;

    /*
        since for this version of the app we are only using a set amount of
        location categories, we've placed the names and images in arrays
        consisting of 12 elements
     */
    private static String[] locationNames = {"Bank", "Cafe", "Restaurant", "Hotels", "Bar", "Store", "Night_Club", "ATM", "Bakery", "Hospital", "Lawyer", "Gas_station"};
    private static int[] locationImages = {R.mipmap.bank_img, R.mipmap.caffee_img, R.mipmap.restaurant_img, R.mipmap.hotels_img,
            R.mipmap.cocktail_bar_img, R.mipmap.market_store_img, R.mipmap.night_club_img, R.mipmap.atm_img, R.mipmap.bakery_img, R.mipmap.hospital_img, R.mipmap.lawyer_img, R.mipmap.gas_station_img};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //fills the grid with GridItem objects that contain the categories's names and images
        grid = (GridView) findViewById(R.id.gridView);
        items = new ArrayList<GridItem>();
        for(int i=0; i<12; i++) {
            GridItem tempItem = new GridItem(locationImages[i], locationNames[i]);
            items.add(tempItem);
        }
        grid.setAdapter(new GridViewAdapter(this, R.layout.activity_grid_item_layout, items));

        /*
            gets the one and only instance from EventHandler,
            sets itself as the current activity
            and calls the EventHandler method that initiates
            requests for location updates
         */
        eventHandler = EventHandler.getInstance();
        eventHandler.setActivity(LocationTypesActivity.this);
        //eventHandler.setLocationUpdates();

        setGridItemListener();

    }

    //listens for a tap on an grid item, then sends the location type to EventHandler (using command subclass)
    private void setGridItemListener() {
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GridItem item = (GridItem) parent.getItemAtPosition(position);
                //Toast.makeText(LocationTypesActivity.this, "Longitude: " + longitude + ", Latitude: " + latitude, Toast.LENGTH_SHORT).show();
                String type = item.getTitle().toLowerCase();
                getLocations = new GetLocations(eventHandler, type);
                getLocations.execute();
            }
        });
    }

    /*
        since EventHandler doesn't extend any type
        of Activity class it cannot override this method which is needed for a part
        of the logic behind the location updates requests, hence this method is in
        this class rather than in EventHandler
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                eventHandler.requestLocationUpdates();
                break;
            default:
                break;
        }
    }
}

