package com.sigma.beaconcontrol.beaconsdk.backend.service;

import android.content.Intent;

import com.sigma.beaconcontrol.beaconsdk.core.model.BeaconsList;
import com.sigma.beaconcontrol.beaconsdk.util.ULog;

/**
 * Service responsible for notifying client that new beacon configuration has been just loaded from
 * the backend.
 *
 * @author Wilson Garcia
 * Created on 10/27/17.
 */
public class BeaconConfigurationChangeProcessor extends BaseProcessor {

    private static final String TAG = BeaconConfigurationChangeProcessor.class.getSimpleName();

    public interface Extra {
        String BEACONS_LIST = "com.sigma.beaconcontrol.beaconsdk.backend.service.BeaconConfigurationChangeProcessor.BEACONS_LIST";
    }

    public BeaconConfigurationChangeProcessor() {
        super("BeaconConfigurationChangeProcessor");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ULog.d(TAG, "onHandleIntent.");
        eventsManager
                .processConfigurationLoaded(
                        (BeaconsList) intent.getSerializableExtra(Extra.BEACONS_LIST),
                        getApplicationContext());
    }
}
