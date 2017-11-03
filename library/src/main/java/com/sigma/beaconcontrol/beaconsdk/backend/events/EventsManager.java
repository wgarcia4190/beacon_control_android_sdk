package com.sigma.beaconcontrol.beaconsdk.backend.events;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;

import com.sigma.beaconcontrol.beaconsdk.backend.SDKControlManager;
import com.sigma.beaconcontrol.beaconsdk.backend.model.Configuration;
import com.sigma.beaconcontrol.beaconsdk.backend.model.CreateEventsRequest;
import com.sigma.beaconcontrol.beaconsdk.backend.model.CreateEventsRequest.Event;
import com.sigma.beaconcontrol.beaconsdk.backend.service.BeaconEvent;
import com.sigma.beaconcontrol.beaconsdk.backend.service.BeaconModel;
import com.sigma.beaconcontrol.beaconsdk.backend.service.EventInfo;
import com.sigma.beaconcontrol.beaconsdk.core.ActionReceiver;
import com.sigma.beaconcontrol.beaconsdk.core.BeaconProximityChangeReceiver;
import com.sigma.beaconcontrol.beaconsdk.core.Config;
import com.sigma.beaconcontrol.beaconsdk.core.ConfigurationLoadReceiver;
import com.sigma.beaconcontrol.beaconsdk.core.SDKPreferences;
import com.sigma.beaconcontrol.beaconsdk.core.model.Action;
import com.sigma.beaconcontrol.beaconsdk.core.model.Beacon;
import com.sigma.beaconcontrol.beaconsdk.core.model.BeaconsList;
import com.sigma.beaconcontrol.beaconsdk.util.ULog;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Wilson Garcia
 * Created on 10/24/17.
 */

public class EventsManager {

    private static final String TAG = EventsManager.class.getSimpleName();

    private static EventsManager instance;
    private static final Object shouldPerformActionAutomaticallyLock = new Object();
    private static boolean shouldPerformActionAutomatically = true;

    private final Config config;
    private final SDKPreferences preferences;
    private final SDKControlManager sdkControlManager;

    private EventsManager(Config config, SDKPreferences preferences,
                          SDKControlManager sdkControlManager) {
        this.config = config;
        this.preferences = preferences;
        this.sdkControlManager = sdkControlManager;
    }

    public static EventsManager getInstance(Config config, SDKPreferences preferences,
                                            SDKControlManager beaconControlManager) {
        if (instance == null) {
            instance = new EventsManager(config, preferences, beaconControlManager);
        }
        return instance;
    }

    public static void setShouldPerformActionAutomatically(boolean shouldPerformActionAutomatically) {
        synchronized (shouldPerformActionAutomaticallyLock) {
            shouldPerformActionAutomatically = shouldPerformActionAutomatically;
        }
    }

    private static boolean shouldPerformActionAutomatically() {
        synchronized (shouldPerformActionAutomaticallyLock) {
            return shouldPerformActionAutomatically;
        }
    }

    public void processEvent(BeaconModel beaconModel, Configuration.Trigger trigger,
                             EventInfo eventInfo, Context context){

        if (beaconModel == null || trigger == null || eventInfo == null || eventInfo.getBeaconEvent() == null) {
            ULog.d(TAG, "Cannot process event.");
            return;
        }

        BeaconEvent beaconEvent = eventInfo.getBeaconEvent();
        @EventInfo.IEventSource int eventSource = eventInfo.getEventSource();

        ULog.d(TAG, "beacon: " + beaconModel.getProximityId() + ", event: " + beaconEvent.name() + ", type: "
                + (eventSource == EventInfo.EventSource.BEACON ? "beacon" : "zone") + ".");

        if (beaconEvent == BeaconEvent.REGION_ENTER || beaconEvent == BeaconEvent.REGION_LEAVE) {
            processEnterLeaveEvent(beaconModel, trigger, eventInfo);
        }

        processAction(trigger.getAction(), context);
    }

    public void processConfigurationLoaded(BeaconsList beaconsList, Context context) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ConfigurationLoadReceiver.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(ConfigurationLoadReceiver.BEACONS_LIST, beaconsList);
        context.sendBroadcast(broadcastIntent);
    }

    public void processBeaconProximityChanged(Beacon beacon, Context context) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BeaconProximityChangeReceiver.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(BeaconProximityChangeReceiver.BEACON, beacon);
        context.sendBroadcast(broadcastIntent);
    }

    private void processEnterLeaveEvent(BeaconModel bm, Configuration.Trigger trigger,
                                        EventInfo eventInfo) {
        Event event = getEventFromBeaconEvent(bm, trigger, eventInfo);

        List<Event> events = preferences.getEventsList();
        events.add(event);

        long currentTimeMillis = System.currentTimeMillis();
        if (preferences.getEventsSentTimestamp() + config.getEventsSendoutDelayInMillis() < currentTimeMillis) {
            sendEvents(events);
            preferences.setEventsSentTimestamp(currentTimeMillis);
            preferences.setEventsList(new LinkedList<Event>());
        } else {
            preferences.setEventsList(events);
        }
    }

    private Event getEventFromBeaconEvent(BeaconModel bm, Configuration.Trigger trigger,
                                                              EventInfo eventInfo) {
        Event event = new Event();
        event.setEventType(eventInfo.getBeaconEvent() == BeaconEvent.REGION_ENTER ?
                Event.EventType.enter : Event.EventType.leave);

        if (eventInfo.getEventSource() == EventInfo.EventSource.BEACON) {
            event.setProximityId(bm.getProximityId());
        } else {
            event.setZoneId(bm.getZone().getId());
        }

        event.setActionId(trigger.getAction().getId());
        event.setTimestamp(eventInfo.getTimestamp() / DateUtils.SECOND_IN_MILLIS);
        return event;
    }

    private void sendEvents(List<Event> events) {
        sdkControlManager.createEventsCall(getCreateEventsRequest(events), result -> {

        });
    }

    private CreateEventsRequest getCreateEventsRequest(List<Event> events) {
        return new CreateEventsRequest(events);
    }

    private Intent getIntentForActionReceiver() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ActionReceiver.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);

        return broadcastIntent;
    }

    private void onActionStart(Action action, Context context) {
        ULog.d(TAG, "onActionStart.");

        Intent broadcastIntent = getIntentForActionReceiver();
        broadcastIntent.putExtra(ActionReceiver.ACTION_START, action);
        context.sendBroadcast(broadcastIntent);
    }

    private void onActionEnd(Action action, Context context) {
        ULog.d(TAG, "onActionEnd.");

        Intent broadcastIntent = getIntentForActionReceiver();
        broadcastIntent.putExtra(ActionReceiver.ACTION_END, action);
        context.sendBroadcast(broadcastIntent);
    }

    private void processAction(Action action, Context context) {
        if (action == null || action.getType() == null) {
            ULog.d(TAG, "Cannot process action.");
            return;
        }

        onActionStart(action, context);

        if (shouldPerformActionAutomatically()) {
            switch (action.getType()) {
                case url:
                case coupon:
                    if (action.getPayload() != null && action.getPayload().getUrl() != null) {
                        displayPage(action.getName(), action.getPayload().getUrl());
                    }
            }
        } else {
            ULog.d(TAG, "Should not perform action automatically.");
        }

        onActionEnd(action, context);
    }

    private void displayPage(String name, String url) {
        //launchActivity(WebViewActivity.getIntent(context, name, url));
    }

    private void launchActivity(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //context.startActivity(intent);
    }
}
