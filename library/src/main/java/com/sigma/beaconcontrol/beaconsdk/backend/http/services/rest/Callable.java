package com.sigma.beaconcontrol.beaconsdk.backend.http.services.rest;

import com.sigma.beaconcontrol.beaconsdk.backend.http.parsers.Parser;
import com.sigma.beaconcontrol.beaconsdk.core.error.ExceptionHandler;

import org.json.JSONException;

/**
 * Created by Wilson on 4/2/17.
 */

public abstract class Callable<T extends Parser> {

    private Class<T> clazz;

    public Callable(Class<T> clazz) {
        this.clazz = clazz;
    }

    public <T extends Parser> T getParserInstance() {
        try {
            return (T) clazz.newInstance();
        } catch (InstantiationException e) {
            ExceptionHandler.handleException(e);
        } catch (IllegalAccessException e) {
            ExceptionHandler.handleException(e);
        }
        return null;
    }

    public void onPreExecute() {
    }

    public abstract void onPostExecute(T parser) throws JSONException;
}
