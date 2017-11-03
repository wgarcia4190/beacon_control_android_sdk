package com.sigma.beaconcontrol.beaconsdk.backend.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;

import com.sigma.beaconcontrol.beaconsdk.backend.model.Configuration;
import com.sigma.beaconcontrol.beaconsdk.core.model.Beacon;
import com.sigma.beaconcontrol.beaconsdk.core.model.BeaconsList;
import com.sigma.beaconcontrol.beaconsdk.util.ULog;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Wilson Garcia
 * Created on 10/27/17
 */

public class ClientsManager {

    private static final String TAG = ClientsManager.class.getSimpleName();

    private Context context;
    private BeaconManager beaconManager;
    private Set<String> clients = new HashSet<>();
    private Map<BeaconModel, BeaconEvent> monitoredBeacons = new HashMap<>();

    protected ClientsManager(Context context, BeaconManager beaconManager) {
        this.context = context;
        this.beaconManager = beaconManager;
    }

    protected boolean hasClients() {
        return !clients.isEmpty();
    }

    public boolean containsClient(String clientId) {
        return clients.contains(clientId);
    }

    protected void addClient(String clientId, Configuration conf) {
        clients.add(clientId);

        addMonitoredBeacons(clientId, conf);
        notifyClientsAboutConfigurationLoaded(clientId, conf);
    }

    protected void updateClient(String clientId, Configuration conf) {
        clients.add(clientId);

        removeMonitoredBeacons(clientId);
        addMonitoredBeacons(clientId, conf);
        notifyClientsAboutConfigurationLoaded(clientId, conf);
    }

    protected void removeClient(String clientId) {
        removeMonitoredBeacons(clientId);

        clients.remove(clientId);
    }

    protected BeaconEvent getBeaconEvent(String beaconUniqueId) {
        BeaconModel bm = getMonitoredBeaconModel(beaconUniqueId);
        return bm == null ? null : monitoredBeacons.get(bm);
    }

    protected void updateBeaconEvent(String beaconUniqueId, BeaconEvent event) {
        BeaconModel bm = getMonitoredBeaconModel(beaconUniqueId);
        if (bm != null) {
            monitoredBeacons.put(bm, event);
        }
    }

    protected void notifyClientsAboutAction(String beaconUniqueId, BeaconEvent beaconEvent, long eventTimestamp) {
        BeaconModel bm = getMonitoredBeaconModel(beaconUniqueId);
        if (bm == null) return;

        EventInfo eventInfo = new EventInfo(beaconEvent, eventTimestamp, EventInfo.EventSource.BEACON);
        notifyClientsAboutAction(bm, bm.getBeaconTriggersForEvent(beaconEvent), eventInfo);

        if (bm.getZone() != null) {
            eventInfo = new EventInfo(beaconEvent, eventTimestamp, EventInfo.EventSource.ZONE);
            notifyClientsAboutAction(bm, bm.getZoneTriggersForEvent(beaconEvent), eventInfo);
        }
    }

    public void notifyClientsAboutBeaconProximityChange(String beaconUniqueId,
                                                        BeaconEvent beaconEvent,
                                                        long eventTimestamp, double distance) {
        BeaconModel bm = getMonitoredBeaconModel(beaconUniqueId);
        if (bm == null) return;

        EventInfo eventInfo = new EventInfo(beaconEvent, eventTimestamp, EventInfo.EventSource.BEACON);

        notifyClientsAboutBeaconProximityChange(bm, eventInfo, distance);
    }

    protected BeaconEvent getBeaconEventFromDistance(double distance) {
        return BeaconEvent.fromBeaconDistance(distance);
    }

    private BeaconModel getMonitoredBeaconModel(String beaconUniqueId) {
        for (BeaconModel bm : monitoredBeacons.keySet()) {
            if (bm.getUniqueId().equals(beaconUniqueId)) {
                return bm;
            }
        }
        return null;
    }

    private void addMonitoredBeacons(String clientId, Configuration conf) {
        for (Configuration.Range range : conf.getRanges()) {
            Map.Entry<BeaconModel, BeaconEvent> beaconEntry = getMonitoredBeaconEntry(range.getId());

            BeaconModel bm;
            BeaconEvent event;
            if (beaconEntry == null || beaconEntry.getKey() == null) {
                bm = new BeaconModel(range);
                Configuration.Zone zone = getBeaconZone(bm.getId(), conf.getZones());
                if (zone != null) {
                    bm.setZone(zone);
                }

                event = BeaconEvent.UNKNOWN;

                addNewRegion(bm);
            } else {
                bm = beaconEntry.getKey();
                event = beaconEntry.getValue();
            }

            addTriggers(clientId, bm, conf.getTriggers());
            bm.addClient(clientId);
            monitoredBeacons.put(bm, event);
        }
    }

    private void addTriggers(String clientId, BeaconModel bm, List<Configuration.Trigger> triggers) {
        for (Configuration.Trigger trigger : triggers) {
            if (trigger.getRange_ids() != null && trigger.getRange_ids().contains(bm.getId())) {
                bm.addBeaconTrigger(clientId, trigger);
            }
            if (trigger.getZone_ids() != null && bm.getZone() != null &&
                    trigger.getZone_ids().contains(bm.getZone().getId())) {
                bm.addZoneTrigger(clientId, trigger);
            }
        }
    }

    private void addNewRegion(BeaconModel bm) {
        Region region = new Region(bm.getUniqueId(), Identifier.parse(bm.getProximityUUID()), Identifier.parse(bm.getProximityMajor().toString()), Identifier.parse(bm.getProximityMinor().toString()));
        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            ULog.e(TAG, "Cannot start monitor/range region.", e);
        }
    }

    private Map.Entry<BeaconModel, BeaconEvent> getMonitoredBeaconEntry(long beaconId) {
        for (Map.Entry<BeaconModel, BeaconEvent> entry : monitoredBeacons.entrySet()) {
            if (entry.getKey().getId() == beaconId) {
                return entry;
            }
        }
        return null;
    }

    private Configuration.Zone getBeaconZone(long beaconId, List<Configuration.Zone> zones) {
        for (Configuration.Zone zone : zones) {
            if (zone.getBeaconIds().contains(beaconId)) {
                return zone;
            }
        }
        return null;
    }

    private void removeMonitoredBeacons(String clientId) {
        Map<BeaconModel, BeaconEvent> newMonitoredBeacons = new HashMap<>();

        for (Map.Entry<BeaconModel, BeaconEvent> entry : monitoredBeacons.entrySet()) {
            BeaconModel bm = entry.getKey();
            if (bm.containsClient(clientId)) {
                bm.removeClient(clientId);
                removeTriggers(clientId, bm);
            }
            if (bm.hasClients()) {
                newMonitoredBeacons.put(bm, entry.getValue());
            } else {
                try {
                    beaconManager.stopMonitoringBeaconsInRegion(new Region(bm.getUniqueId(), null, null, null));
                    beaconManager.stopRangingBeaconsInRegion(new Region(bm.getUniqueId(), null, null, null));
                } catch (RemoteException e) {
                    ULog.d(TAG, "Cannot stop monitor/range region.");
                }
            }
        }
        monitoredBeacons = newMonitoredBeacons;
    }

    private void removeTriggers(String clientId, BeaconModel bm) {
        bm.removeClientTriggers(clientId);
    }

    private void notifyClientsAboutAction(BeaconModel bm, Map<String, List<Configuration.Trigger>> eventTriggers, EventInfo eventInfo) {
        for (Map.Entry<String, List<Configuration.Trigger>> entry : eventTriggers.entrySet()) {
            String client = entry.getKey();
            for (Configuration.Trigger trigger : entry.getValue()) {
                Bundle extrasBundle = new Bundle();
                extrasBundle.putSerializable(BeaconActionProcessor.Extra.BEACON, bm);
                extrasBundle.putSerializable(BeaconActionProcessor.Extra.TRIGGER, trigger);
                extrasBundle.putSerializable(BeaconActionProcessor.Extra.EVENT, eventInfo);

                notifyClients(client, extrasBundle, BeaconActionProcessor.class);
            }
        }
    }

    private void notifyClients(String clientId, Bundle extras, Class notificationProcessorClass) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(clientId, notificationProcessorClass.getCanonicalName()));
        intent.putExtras(extras);
        context.startService(intent);
    }

    private void notifyClientsAboutBeaconProximityChange(BeaconModel beaconModel, EventInfo eventInfo, double distance) {
        final Beacon beacon = new Beacon(
                beaconModel.getId(),
                beaconModel.getName(),
                beaconModel.getProximityUUID(),
                beaconModel.getProximityMajor(),
                beaconModel.getProximityMinor(),
                mapBeaconEventToProximity(eventInfo.getBeaconEvent()),
                distance
        );
        for (String clientId : beaconModel.getClients()) {
            Bundle extrasBundle = new Bundle();
            extrasBundle.putSerializable(BeaconProximityChangeProcessor.Extra.BEACON, beacon);
            notifyClients(clientId, extrasBundle, BeaconProximityChangeProcessor.class);
        }
    }

    private void notifyClientsAboutConfigurationLoaded(String clientId,
                                                       Configuration configurationsResponse) {
        Bundle extrasBundle = new Bundle();
        extrasBundle.putSerializable(BeaconConfigurationChangeProcessor.Extra.BEACONS_LIST, getMonitoredBeaconsListWithCurrentProximity(configurationsResponse));
        notifyClients(clientId, extrasBundle, BeaconConfigurationChangeProcessor.class);
    }

    private BeaconsList getMonitoredBeaconsListWithCurrentProximity(Configuration configurationsResponse) {
        final List<Beacon> beaconsList = new ArrayList<>();

        for (Configuration.Range range : configurationsResponse.getRanges()) {
            Map.Entry<BeaconModel, BeaconEvent> beaconEntry = getMonitoredBeaconEntry(range.getId());
            Beacon beacon = new Beacon(
                    range.getId(),
                    range.getName(),
                    range.getProximityId().getUUID(),
                    range.getProximityId().getMajor(),
                    range.getProximityId().getMinor(),
                    Beacon.Proximity.OUT_OF_RANGE,
                    Beacon.DISTANCE_UNDEFINED
            );
            if (beaconEntry != null && beaconEntry.getValue() != null) {
                beacon.setCurrentProximity(mapBeaconEventToProximity(beaconEntry.getValue()));
            }
            beaconsList.add(beacon);
        }
        return new BeaconsList(beaconsList);
    }

    private Beacon.Proximity mapBeaconEventToProximity(BeaconEvent event) {
        switch (event) {
            case CAME_IMMEDIATE:
                return Beacon.Proximity.IMMEDIATE;
            case CAME_NEAR:
                return Beacon.Proximity.NEAR;
            case CAME_FAR:
            case REGION_ENTER:
                return Beacon.Proximity.FAR;
            default:
                return Beacon.Proximity.OUT_OF_RANGE;
        }
    }
}