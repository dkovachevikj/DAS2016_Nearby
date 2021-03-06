package com.dmgremlins.nearby;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class GetDirectionsActivity extends FragmentActivity implements OnMapReadyCallback {

    //fields referencing to interface elements
    private GoogleMap mMap;
    private String text;
    /*
        polyline that will be referencing the polyline
        that goes from the current user location to
        the place coordinates, once it's available
     */
    private PolylineOptions polylineOptions;
    /*
        listens for a broadcasted intent that will
        contain the polyline and sets the map
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().compareTo("Polyline") == 0) {
                text = intent.getExtras().getString("text");
                polylineOptions = (PolylineOptions) intent.getExtras().get("polyline");
                mMap.addPolyline(polylineOptions.color(Color.RED).width(5));
                mMap.addMarker(new MarkerOptions().position((LatLng) getIntent().getExtras().get("userLatLng")).title("Me"));
                mMap.addMarker(new MarkerOptions().position((LatLng) getIntent().getExtras().get("placeLatLng")).title(getIntent().getExtras().getString("name")));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((LatLng) getIntent().getExtras().get("userLatLng"), 15f));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_directions);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setFilter();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        Log.d("GetDirectionsActivity","Broadcast unregistered");
    }

    //sets up the filter needed for the BroadcastReceiver object
    private void setFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("Polyline");
        registerReceiver(broadcastReceiver, filter);
        Log.d("GetDirectionsActivity","Broadcast registered");
    }

}
