package com.sigma.beaconcontrol.beaconsdk.backend.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Wilson on 10/23/17.
 */

public class CreateEventsRequest implements Serializable{

    private List<Event> events;

    public static class Event implements Serializable {
        public enum EventType {enter, leave}

        private EventType eventType;
        private String proximityId;
        private Long zoneId;
        private long actionId;
        private long timestamp;

        public Event() {

        }

        public Event(EventType eventType) {
            this.eventType = eventType;
        }

        public Event setEventType(EventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public Event setProximityId(String proximityId) {
            this.proximityId = proximityId;
            return this;
        }

        public Event setZoneId(Long zoneId) {
            this.zoneId = zoneId;
            return this;
        }

        public Event setActionId(long actionId) {
            this.actionId = actionId;
            return this;
        }

        public Event setTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }
    }

    public CreateEventsRequest(List<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents() {
        return events;
    }
}
