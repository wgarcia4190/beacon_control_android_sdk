package com.sigma.beaconcontrol.beaconsdk.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sigma.beaconcontrol.beaconsdk.core.model.Action;
import com.sigma.beaconcontrol.beaconsdk.util.ULog;

/**
 * @author Wilson Garcia
 * Created on 10/25/17
 */

public class ActionReceiver extends BroadcastReceiver {

    private static final String TAG = "ActionReceiver"; // added explicitly, because inner classes cannot have a static initializer block

    public static final String PROCESS_RESPONSE = "BeaconControl.ActionReceiver.PROCESS_RESPONSE";

    public static final String ACTION_START = "BeaconControl.ActionReceiver.ACTION_START";
    public static final String ACTION_END = "BeaconControl.ActionReceiver.ACTION_END";

    private BeaconSDKDelegate beaconDelegate;

    public ActionReceiver(BeaconSDKDelegate beaconDelegate) {
        this.beaconDelegate = beaconDelegate;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (beaconDelegate == null) {
            ULog.d(TAG, "beaconDelegate is null.");
            return;
        }

        if (intent.hasExtra(ACTION_START)) {
            ULog.d(TAG, "onActionStart.");
            beaconDelegate.onActionStart((Action) intent.getSerializableExtra(ACTION_START));
        } else if (intent.hasExtra(ACTION_END)) {
            ULog.d(TAG, "onActionEnd.");
            beaconDelegate.onActionEnd((Action) intent.getSerializableExtra(ACTION_END));
        } else {
            ULog.d(TAG, "Unknown operation.");
        }
    }
}
