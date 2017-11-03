package com.sigma.beaconcontrol.beaconsdk.core;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateUtils;

import com.sigma.beaconcontrol.beaconsdk.R;
import com.sigma.beaconcontrol.beaconsdk.util.ULog;

/**
 * This implementation of the interface {@link Config} handles all the configuration
 * properties that we need to use in this project.
 *
 * THe value of each configuration is defined inside of the config.xml file
 *
 * @author Wilson Garcia
 * Created on 10/23/17
 */

public final class ConfigImpl implements Config {

    private static final Object instanceLock = new Object();
    private static Config instance;
    private Resources resources;

    private ConfigImpl(Context context) {
        this.resources = context.getResources();
    }

    public static Config getInstance(Context context) {
        synchronized (instanceLock) {
            if (instance == null) {
                instance = new ConfigImpl(context);
            }
            return instance;
        }
    }

    @Override
    public int getConnectionTimeoutInSec() {
        return resources.getInteger(R.integer.beacon_sdk_config_connect_timeout_seconds);
    }

    @Override
    public int getReadTimeoutInSec() {
        return resources.getInteger(R.integer.beacon_sdk_config_read_timeout_seconds);
    }

    @Override
    public int getWriteTimeoutInSec() {
        return resources.getInteger(R.integer.beacon_sdk_config_write_timeout_seconds);
    }

    @Override
    public String getServiceHTTPScheme() {
        return resources.getString(R.string.beacon_sdk_config_service_scheme);
    }

    @Override
    public String getServiceBaseUrl() {
        return resources.getString(R.string.beacon_sdk_config_service_base_url);
    }

    @Override
    public int getMaxRunningServices() {
        return resources.getInteger(R.integer.beacon_sdk_config_max_running_services);
    }

    @Override
    public int getLeaveMsgDelayInMillis() {
        return resources.getInteger(R.integer.beacon_sdk_config_leave_msg_delay_millis);
    }

    @Override
    public String getBeaconParserLayout() {
        return resources.getString(R.string.beacon_sdk_config_iBeacon_parser_layout);
    }

    @Override
    public int getForegroundScanDurationInMillis() {
        return resources.getInteger(R.integer.beacon_sdk_config_foreground_scan_duration_millis);
    }

    @Override
    public int getForegroundPauseDurationInMillis() {
        return resources.getInteger(R.integer.beacon_sdk_config_foreground_pause_duration_millis);
    }

    @Override
    public int getBackgroundScanDurationInMillis() {
        return resources.getInteger(R.integer.beacon_sdk_config_background_scan_duration_millis);
    }

    @Override
    public int getBackgroundPauseDurationInMillis() {
        return resources.getInteger(R.integer.beacon_sdk_config_background_pause_duration_millis);
    }

    @Override
    public long getEventsSendoutDelayInMillis() {
        return resources.getInteger(R.integer.beacon_sdk_config_events_sendout_timeout_seconds) * DateUtils.SECOND_IN_MILLIS;
    }
}
