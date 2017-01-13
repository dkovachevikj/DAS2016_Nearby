package com.dmgremlins.nearby.command_pattern;

import com.google.android.gms.maps.model.LatLng;

import retrofit.http.GET;

/**
 * Created by User on 1/13/2017.
 */

public class GetDirections implements Command{

    private NearbyEventBus nearbyEventBus;
    private LatLng place;
    private String name;

    public GetDirections(NearbyEventBus nearbyEventBus, LatLng place, String name) {
        this.nearbyEventBus = nearbyEventBus;
        this.place = place;
        this.name = name;
    }


    @Override
    public void execute() {
        nearbyEventBus.getDirections(place, name);
    }
}
