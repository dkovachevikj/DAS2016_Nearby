package com.dmgremlins.nearby.command_pattern;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by User on 1/13/2017.
 */

public interface NearbyEventBus {

    void getLocations(String type);

    void getReviews();

    void addReview();

    void getPlaceDetails(String placeId);

    void getDirections(LatLng place, String name);
}
