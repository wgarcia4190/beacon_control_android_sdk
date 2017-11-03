package com.sigma.beaconcontrol.beaconsdk.backend.model;

/**
 * Created by Wilson on 10/23/17.
 */

public class TokenCredentials {

    private final String clientId;
    private final String clientSecret;
    private final String userId;

    public TokenCredentials(String clientId, String clientSecret, String userId) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.userId = userId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getUserId() {
        return userId;
    }
}
