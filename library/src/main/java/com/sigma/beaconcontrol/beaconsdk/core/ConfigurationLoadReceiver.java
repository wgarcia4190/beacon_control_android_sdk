package com.sigma.beaconcontrol.beaconsdk.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sigma.beaconcontrol.beaconsdk.core.model.BeaconsList;
import com.sigma.beaconcontrol.beaconsdk.util.ULog;

/**
 * @author Wilson Garcia
 * Created on 10/25/17.
 */

public class ConfigurationLoadReceiver extends BroadcastReceiver {

    private static final String TAG = "ConfigurationLoadReceiver";
    public static final String PROCESS_RESPONSE = "BeaconControl.ConfigurationLoadReceiver.PROCESS_RESPONSE";
    public static final String BEACONS_LIST = "BeaconControl.ConfigurationLoadReceiver.BEACONS_LIST";

    private BeaconSDKDelegate beaconDelegate;

    public ConfigurationLoadReceiver(BeaconSDKDelegate beaconDelegate){
        this.beaconDelegate = beaconDelegate;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (beaconDelegate == null) {
            ULog.d(TAG, "beaconDelegate is null.");
            return;
        }

        if (intent.hasExtra(BEACONS_LIST)) {
            BeaconsList beaconsList = (BeaconsList) intent.getSerializableExtra(BEACONS_LIST);
            beaconDelegate.onBeaconsConfigurationLoaded(beaconsList.beaconList);
        } else {
            ULog.d(TAG, "Unknown operation.");
        }
    }
}
