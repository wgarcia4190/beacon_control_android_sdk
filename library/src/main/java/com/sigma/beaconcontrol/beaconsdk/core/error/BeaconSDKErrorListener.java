package com.sigma.beaconcontrol.beaconsdk.core.error;

/**
 * This interface represents listener for error that may occur during Beacon SDK usage.
 *
 * @author Wilson Garcia
 * Created on 10/23/17
 */
public interface BeaconSDKErrorListener {

    /**
     * It is called when an error has occurred.
     *
     * @param errorCode Error code.
     */
    void onError(ErrorCode errorCode);
}
