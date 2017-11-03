package com.sigma.beaconcontrol.beaconsdk.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sigma.beaconcontrol.beaconsdk.core.model.Beacon;
import com.sigma.beaconcontrol.beaconsdk.util.ULog;

/**
 * @author Wilson Garcia
 * Created on 10/25/17.
 */

public class BeaconProximityChangeReceiver extends BroadcastReceiver {

    private static final String TAG = "BeaconProximityChangeReceiver";
    public static final String PROCESS_RESPONSE = "BeaconControl.BeaconProximityChangeReceiver.PROCESS_RESPONSE";
    public static final String BEACON = "BeaconControl.BeaconProximityChangeReceiver.BEACON";

    private BeaconSDKDelegate beaconDelegate;

    public BeaconProximityChangeReceiver(BeaconSDKDelegate beaconDelegate){
        this.beaconDelegate = beaconDelegate;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (beaconDelegate == null) {
            ULog.d(TAG, "beaconDelegate is null.");
            return;
        }

        if (intent.hasExtra(BEACON)) {
            Beacon beacon = (Beacon) intent.getSerializableExtra(BEACON);
            beaconDelegate.onBeaconProximityChanged(beacon);
        } else {
            ULog.d(TAG, "Unknown operation.");
        }
    }
}
