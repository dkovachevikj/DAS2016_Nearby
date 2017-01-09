package com.dmgremlins.nearby;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.dmgremlins.nearby.POJO.Example;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.event.Event;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by User on 1/9/2017.
 */

public class EventHandler implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int LOCATIONS_TYPE = 1;
    private static final int PLACES_TYPE = 2;
    private static EventHandler eventHandler;
    private static Activity activity;
    private GoogleApiClient mGoogleApiClient;
    private ConnectionDetector cd;
    private LocationManager lm;
    private android.location.LocationListener listener;
    private double latitude;
    private double longitude;
    private int LOCATION_REQUEST_CODE = 10;
    private int PROXIMITY_RADIUS = 5000;
    private final String TAG = "EventHandler";

    public static EventHandler getInstance() {
        if(eventHandler == null) {
            eventHandler = new EventHandler();
        }
        return eventHandler;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    private void setUpConnection() {
        cd = new ConnectionDetector(activity);
        lm = (LocationManager) activity.getSystemService(LOCATION_SERVICE);

        listener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
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
                activity.startActivity(intent);
            }
        };

        requestUserLocation();
    }

    private void requestUserLocation() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
                }, LOCATION_REQUEST_CODE);
            }
            return;
        }
        lm.requestLocationUpdates("gps", 500, 0, listener);
    }

    protected synchronized void buildGoogleApiClient(int type) {
        if(type == LOCATIONS_TYPE) {
            mGoogleApiClient = new GoogleApiClient.Builder(activity)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        } else {
            mGoogleApiClient = new GoogleApiClient.Builder(activity)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Places.PLACE_DETECTION_API)
                    .addApi(Places.GEO_DATA_API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    public void getLocations(String type) {
        buildGoogleApiClient(LOCATIONS_TYPE);
        String url = "https://maps.googleapis.com/maps/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitMaps service = retrofit.create(RetrofitMaps.class);
        Call<Example> call = service.getNearbyPlaces(type, 42.004408 + "," + 21.409509, PROXIMITY_RADIUS);
        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Response<Example> response, Retrofit retrofit) {

                try {
                    // This loop will go through all the results and add marker on each location.
                    Intent intent = new Intent(activity, LocationsListActivity.class);
                    int j = 0;
                    // This loop will go through all the results and add marker on each location.
                    for (int i = 0; i < response.body().getResults().size(); i++) {
                        intent.putExtra(String.valueOf(j), response.body().getResults().get(i).getName().toString());
                        intent.putExtra(String.valueOf(j + 1), response.body().getResults().get(i).getPlaceId().toString());
                        j += 2;
                    }
                    activity.startActivity(intent);
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });
        if(mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    public void getPlaceDetails(String placeId) {
        buildGoogleApiClient(PLACES_TYPE);
        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(PlaceBuffer places) {
                Log.i(TAG, "Testing");
                if (places.getStatus().isSuccess() && places.getCount() > 0) {
                    Place place = places.get(0);
                    Log.i(TAG, "Place found: " + place.getName());
                    String placeName;
                    String address;
                    String website;
                    String phoneNumber;
                    Intent intent = new Intent(activity, PlaceDetailsActivity.class);
                    if (place.getName() == null || place.getName().toString().compareTo("") == 0) {
                        placeName = "n/a";
                    } else {
                        placeName = place.getName().toString();
                    }
                    if(place.getAddress() == null || place.getAddress().toString().compareTo("") == 0) {
                        address = "n/a";
                    } else {
                        address = place.getAddress().toString();
                    }
                    if(place.getWebsiteUri() == null || place.getWebsiteUri().toString().compareTo("") == 0) {
                        website = "n/a";
                    } else {
                        website = place.getWebsiteUri().toString();
                    }
                    if(place.getPhoneNumber() == null || place.getPhoneNumber().toString().compareTo("") == 0) {
                        phoneNumber = "n/a";
                    } else {
                        phoneNumber = place.getPhoneNumber().toString();
                    }
                    intent.putExtra("latLng", place.getLatLng());
                    intent.putExtra("name", placeName);
                    intent.putExtra("address", address);
                    intent.putExtra("website", website);
                    intent.putExtra("phoneNumber", phoneNumber);
                    intent.putExtra("rating", place.getRating());
                    activity.startActivity(intent);
                } else {
                    Log.e(TAG, "Place not found");
                }
                places.release();
                if(mGoogleApiClient != null) {
                    mGoogleApiClient.disconnect();
                }
            }
        });
    }

    public void getReviews(String id) {
        Intent intent = new Intent(activity, ReviewsListActivity.class);
        activity.startActivity(intent);
    }

    public void addReview() {
        Intent intent = new Intent(activity, WriteReviewActivity.class);
        activity.startActivity(intent);
    }

    public void getDirections(LatLng latLng, String name) {
        Intent intent = new Intent(activity, GetDirectionsActivity.class);
        intent.putExtra("latlng", latLng);
        intent.putExtra("name", name);
        activity.startActivity(intent);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
