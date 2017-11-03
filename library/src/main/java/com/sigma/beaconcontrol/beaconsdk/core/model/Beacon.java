package com.sigma.beaconcontrol.beaconsdk.core.model;

import java.io.Serializable;

/**
 * @author Wilson Garcia
 * Created on 10/23/17
 */

public class Beacon implements Serializable {
    public static final double DISTANCE_UNDEFINED = Double.MAX_VALUE;

    public enum Proximity {
        OUT_OF_RANGE, FAR, NEAR, IMMEDIATE
    }

    private final Long id;
    private final String name;
    private final String uuid;
    private final Integer major;
    private final Integer minor;
    private Proximity currentProximity;
    private double distance;

    public Beacon(Long id, String name, String uuid, Integer major, Integer minor,
                  Proximity currentProximity, double distance) {
        this.id = id;
        this.name = name;
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.currentProximity = currentProximity;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public Integer getMajor() {
        return major;
    }

    public Integer getMinor() {
        return minor;
    }

    public Proximity getCurrentProximity() {
        return currentProximity;
    }

    public void setCurrentProximity(Proximity currentProximity) {
        this.currentProximity = currentProximity;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
