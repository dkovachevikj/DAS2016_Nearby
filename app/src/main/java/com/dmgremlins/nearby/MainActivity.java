package com.dmgremlins.nearby;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.dmgremlins.nearby.POJO.Example;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.event.Event;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity {

    GridView grid;
    private ArrayList<GridItem> items;
    private EventHandler eventHandler;

    private static String[] locationNames = {"Bank", "Cafe", "Restaurant", "Hotels", "Bar", "Store", "Night_Club", "ATM", "Bakery", "Hospital", "Lawyer", "Gas_station"};
    private static int[] locationImages = {R.mipmap.bank_img, R.mipmap.caffee_img, R.mipmap.restaurant_img, R.mipmap.hotels_img,
            R.mipmap.cocktail_bar_img, R.mipmap.market_store_img, R.mipmap.night_club_img, R.mipmap.atm_img, R.mipmap.bakery_img, R.mipmap.hospital_img, R.mipmap.lawyer_img, R.mipmap.gas_station_img};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        grid = (GridView) findViewById(R.id.gridView);
        items = new ArrayList<GridItem>();
        for(int i=0; i<12; i++) {
            GridItem tempItem = new GridItem(locationImages[i], locationNames[i]);
            items.add(tempItem);
        }
        grid.setAdapter(new GridViewAdapter(this, R.layout.activity_grid_item_layout, items));

        eventHandler = EventHandler.getInstance();
        eventHandler.setActivity(MainActivity.this);

        setGridItemListener();

    }

    private void setGridItemListener() {
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GridItem item = (GridItem) parent.getItemAtPosition(position);
                //Toast.makeText(MainActivity.this, "Longitude: " + longitude + ", Latitude: " + latitude, Toast.LENGTH_SHORT).show();
                String type = item.getTitle().toLowerCase();
                eventHandler.getLocations(type);
            }
        });
    }

}

