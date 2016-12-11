package com.dmgremlins.nearby;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dmgremlins.nearby.POJO.Result;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AddPlaceRequest;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlacesOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class PlaceDetailsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "PlaceDetailsActivity";

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private Place place;
    PendingResult<PlaceBuffer> placeResult;
    private String placeId;

    Button reviewsButton;
    Button getDirectionsButton;
    Button writeReviewButton;
    LatLng placeLatLng;
    String placeName;

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

        placeId = getIntent().getExtras().getString("id");
        buildGoogleApiClient();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(PlaceBuffer places) {
                Log.i(TAG, "Testing");
                if (places.getStatus().isSuccess() && places.getCount() > 0) {
                    place = places.get(0);
                    Log.i(TAG, "Place found: " + place.getName());
                    placeLatLng = place.getLatLng();
                    placeName = place.getName().toString();
                    setFields();
                    updateMap();
                } else {
                    Log.e(TAG, "Place not found");
                }
                places.release();
            }
        });

    }

    private void setWriteReviewButtonListener() {
        writeReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaceDetailsActivity.this, WriteReviewActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setGetDirectionsButtonListener() {
        getDirectionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(place != null) {
                    Intent intent = new Intent(PlaceDetailsActivity.this, GetDirectionsActivity.class);
                    intent.putExtra("latlng", placeLatLng);
                    intent.putExtra("name", placeName);
                    startActivity(intent);
                }
            }
        });
    }

    private void setReviewsButtonListener() {
        reviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaceDetailsActivity.this, ReviewsListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setFields() {
        if (place != null) {
            TextView title = (TextView) findViewById(R.id.placeDetailsTitle);
            TextView address = (TextView) findViewById(R.id.placeDetailsAddress);
            TextView phone = (TextView) findViewById(R.id.placeDetailsPhone);
            TextView website = (TextView) findViewById(R.id.placeDetailsWebsite);
            RatingBar ratingBar = (RatingBar) findViewById(R.id.detailsRatingBar);
            if (place.getName() != null) {
                title.setText(place.getName());
            } else {
                title.setText("n/a");
            }
            if (place.getAddress() != null && !(place.getAddress().toString().compareTo("") == 0)) {
                address.setText(place.getAddress());
            } else {
                address.setText("n/a");
            }
            if (place.getPhoneNumber() != null && !(place.getPhoneNumber().toString().compareTo("") == 0)) {
                phone.setText(place.getPhoneNumber());
            } else {
                phone.setText("n/a");
            }
            if (place.getWebsiteUri() != null && !(place.getWebsiteUri().toString().compareTo("") == 0)) {
                website.setText(place.getWebsiteUri().toString());
            } else {
                website.setText("n/a");
            }
            ratingBar.setRating(place.getRating());
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(Places.GEO_DATA_API)
                .build();
        mGoogleApiClient.connect();
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
    }

    private void updateMap() {
        if (place != null) {
            LatLng placeLatLng = place.getLatLng();
            mMap.addMarker(new MarkerOptions().position(placeLatLng).title(place.getName().toString()));
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

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
