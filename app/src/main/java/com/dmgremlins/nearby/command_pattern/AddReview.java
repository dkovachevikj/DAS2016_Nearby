package com.dmgremlins.nearby.command_pattern;

/**
 * Created by User on 1/13/2017.
 */

public class AddReview implements Command{

    private NearbyEventBus nearbyEventBus;

    public AddReview(NearbyEventBus nearbyEventBus) {
        this.nearbyEventBus = nearbyEventBus;
    }

    @Override
    public void execute() {
        nearbyEventBus.addReview();
    }
}
