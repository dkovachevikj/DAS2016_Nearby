package com.testdomain.connectiontest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    LocationManager lm;
    LocationListener listener;
    ConnectionDetector cd;
    int LOCATION_REQUEST_CODE = 10;

    GridView grid;
    private ArrayList<GridItem> items;

    private static String[] locationNames = {"Bank", "Coffee", "Restaurants", "Hotels", "Cocktail bars", "Exchange office", "Market store", "Night Club"};
    private static int[] locationImages = {R.mipmap.bank_img, R.mipmap.caffee_img, R.mipmap.restaurant_img, R.mipmap.hotels_img, R.mipmap.cocktail_bar_img, R.mipmap.exchange_office_img, R.mipmap.market_store_img, R.mipmap.night_club_img};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        grid = (GridView) findViewById(R.id.gridView);
        items = new ArrayList<GridItem>();
        for(int i=0; i<8; i++) {
            GridItem tempItem = new GridItem(locationImages[i], locationNames[i]);
            items.add(tempItem);
        }
        grid.setAdapter(new GridViewAdapter(this, R.layout.activity_grid_item_layout, items));

        cd = new ConnectionDetector(this);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        setGridItemListener();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == LOCATION_REQUEST_CODE) {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    configureButton();
                }
            }
    }

    private void configureButton() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
                }, LOCATION_REQUEST_CODE);
            }
            return;
        }
        lm.requestLocationUpdates("gps", 5000, 5, listener);
    }

    private boolean checkConnection() {
        if(cd.isConnected()) {
            return true;
        }
        return false;
    }

    private void setGridItemListener() {
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GridItem item = (GridItem) parent.getItemAtPosition(position);
                if(checkConnection()) {
                    Toast.makeText(MainActivity.this, "You selected " + item.getTitle(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Not connected! You selected " + item.getTitle(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
