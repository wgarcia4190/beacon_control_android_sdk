package com.sigma.beaconcontrol.beaconsdk.core.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author Wilson Garcia
 * Created on 10/23/17
 */

public class BeaconsList implements Serializable {

    public List<Beacon> beaconList;

    public BeaconsList(List<Beacon> beaconList) {
        this.beaconList = beaconList;
    }
}
