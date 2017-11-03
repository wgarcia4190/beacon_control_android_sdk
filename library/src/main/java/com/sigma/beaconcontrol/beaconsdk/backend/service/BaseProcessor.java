package com.sigma.beaconcontrol.beaconsdk.backend.service;

import android.app.IntentService;
import android.content.Context;

import com.sigma.beaconcontrol.beaconsdk.backend.SDKControlManager;
import com.sigma.beaconcontrol.beaconsdk.backend.SDKControlManagerImpl;
import com.sigma.beaconcontrol.beaconsdk.backend.events.EventsManager;
import com.sigma.beaconcontrol.beaconsdk.core.Config;
import com.sigma.beaconcontrol.beaconsdk.core.ConfigImpl;
import com.sigma.beaconcontrol.beaconsdk.core.SDKPreferences;
import com.sigma.beaconcontrol.beaconsdk.core.SDKPreferencesImpl;

/**
 * @author Wilson Garcia
 * Created on 10/27/17
 */

public abstract class BaseProcessor extends IntentService {

    private static final String TAG = BaseProcessor.class.getSimpleName();

    protected EventsManager eventsManager;

    public BaseProcessor(String serviceName) {
        super("BaseProcessor");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Context appContext = getApplicationContext();
        Config config = ConfigImpl.getInstance(appContext);
        SDKPreferences preferences = SDKPreferencesImpl.getInstance(appContext);
        SDKControlManager beaconControlManager = SDKControlManagerImpl.
                getInstance(appContext, config, preferences);

        eventsManager = EventsManager.getInstance(config, preferences, beaconControlManager);
    }
}
