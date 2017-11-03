package com.sigma.beaconcontrol.beaconsdk.util.service;

/**
 * Created by Wilson on 7/13/17.
 */

public enum HttpMethod {
    GET("GET"), POST("POST"), PUT("PUT"), PATCH("PATCH"), DELETE("DELETE");

    private String method;

    private HttpMethod(String method){
        this.method = method;
    }

    public String getMethod(){
        return this.method;
    }
}
