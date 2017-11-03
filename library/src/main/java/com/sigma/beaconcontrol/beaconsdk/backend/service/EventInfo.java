package com.sigma.beaconcontrol.beaconsdk.backend.service;

import android.support.annotation.IntDef;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Wilson on 10/25/17.
 */

public class EventInfo implements Serializable {

    public interface EventSource {
        int BEACON = 0;
        int ZONE = 1;
    }

    @IntDef({EventSource.BEACON, EventSource.ZONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface IEventSource {}

    private BeaconEvent beaconEvent;
    private long timestamp;
    private @IEventSource int eventSource;

    public EventInfo(BeaconEvent beaconEvent, long timestamp, @IEventSource int eventSource) {
        this.beaconEvent = beaconEvent;
        this.timestamp = timestamp;
        this.eventSource = eventSource;
    }

    public BeaconEvent getBeaconEvent() {
        return beaconEvent;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public @IEventSource int getEventSource() {
        return eventSource;
    }
}

