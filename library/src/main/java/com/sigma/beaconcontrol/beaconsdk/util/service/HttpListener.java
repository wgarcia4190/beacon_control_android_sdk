package com.sigma.beaconcontrol.beaconsdk.util.service;

/**
 * @author Ing. Wilson Garcia
 * Created on 10/29/17
 */

@FunctionalInterface
public interface HttpListener<T> {
    void onResult(T result);
}
