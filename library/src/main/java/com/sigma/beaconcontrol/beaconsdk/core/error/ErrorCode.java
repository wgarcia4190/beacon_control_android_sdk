package com.sigma.beaconcontrol.beaconsdk.core.error;


/**
 * This enum represents error codes that may occur during Beacon SDK usage.
 *
 * @author Wilson Garcia.
 * Created on 10/23/17
 */
public enum ErrorCode {

    /**
     * Server error.
     */
    BEACON_CONTROL_ERROR,

    /**
     * There is some issue with Internet connection or web address.
     */
    IO_ERROR, // check Internet connection and web addresses

    /**
     * Device is not connected to the Internet.
     */
    OFFLINE,  // device not connected to the Internet

    /**
     * Bluetooth low energy feature is not supported.
     */
    BLE_NOT_SUPPORTED,

    /**
     * Bluetooth service is not enabled.
     */
    BLUETOOTH_NOT_ENABLED
}
