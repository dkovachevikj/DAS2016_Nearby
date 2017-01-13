package com.dmgremlins.nearby;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.dmgremlins.nearby.POJO.Example;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static android.content.Context.LOCATION_SERVICE;


public class EventHandler implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    //necessary variables used in methods
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
    private String currentPlaceID;

    //get an instance of EventHandler
    //singleton pattern
    public static EventHandler getInstance() {
        if(eventHandler == null) {
            eventHandler = new EventHandler();
        }
        return eventHandler;
    }

    /* sets the current Activity (category view, locations, place details etc.
     * necessary for some operations that require an application context
     * and because EventHandler doesn't extend Activity or any subclasses of Activity
     */
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    //sets up code for location updates
    public void setLocationUpdates() {
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
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(i);
            }
        };
        requestLocationUpdates();
    }

    //requests location updates every 5 seconds
    public void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }
        lm.requestLocationUpdates("gps", 5000, 0, listener);

    }

    /* builds two types of GoogleApiClient
     * one for locations search
     * other for place search
     */
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

    // used to get locations from a specific category (restaurants, banks, bars etc.)
    public void getLocations(String type) {
        buildGoogleApiClient(LOCATIONS_TYPE);
        String url = "https://maps.googleapis.com/maps/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitMaps service = retrofit.create(RetrofitMaps.class);
        Call<Example> call = service.getNearbyPlaces(type, latitude + "," + longitude, PROXIMITY_RADIUS);
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

    // gets details for the selected place (name, address, phone number, ratings etc.)
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
                    currentPlaceID = place.getId();
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

    // gets reviews for a specific place from the database
    public void getReviews() {
        Intent intent = new Intent(activity, ReviewsListActivity.class);
        intent.putExtra("id", currentPlaceID);
        activity.startActivity(intent);
    }

    // adds a user written review for a specific place to the database
    public void addReview() {
        Intent intent = new Intent(activity, WriteReviewActivity.class);
        intent.putExtra("id", currentPlaceID);
        activity.startActivity(intent);
    }

    // gets directions from the user's current location to the selected place on Google Maps
    public void getDirections(LatLng place, String name) {
        Intent intent = new Intent(activity, GetDirectionsActivity.class);
        String url = getDirectionsUrl(new LatLng(latitude, longitude), place);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
        intent.putExtra("userLatLng", new LatLng(latitude, longitude));
        intent.putExtra("placeLatLng", place);
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

    /* the methods and private classes below are used to get a polyline
     * that will go from the user's current position to
     * the selected place
     * and then it sends that polyline (PolylineOptions object)
     * to the GetDirectionsActivity through an intent
     * just like the locations, reviews etc. that are sent
     * to the other activities through an intent
     */

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Toast.makeText(activity, "Error while taking directions", Toast.LENGTH_SHORT);
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }


    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";

            if (result.size() < 1) {
                Toast.makeText(activity, "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // Get distance from the list
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }

            String text = "Distance:" + distance + ", Duration:" + duration;
            Intent intent = new Intent();
            intent.setAction("Polyline");
            intent.putExtra("text", text);
            intent.putExtra("polyline", lineOptions);
            activity.sendBroadcast(intent);
            // Drawing polyline in the Google Map for the i-th route
        }
    }
}
