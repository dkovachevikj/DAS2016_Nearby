package com.dmgremlins.nearby;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by User on 1/12/2017.
 */

public class LocationService extends Service implements LocationListener {

    private Location lastLocation;
    private LocationManager locationManager;

    public LocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this,"Service started",Toast.LENGTH_LONG).show();
        if(locationManager==null){
            locationManager=(LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,60000,0,this);
        }
        catch (SecurityException e1){
            Log.d("LocationService","Security exception create");
        }
        catch (Exception e2){
            Log.d("LocationService","Error create");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this,"Service stopped",Toast.LENGTH_LONG).show();
        if(locationManager!=null){
            try {
                locationManager.removeUpdates(this);
            }
            catch (SecurityException e1){
                Log.d("LocationService","Security exception destroy");
            }
            catch (Exception e2){
                Log.d("LocationService","Error destroy");
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation=location;
        if(location==null){
            Log.d("LocationService","Location is null");
        }
        Toast.makeText(this,"Location Changed",Toast.LENGTH_LONG).show();
        Toast.makeText(this,"Latitude: "+lastLocation.getLatitude(),Toast.LENGTH_LONG).show();
        Toast.makeText(this,"Longitude: "+lastLocation.getLongitude(),Toast.LENGTH_LONG).show();
        Log.d("LocationService","Location Changed");
        Log.d("LocationService","Location lat "+lastLocation.getLatitude());
        Log.d("LocationService","Location long "+lastLocation.getLongitude());
        Intent intent = new Intent();
        intent.setAction("UpdateLocation");
        intent.putExtra("latitude", location.getLatitude());
        intent.putExtra("longitude", location.getLongitude());
        sendBroadcast(intent);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
