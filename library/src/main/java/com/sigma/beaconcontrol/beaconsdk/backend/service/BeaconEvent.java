package com.sigma.beaconcontrol.beaconsdk.backend.service;

import com.sigma.beaconcontrol.beaconsdk.util.ULog;

import java.io.Serializable;

/**
 * @author Wilson Garcia
 * Created on 10/24/17
 */

public enum BeaconEvent implements Serializable {

    /**
     * A beacon has appeared in range
     */
    REGION_ENTER,

    /**
     * A beacon has disappeared from range
     */
    REGION_LEAVE,

    /**
     * A beacon has just changed proximity to immediate
     */
    CAME_IMMEDIATE,

    /**
     * A beacon has just changed proximity to near
     */
    CAME_NEAR,

    /**
     * A beacon has just changed proximity to far
     */
    CAME_FAR,

    /**
     * A beacon proximity is unknown, e.g. beacon has just been added to monitored set
     */
    UNKNOWN;

    private static final String TAG = BeaconEvent.class.getSimpleName();

    private static final double IMMEDIATE_MAX_DISTANCE = 0.5;
    private static final double NEAR_MAX_DISTANCE = 3.0;

    public static BeaconEvent fromBeaconDistance(double distance) {
        if (distance < 0) {
            ULog.d(TAG, "Beacon distance less than 0.");
            return null;
        }

        if (distance <= IMMEDIATE_MAX_DISTANCE) {
            return CAME_IMMEDIATE;
        } else if (distance <= NEAR_MAX_DISTANCE) {
            return CAME_NEAR;
        } else {
            return CAME_FAR;
        }
    }
}
