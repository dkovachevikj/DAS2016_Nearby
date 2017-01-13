package com.dmgremlins.nearby.command_pattern;

/**
 * Created by User on 1/13/2017.
 */

public class GetLocations implements Command {

    private NearbyEventBus nearbyEventBus;
    private String type;

    public GetLocations(NearbyEventBus nearbyEventBus, String type) {
        this.nearbyEventBus = nearbyEventBus;
        this.type = type;
    }

    @Override
    public void execute() {
        nearbyEventBus.getLocations(type);
    }
}
