package com.sigma.beaconcontrol.beaconsdk.util.service;

/**
 * Created by Wilson on 7/13/17.
 */

public enum  HttpScheme {
    HTTP("http://"),
    HTTPS("https://");

    private String scheme;

    private HttpScheme(String scheme){
        this.scheme = scheme;
    }

    public String getScheme(){
        return scheme;
    }

    public static HttpScheme getScheme(String scheme){
        if("http".equals(scheme)){
            return HTTP;
        }
        return HTTPS;
    }
}
