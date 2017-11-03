package com.sigma.beaconcontrol.beaconsdk.backend.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.sigma.beaconcontrol.beaconsdk.backend.SDKControlManager;
import com.sigma.beaconcontrol.beaconsdk.backend.model.Configuration;
import com.sigma.beaconcontrol.beaconsdk.core.Config;
import com.sigma.beaconcontrol.beaconsdk.util.ApplicationUtils;
import com.sigma.beaconcontrol.beaconsdk.util.ULog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Wilson Garcia
 *         Created on  10/25/17
 */

public class BeaconServiceHelper {

    private static final String TAG = BeaconServiceHelper.class.getSimpleName();

    private static final Object instanceLock = new Object();
    private static BeaconServiceHelper instance;

    private final Config config;
    private final SDKControlManager sdkControlManager;

    //private GetConfigurationsCallMediator getConfigurationsCallMediator;
    private Configuration configurations;
    private boolean isLoadingConfiguration;
    private boolean bound = false;

    private BeaconServiceHelper(Config config, SDKControlManager sdkControlManager) {
        this.config = config;
        this.sdkControlManager = sdkControlManager;
    }

    public static BeaconServiceHelper getInstance(Config config, SDKControlManager sdkControlManager) {
        synchronized (instanceLock) {
            if (instance == null) {
                instance = new BeaconServiceHelper(config, sdkControlManager);
            }
            return instance;
        }
    }

    public void getConfigurationsAsync(Context context) {
        isLoadingConfiguration = true;

        sdkControlManager.getConfigurationsCall(result -> {
            isLoadingConfiguration = false;

            if (bound) {
                notifyService(context, getBeaconServiceIntentWithConf(context),
                        BeaconSDKService.Command.START_SCAN);
            }
        });
    }

    private void handleUnsupportedProtocols(Configuration response) {
        List<Long> unsupportedRangeIds = removeRangesWithUnsupportedProtocols(response);
        removeUnsupportedRangesFromTriggers(response, unsupportedRangeIds);
    }

    private List<Long> removeRangesWithUnsupportedProtocols(Configuration response) {
        List<Long> unsupportedRangeIds = new ArrayList<>();
        Iterator<Configuration.Range> it = response.getRanges().iterator();
        while (it.hasNext()) {
            Configuration.Range range = it.next();
            switch (range.getProtocol()) {
                case iBeacon:
                    // ignore, as this protocol is supported
                    break;
                default:
                    ULog.w(TAG, "Unsupported protocol for range id " + range.getId());
                    unsupportedRangeIds.add(range.getId());
                    it.remove();
            }
        }
        return unsupportedRangeIds;
    }

    private void removeUnsupportedRangesFromTriggers(Configuration response, List<Long> unsupportedRangeIds) {
        Iterator<Configuration.Trigger> it = response.getTriggers().iterator();
        while (it.hasNext()) {
            Configuration.Trigger trigger = it.next();
            trigger.getRange_ids().removeAll(unsupportedRangeIds);

            if (isTriggerUnused(trigger)) {
                it.remove();
            }
        }
    }

    private boolean isTriggerUnused(Configuration.Trigger trigger) {
        return trigger.getRange_ids().isEmpty();
    }

    private boolean isGetConfigurationsInProgress() {
        return isLoadingConfiguration;
    }

    public void startScan(Context context) {
        if (!isGetConfigurationsInProgress()) {
            getConfigurationsAsync(context);
        }

        if (bound) return;

        notifyService(context, getBeaconServiceIntentWithConf(context),
                BeaconSDKService.Command.START_SCAN);
        bound = true;
    }

    public void stopScan(Context context) {
        if (bound) {
            notifyService(context, getBeaconServiceIntent(context),
                    BeaconSDKService.Command.STOP_SCAN);
            bound = false;
        }

        configurations = null;
        sdkControlManager.clearToken();
    }

    private void notifyService(Context context, Intent i, BeaconSDKService.Command cmd) {
        i.putExtra(BeaconSDKService.Extra.COMMAND, cmd);
        context.startService(i);
    }

    private Intent getBeaconServiceIntentWithConf(Context context) {
        return getBeaconServiceIntent(context).putExtra(BeaconSDKService.Extra.CONFIGURATIONS,
                configurations);
    }

    private Intent getBeaconServiceIntent(Context context) {
        Intent i = new Intent(BeaconSDKService.ACTION_NAME);
        i.setComponent(getAppropriateBeaconService(context, i));
        i.putExtra(BeaconSDKService.Extra.CLIENT_APP_PACKAGE, context.getPackageName());

        return i;
    }

    private ComponentName getAppropriateBeaconService(Context context, Intent i) {
        return ApplicationUtils.getAppropriateBeaconService(context, config, i);
    }
}
