package com.sigma.beaconcontrol.beaconsdk.backend.service;

import com.sigma.beaconcontrol.beaconsdk.backend.model.BeaconProximity;
import com.sigma.beaconcontrol.beaconsdk.backend.model.Configuration.Range;
import com.sigma.beaconcontrol.beaconsdk.backend.model.Configuration.Range.Location;
import com.sigma.beaconcontrol.beaconsdk.backend.model.Configuration.Trigger;
import com.sigma.beaconcontrol.beaconsdk.backend.model.Configuration.Zone;
import com.sigma.beaconcontrol.beaconsdk.util.RandUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Wilson Garcia
 * Created on 10/24/17
 */

public class BeaconModel implements Serializable {

    private long id;
    private String name;
    private BeaconProximity proximityId;
    private Location location;
    private Zone zone;
    private Map<BeaconEvent, Map<String, List<Trigger>>> beaconTriggers;
    private Map<BeaconEvent, Map<String, List<Trigger>>> zoneTriggers;
    private Set<String> clients;
    private String uniqueId;

    public BeaconModel(Range range) {
        this.id = range.getId();
        this.name = range.getName();
        this.proximityId = range.getProximityId();
        this.location = range.getLocation();

        this.beaconTriggers = new HashMap<>();
        this.zoneTriggers = new HashMap<>();
        for (BeaconEvent event : BeaconEvent.values()) {
            beaconTriggers.put(event, new HashMap<String, List<Trigger>>());
            zoneTriggers.put(event, new HashMap<String, List<Trigger>>());
        }

        this.clients = new HashSet<>();
        this.uniqueId = String.valueOf(id + RandUtils.nextLong());
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public Zone getZone() {
        return zone;
    }

    public void addBeaconTrigger(String clientId, Trigger trigger) {
        addTrigger(clientId, beaconTriggers, trigger);
    }

    public void addZoneTrigger(String clientId, Trigger trigger) {
        addTrigger(clientId, zoneTriggers, trigger);
    }

    public void removeClientTriggers(String clientId) {
        removeClientTriggers(beaconTriggers, clientId);
        removeClientTriggers(zoneTriggers, clientId);
    }

    public String getProximityId() {
        return proximityId.getProximityId();
    }

    public String getProximityUUID() {
        return proximityId.getUUID();
    }

    public Integer getProximityMajor() {
        return proximityId.getMajor();
    }

    public Integer getProximityMinor() {
        return proximityId.getMinor();
    }

    public void addClient(String clientId) {
        clients.add(clientId);
    }

    public boolean containsClient(String clientId) {
        return clients.contains(clientId);
    }

    public void removeClient(String clientId) {
        clients.remove(clientId);
    }

    public boolean hasClients() {
        return !clients.isEmpty();
    }

    public Set<String> getClients() {
        return clients;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public Map<String, List<Trigger>> getBeaconTriggersForEvent(BeaconEvent event) {
        return beaconTriggers.get(event);
    }

    public Map<String, List<Trigger>> getZoneTriggersForEvent(BeaconEvent event) {
        return zoneTriggers.get(event);
    }

    private void addTrigger(String clientId, Map<BeaconEvent, Map<String, List<Trigger>>> triggers, Trigger trigger) {
        for (Trigger.Condition condition : trigger.getConditions()) {
            switch (condition.getEventType()) {
                case enter:
                    addTriggerForClient(clientId, triggers, trigger, BeaconEvent.REGION_ENTER);
                    break;
                case leave:
                    addTriggerForClient(clientId, triggers, trigger, BeaconEvent.REGION_LEAVE);
                    break;
                case immediate:
                    addTriggerForClient(clientId, triggers, trigger, BeaconEvent.CAME_IMMEDIATE);
                    break;
                case near:
                    addTriggerForClient(clientId, triggers, trigger, BeaconEvent.CAME_NEAR);
                    break;
                case far:
                    addTriggerForClient(clientId, triggers, trigger, BeaconEvent.CAME_FAR);
                    break;
            }
        }
    }

    private void addTriggerForClient(String clientId, Map<BeaconEvent, Map<String, List<Trigger>>> triggers, Trigger trigger, BeaconEvent event) {
        Map<String, List<Trigger>> eventTriggers = triggers.get(event);
        List<Trigger> eventAndClientTriggers = eventTriggers.get(clientId);
        if (eventAndClientTriggers == null) {
            eventAndClientTriggers = new LinkedList<>();
        }
        eventAndClientTriggers.add(trigger);
        eventTriggers.put(clientId, eventAndClientTriggers);
    }

    private void removeClientTriggers(Map<BeaconEvent, Map<String, List<Trigger>>> triggers, String clientId) {
        for (Map<String, List<Trigger>> eventTriggers : triggers.values()) {
            eventTriggers.remove(clientId);
        }
    }

}
