package com.dmgremlins.nearby.command_pattern;

/**
 * Created by User on 1/13/2017.
 */

public class GetPlaceDetails implements Command {

    private NearbyEventBus nearbyEventBus;
    private String id;

    public GetPlaceDetails(NearbyEventBus nearbyEventBus, String id) {
        this.nearbyEventBus = nearbyEventBus;
        this.id = id;
    }


    @Override
    public void execute() {
        nearbyEventBus.getPlaceDetails(id);
    }
}
