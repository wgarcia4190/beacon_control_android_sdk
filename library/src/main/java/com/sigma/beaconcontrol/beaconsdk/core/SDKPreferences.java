package com.sigma.beaconcontrol.beaconsdk.core;

import com.sigma.beaconcontrol.beaconsdk.backend.model.CreateEventsRequest;
import com.sigma.beaconcontrol.beaconsdk.backend.model.TokenCredentials;

import java.util.List;

/**
 * Created by Wilson on 10/23/17.
 */

public interface SDKPreferences {

    void setOAuthCredentials(TokenCredentials tokenCredentials);

    TokenCredentials getOAuthCredentials();

    void setEventsSentTimestamp(long timestamp);

    long getEventsSentTimestamp();

    /*
     * Stores events that will be sent to server in a batch.
     */
    void setEventsList(List<CreateEventsRequest.Event> events);

    List<CreateEventsRequest.Event> getEventsList();
}
