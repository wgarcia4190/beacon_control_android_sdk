package com.sigma.beaconcontrol.beaconsdk.backend.model;

import com.sigma.beaconcontrol.beaconsdk.backend.Validable;

/**
 * Created by Wilson on 10/23/17.
 */

public class Token implements Validable {

    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private String refreshToken;
    private long createdAt;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean isValid() {
        // TODO
        return true;
    }
}
