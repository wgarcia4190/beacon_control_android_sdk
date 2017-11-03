package com.sigma.beaconcontrol.beaconsdk.util.service;

import android.support.annotation.NonNull;

import com.sigma.beaconcontrol.beaconsdk.core.error.ExceptionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Wilson on 7/13/17.
 */

public class JsonBuilder {

    private JSONObject jsonObject = new JSONObject();

    public JsonBuilder addProperty(@NonNull String key, @NonNull String value) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            ExceptionHandler.handleException(e);
        }
        return this;
    }

    public JsonBuilder addProperty(@NonNull String key, @NonNull double value) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            ExceptionHandler.handleException(e);
        }
        return this;
    }

    public JsonBuilder addProperty(@NonNull String key, @NonNull Map<String, Integer> values) {
        try {
            JSONObject value = new JSONObject();

            for (Map.Entry<String, Integer> entry : values.entrySet()) {
                value.put(entry.getKey(), entry.getValue());
            }
            jsonObject.put(key, value);
        } catch (JSONException e) {
            ExceptionHandler.handleException(e);
        }
        return this;
    }

    public String build() {
        return jsonObject.toString();
    }
}
