package com.sigma.beaconcontrol.beaconsdk.backend;

import com.sigma.beaconcontrol.beaconsdk.backend.model.Configuration;
import com.sigma.beaconcontrol.beaconsdk.backend.model.CreateEventsRequest;
import com.sigma.beaconcontrol.beaconsdk.util.service.HttpListener;

/**
 * Created by Wilson on 10/23/17.
 */

public interface SDKControlManager {

    String CONFIGURATION_ENDPOINT = "/get-configurations";

    void getConfigurationsCall(HttpListener<Configuration> httpListener);
    void createEventsCall(CreateEventsRequest request, HttpListener<String> httpListener);

    void clearToken();
}
