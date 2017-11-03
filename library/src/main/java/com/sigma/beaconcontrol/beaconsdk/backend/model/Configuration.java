package com.sigma.beaconcontrol.beaconsdk.backend.model;

import com.sigma.beaconcontrol.beaconsdk.backend.Validable;
import com.sigma.beaconcontrol.beaconsdk.core.model.Action;
import com.sigma.beaconcontrol.beaconsdk.util.StringUtils;
import com.sigma.beaconcontrol.beaconsdk.util.ULog;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ing. Wilson Garcia
 * Created on 10/24/17.
 */

public class Configuration implements Serializable, Validable {

    private static final String TAG = Configuration.class.getSimpleName();

    private List<Trigger> triggers;
    private List<Range> ranges;
    private List<Zone> zones;
    private long ttl;


    public static class Trigger implements Serializable, Validable {

        private long id;
        private List<Condition> conditions;

        public static class Condition implements Serializable {

            public enum EventType {enter, leave, dwell_time, immediate, near, far}

            private enum Type {event_type}

            private EventType eventType;
            private Type type;

            public EventType getEventType() {
                return eventType;
            }

            public void setEventType(EventType eventType) {
                this.eventType = eventType;
            }

            public Type getType() {
                return type;
            }

            public void setType(Type type) {
                this.type = type;
            }
        }

        private Boolean test;
        private List<Long> range_ids;
        private List<Long> zone_ids;
        private Action action;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public List<Condition> getConditions() {
            return conditions;
        }

        public void setConditions(List<Condition> conditions) {
            this.conditions = conditions;
        }

        public Boolean getTest() {
            return test;
        }

        public void setTest(Boolean test) {
            this.test = test;
        }

        public List<Long> getRange_ids() {
            return range_ids;
        }

        public void setRange_ids(List<Long> range_ids) {
            this.range_ids = range_ids;
        }

        public List<Long> getZone_ids() {
            return zone_ids;
        }

        public void setZone_ids(List<Long> zone_ids) {
            this.zone_ids = zone_ids;
        }

        public Action getAction() {
            return action;
        }

        public void setAction(Action action) {
            this.action = action;
        }

        @Override
        public boolean isValid() {
            return true;
        }
    }

    public static class Range implements Serializable, Validable {

        public enum Protocol {
            iBeacon, Eddystone, Unknown;

            private static Map<String, Protocol> protocolsMap = new HashMap<>();

            static {
                protocolsMap.put("iBeacon", iBeacon);
                protocolsMap.put("Eddystone", Eddystone);
                protocolsMap.put("Unknown", Unknown);
            }

            public static Protocol forValue(String value) {
                return protocolsMap.containsKey(value) ? protocolsMap.get(value) : Unknown;
            }

            public String toValue() {
                for (Map.Entry<String, Protocol> entry : protocolsMap.entrySet()) {
                    if (entry.getValue() == this) {
                        return entry.getKey();
                    }
                }

                return Unknown.name();
            }
        }

        private long id;
        private String name;
        private Protocol protocol;
        private BeaconProximity proximityId;
        private Location location;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Protocol getProtocol() {
            return protocol;
        }

        public void setProtocol(Protocol protocol) {
            this.protocol = protocol;
        }

        public BeaconProximity getProximityId() {
            return proximityId;
        }

        public void setProximityId(BeaconProximity proximityId) {
            this.proximityId = proximityId;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public static class Location implements Serializable {

            private float lat;
            private float lng;
            private int floor;

            public float getLat() {
                return lat;
            }

            public float getLng() {
                return lng;
            }

            public int getFloor() {
                return floor;
            }

            public void setFloor(int floor) {
                this.floor = floor;
            }

            public void setLat(final String latStr) {
                lat = StringUtils.safeParseFloat(latStr, -1);
            }

            public void setLng(final String lngStr) {
                lng = StringUtils.safeParseFloat(lngStr, -1);
            }


        }

        @Override
        public boolean isValid() {
            return name != null && !name.isEmpty() &&
                    proximityId != null && proximityId.isValid();
        }
    }

    public static class Zone implements Serializable, Validable {
        private long id;
        private String name;
        private List<Long> beaconIds;
        private BeaconSDKColor color;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Long> getBeaconIds() {
            return beaconIds;
        }

        public void setBeaconIds(List<Long> beaconIds) {
            this.beaconIds = beaconIds;
        }

        public BeaconSDKColor getColor() {
            return color;
        }

        public void setColor(BeaconSDKColor color) {
            this.color = color;
        }

        @Override
        public boolean isValid() {
            return true;
        }
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<Trigger> triggers) {
        this.triggers = triggers;
    }

    public List<Range> getRanges() {
        return ranges;
    }

    public void setRanges(List<Range> ranges) {
        this.ranges = ranges;
    }

    public List<Zone> getZones() {
        return zones;
    }

    public void setZones(List<Zone> zones) {
        this.zones = zones;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    @Override
    public boolean isValid() {
        for (Trigger trigger : triggers) {
            if (!trigger.isValid()) {
                ULog.d(TAG, "Configurations triggers are invalid.");
                return false;
            }
        }

        for (Range range : ranges) {
            if (!range.isValid()) {
                ULog.d(TAG, "Configurations ranges are invalid.");
                return false;
            }
        }

        for (Zone zone : zones) {
            if (!zone.isValid()) {
                ULog.d(TAG, "Configurations zones are invalid.");
                return false;
            }
        }

        return true;
    }
}
