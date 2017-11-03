package com.sigma.beaconcontrol.beaconsdk.backend.service;

import android.content.Intent;

import com.sigma.beaconcontrol.beaconsdk.backend.model.Configuration;
import com.sigma.beaconcontrol.beaconsdk.util.ULog;

/**
 * Service responsible for handling user-defined actions triggered by beacon/zone changes
 *
 * @author Wilson Garcia
 * Created on 10/27/17.
 */

public class BeaconActionProcessor extends BaseProcessor {

    private static final String TAG = BeaconActionProcessor.class.getSimpleName();

    public interface Extra {
        String BEACON = "com.sigma.beaconcontrol.beaconsdk.backend.service.BeaconIntentProcessor.BEACON";
        String TRIGGER = "com.sigma.beaconcontrol.beaconsdk.backend.service.BeaconIntentProcessor.TRIGGER";
        String EVENT = "com.sigma.beaconcontrol.beaconsdk.backend.service.BeaconIntentProcessor.EVENT";
    }

    public BeaconActionProcessor() {
        super("BeaconActionProcessor");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ULog.d(TAG, "onHandleIntent.");

        eventsManager.processEvent((BeaconModel) intent.getSerializableExtra(Extra.BEACON),
                (Configuration.Trigger) intent.getSerializableExtra(Extra.TRIGGER),
                (EventInfo) intent.getSerializableExtra(Extra.EVENT), getApplicationContext());
    }
}

