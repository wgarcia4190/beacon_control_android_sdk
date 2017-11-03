package com.sigma.beaconcontrol.beaconsdk.util.service;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wilson on 7/13/17.
 */

public class MapBuilder {
    private HashMap<String, String> params = new HashMap<>();

    public MapBuilder addParam(@NonNull String key, @NonNull String value){
        this.params.put(key, value);
        return this;
    }

    public HashMap<String, String> build(){
        return this.params;
    }

    public String buildBody(){
        StringBuilder body = new StringBuilder();
        for(Map.Entry<String, String> entry: this.params.entrySet()){
            body.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        body.deleteCharAt(body.lastIndexOf("&"));
        return body.toString();
    }
}
