package com.sigma.beaconcontrol.beaconsdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.sigma.beaconcontrol.beaconsdk.backend.SDKControlManager;
import com.sigma.beaconcontrol.beaconsdk.backend.SDKControlManagerImpl;
import com.sigma.beaconcontrol.beaconsdk.backend.events.EventsManager;
import com.sigma.beaconcontrol.beaconsdk.backend.model.TokenCredentials;
import com.sigma.beaconcontrol.beaconsdk.backend.service.BeaconServiceHelper;
import com.sigma.beaconcontrol.beaconsdk.core.ActionReceiver;
import com.sigma.beaconcontrol.beaconsdk.core.BeaconProximityChangeReceiver;
import com.sigma.beaconcontrol.beaconsdk.core.BeaconSDKDelegate;
import com.sigma.beaconcontrol.beaconsdk.core.Config;
import com.sigma.beaconcontrol.beaconsdk.core.ConfigImpl;
import com.sigma.beaconcontrol.beaconsdk.core.ConfigurationLoadReceiver;
import com.sigma.beaconcontrol.beaconsdk.core.SDKPreferences;
import com.sigma.beaconcontrol.beaconsdk.core.SDKPreferencesImpl;
import com.sigma.beaconcontrol.beaconsdk.core.error.BeaconSDKErrorListener;
import com.sigma.beaconcontrol.beaconsdk.core.error.ErrorCode;
import com.sigma.beaconcontrol.beaconsdk.util.ApplicationUtils;
import com.sigma.beaconcontrol.beaconsdk.util.ULog;

import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

/**
 * This class represents singleton object that allows for interaction with Beacon SDK.
 * You need to call the {@link #getInstance(Context, String, String, String)} method
 * inside of your {@link android.app.Application} subclass
 * {@link android.app.Application#attachBaseContext(Context)} method.
 * <p>
 *
 * @author Wilson Garcia.
 *         Created on 10/23/17
 */
public final class BeaconSDK {

    private static final String TAG = BeaconSDK.class.getSimpleName();

    private static final Object instanceLock = new Object();
    private static BeaconSDK instance;
    private boolean started;

    private final BeaconServiceHelper beaconServiceHelper;
    private BeaconSDKErrorListener beaconSDKErrorListener;
    private BeaconSDKDelegate beaconDelegate;
    private BroadcastReceiver actionReceiver;
    private BroadcastReceiver configurationLoadReceiver;
    private BroadcastReceiver beaconProximityChangeReceiver;
    private final BackgroundPowerSaver backgroundPowerSaver;


    /**
     * Creates BeaconSDK instance.
     *
     * @param config               SDK Configuration.
     * @param preferences          Preferences Manager.
     * @param beaconControlManager Backend Connection Entry point.
     * @param tokenCredentials     Login credentials
     */
    private BeaconSDK(Context context, Config config, SDKPreferences preferences,
                      SDKControlManager beaconControlManager, TokenCredentials tokenCredentials) {

        beaconServiceHelper = BeaconServiceHelper.getInstance(config, beaconControlManager);
        backgroundPowerSaver = new BackgroundPowerSaver(context);

    }

    /**
     * Gets BeaconSDK instance.
     *
     * @param context      The Context from which application context is retrieved.
     * @param clientId     OAuth client id.
     * @param clientSecret OAuth client secret.
     * @param userId       Unique id per application and user.
     * @return Instance of BeaconSDK.
     */
    public static BeaconSDK getInstance(Context context, String clientId, String clientSecret,
                                        String userId) {
        synchronized (instanceLock) {
            if (instance == null) {
                instance = getBeaconSDKInstance(context, new TokenCredentials(clientId,
                        clientSecret, userId));
            }
            return instance;
        }

    }

    /**
     * Creates BeaconSDK instance.
     *
     * @param context          The Context from which application context is retrieved.
     * @param tokenCredentials Login credentials
     * @return Instance of BeaconSDK.
     */
    private static BeaconSDK getBeaconSDKInstance(Context context, TokenCredentials tokenCredentials) {
        Context appContext = context.getApplicationContext();
        Config config = ConfigImpl.getInstance(appContext);
        SDKPreferences preferences = SDKPreferencesImpl.getInstance(appContext);

        preferences.setOAuthCredentials(tokenCredentials);
        SDKControlManager beaconControlManager = SDKControlManagerImpl
                .getInstance(appContext, config, preferences);

        return new BeaconSDK(appContext, config, preferences, beaconControlManager, tokenCredentials);
    }

    public static void onError(ErrorCode errorCode) {
        synchronized (instanceLock) {
            if (instance != null) {
                instance.notifyAboutError(errorCode);
            } else {
                ULog.d(TAG, "onError, instance is null.");
            }
        }
    }

    /**
     * Sets {@link BeaconSDKDelegate} object.
     *
     * @param beaconDelegate Object that implements {@link BeaconSDKDelegate} interface.
     */
    public void setBeaconDelegate(BeaconSDKDelegate beaconDelegate) {
        this.beaconDelegate = beaconDelegate;
        EventsManager
                .setShouldPerformActionAutomatically(beaconDelegate
                        .shouldPerformActionAutomatically());
    }

    /**
     * Sets listener that informs about errors.
     *
     * @param beaconSDKErrorListener Object that implements {@link BeaconSDKErrorListener} interface.
     */
    public void setBeaconErrorListener(BeaconSDKErrorListener beaconSDKErrorListener) {
        this.beaconSDKErrorListener = beaconSDKErrorListener;
    }

    /**
     * Enable or disable logging.
     *
     * @param enable True if the logging should be enabled, false otherwise.
     */
    public void enableLogging(boolean enable) {
        ULog.enableLogging(enable);
    }

    /**
     * Starts beacons monitoring.
     */
    public void startScan(Context context) {
        if (ApplicationUtils.isBLEAvailable(context, beaconSDKErrorListener) &&
                ApplicationUtils.isBluetoothEnabled(context, beaconSDKErrorListener)) {
            if (!started) {
                registerReceivers(context);
            }
            beaconServiceHelper.startScan(context);
            started = true;
        }
    }

    /**
     * Stops beacons monitoring.
     */
    public void stopScan(Context context) {
        if (started) {
            beaconServiceHelper.stopScan(context);
            unregisterReceivers(context);
            started = false;
            instance = null;
        }
    }

    private void registerActionReceiver(Context context) {
        IntentFilter filter = new IntentFilter(ActionReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);

        actionReceiver = new ActionReceiver(beaconDelegate);
        context.registerReceiver(actionReceiver, filter);
    }

    private void registerConfigurationLoadReceiver(Context context) {
        IntentFilter filter = new IntentFilter(ConfigurationLoadReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);

        configurationLoadReceiver = new ConfigurationLoadReceiver(beaconDelegate);
        context.registerReceiver(configurationLoadReceiver, filter);
    }

    private void registerBeaconProximityChangeReceiver(Context context) {
        IntentFilter filter = new IntentFilter(BeaconProximityChangeReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);

        beaconProximityChangeReceiver = new BeaconProximityChangeReceiver(beaconDelegate);
        context.registerReceiver(beaconProximityChangeReceiver, filter);
    }

    private void registerReceivers(Context context) {
        registerActionReceiver(context);
        registerConfigurationLoadReceiver(context);
        registerBeaconProximityChangeReceiver(context);
    }

    private void unregisterReceivers(Context context) {
        context.unregisterReceiver(actionReceiver);
        context.unregisterReceiver(configurationLoadReceiver);
        context.unregisterReceiver(beaconProximityChangeReceiver);
    }

    private void notifyAboutError(ErrorCode errorCode) {
        if (beaconSDKErrorListener != null) {
            beaconSDKErrorListener.onError(errorCode);
        }
    }

    /**
     * Reloads configuration from the backend.
     * This method should be called only if the scanning is already started (startScan() was called).
     */
    public void reloadConfiguration(Context context) {
        if (started) {
            beaconServiceHelper.getConfigurationsAsync(context);
        } else {
            Log.w(TAG, "Cannot reload configuration as the service is not yet started, call startScan() if not called yet.");
        }
    }
}
