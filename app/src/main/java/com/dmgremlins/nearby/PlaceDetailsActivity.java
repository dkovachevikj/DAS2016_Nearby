package com.dmgremlins.nearby;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dmgremlins.nearby.command_pattern.AddReview;
import com.dmgremlins.nearby.command_pattern.GetDirections;
import com.dmgremlins.nearby.command_pattern.GetReviews;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PlaceDetailsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "PlaceDetailsActivity";

    private GoogleMap mMap;

    //buttons from interface
    private Button reviewsButton;
    private Button getDirectionsButton;
    private Button writeReviewButton;
    private LatLng placeLatLng;

    //reference to EventHandler and command subclasses
    private EventHandler eventHandler;
    private AddReview addReview;
    private GetDirections getDirections;
    private GetReviews getReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        reviewsButton = (Button) findViewById(R.id.detailsReviewsButton);
        getDirectionsButton = (Button) findViewById(R.id.detailsGetDirections);
        writeReviewButton = (Button) findViewById(R.id.writeReviewButton);
        setReviewsButtonListener();
        setGetDirectionsButtonListener();
        setWriteReviewButtonListener();

        eventHandler = EventHandler.getInstance();
        eventHandler.setActivity(PlaceDetailsActivity.this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setFields();
        placeLatLng = (LatLng) getIntent().getExtras().get("latLng");
    }

    //listens for tap on Write review button then calls EventHandler method through command subclass
    private void setWriteReviewButtonListener() {
        writeReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReview = new AddReview(eventHandler);
                addReview.execute();
            }
        });
    }

    //listens for tap on Get directions button then calls EventHandler method through command subclass
    private void setGetDirectionsButtonListener() {
        getDirectionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(placeLatLng != null) {
                    getDirections = new GetDirections(eventHandler, (LatLng) getIntent().getExtras().get("latLng"), getIntent().getExtras().getString("name"));
                    getDirections.execute();
                }
            }
        });
    }

    //listens for tap on Get reviews button then calls EventHandler method through command subclass
    private void setReviewsButtonListener() {
        reviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReviews = new GetReviews(eventHandler);
                getReviews.execute();
            }
        });
    }

    //sets fields from interface that show name, address, rating etc.
    private void setFields() {
        TextView title = (TextView) findViewById(R.id.placeDetailsTitle);
        TextView address = (TextView) findViewById(R.id.placeDetailsAddress);
        TextView phone = (TextView) findViewById(R.id.placeDetailsPhone);
        TextView website = (TextView) findViewById(R.id.placeDetailsWebsite);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.detailsRatingBar);
        title.setText(getIntent().getExtras().getString("name"));
        address.setText(getIntent().getExtras().getString("address"));
        phone.setText(getIntent().getExtras().getString("phoneNumber"));
        website.setText(getIntent().getExtras().getString("website"));
        ratingBar.setRating(getIntent().getExtras().getFloat("rating"));
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
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        updateMap();
    }

    //updates the map with the place location (puts marker)
    private void updateMap() {
            mMap.addMarker(new MarkerOptions().position(placeLatLng).title(""));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 14.0f));
            mMap.getMaxZoomLevel();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("PlaceDetails Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }
}
