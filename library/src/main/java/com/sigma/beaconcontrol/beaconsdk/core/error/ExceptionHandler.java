package com.sigma.beaconcontrol.beaconsdk.core.error;

import com.sigma.beaconcontrol.beaconsdk.util.ULog;

/**
 * Created by Wilson on 7/13/17.
 */

public class ExceptionHandler {

    private static final String TAG = ExceptionHandler.class.getName();

    public static void handleException(Throwable ex, String title, String message){
        ex.printStackTrace();
        ULog.e(TAG, ex.getCause().getMessage());
    }

    public static void handleException(Throwable ex){
        ex.printStackTrace();
        ULog.e(TAG, ex.getCause().getMessage());
    }
}
