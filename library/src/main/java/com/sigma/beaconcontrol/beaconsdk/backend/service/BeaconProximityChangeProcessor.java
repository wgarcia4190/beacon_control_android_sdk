package com.sigma.beaconcontrol.beaconsdk.backend.service;

import android.content.Intent;

import com.sigma.beaconcontrol.beaconsdk.core.model.Beacon;
import com.sigma.beaconcontrol.beaconsdk.util.ULog;

/**
 * Service responsible for notifying client that a beacon's proximity has changed
 *
 * @author Wilson Garcia
 * Created on 10/27/17.
 */

public class BeaconProximityChangeProcessor extends BaseProcessor {

    private static final String TAG = BeaconProximityChangeProcessor.class.getSimpleName();

    public interface Extra {
        String BEACON = "com.sigma.beaconcontrol.beaconsdk.backend.service.BeaconProximityChangeProcessor.BEACON";
    }

    public BeaconProximityChangeProcessor() {
        super("BeaconProximityChangeProcessor");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ULog.d(TAG, "onHandleIntent.");
        eventsManager
                .processBeaconProximityChanged((Beacon) intent.getSerializableExtra(Extra.BEACON),
                        getApplicationContext());
    }
}
