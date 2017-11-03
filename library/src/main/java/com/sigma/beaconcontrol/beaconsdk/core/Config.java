package com.sigma.beaconcontrol.beaconsdk.core;

/**
 * Created by Wilson on 10/23/17.
 */

public interface Config {

    int getConnectionTimeoutInSec();

    int getReadTimeoutInSec();

    int getWriteTimeoutInSec();

    String getServiceHTTPScheme();

    String getServiceBaseUrl();

    int getMaxRunningServices();

    int getLeaveMsgDelayInMillis();

    String getBeaconParserLayout();

    int getForegroundScanDurationInMillis();

    int getForegroundPauseDurationInMillis();

    int getBackgroundScanDurationInMillis();

    int getBackgroundPauseDurationInMillis();

    long getEventsSendoutDelayInMillis();
}
