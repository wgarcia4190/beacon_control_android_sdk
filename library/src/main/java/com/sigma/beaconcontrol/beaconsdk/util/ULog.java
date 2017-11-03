package com.sigma.beaconcontrol.beaconsdk.util;

import android.util.Log;

/**
 * Created by Wilson on 10/23/17.
 */

public final class ULog {
    private static boolean enabled = false;

    private static String format(String tag) {
        return "BeaconSDK~" + tag;
    }

    public static void enableLogging(boolean enable) {
        enabled = enable;
    }

    public static void d(String tag, String msg) {
        if (enabled) {
            Log.d(format(tag), msg);
        }
    }

    public static void w(String tag, String msg) {
        if (enabled) {
            Log.w(format(tag), msg);
        }
    }

    public static void e(String tag, String msg) {
        if (enabled) {
            Log.e(format(tag), msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (enabled) {
            Log.e(format(tag), msg, tr);
        }
    }
}
