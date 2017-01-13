package com.dmgremlins.nearby.command_pattern;

/**
 * Created by User on 1/13/2017.
 */

public class GetReviews implements Command {

    private NearbyEventBus nearbyEventBus;

    public GetReviews(NearbyEventBus nearbyEventBus) {
        this.nearbyEventBus = nearbyEventBus;
    }

    @Override
    public void execute() {
        nearbyEventBus.getReviews();
    }
}
