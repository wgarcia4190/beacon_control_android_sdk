package com.sigma.beaconcontrol.beaconsdk.backend;

import com.sigma.beaconcontrol.beaconsdk.util.service.HttpListener;

/**
 * @author Ing. Wilson Garcia
 * Created on 10/29/17
 */

public interface SDKTokenManager {

    void getToken(HttpListener<String> httpListener);
}
