package com.sigma.beaconcontrol.beaconsdk.util;

import android.text.TextUtils;

/**
 * @author Wilson Garcia
 * Created on 10/24/17
 */

public final class StringUtils {

    private static final String TAG = StringUtils.class.getSimpleName();
    public static final String EMPTY = "";

    private StringUtils() {
    }

    public static String capitalizeFirstLetter(String input) {
        if (TextUtils.isEmpty(input)) {
            return input;
        }

        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static float safeParseFloat(String str, float fallback) {
        if (str == null) {
            // this check is needed for proper deserialization
            return fallback;
        }

        try {
            return Float.valueOf(str);
        } catch (NumberFormatException e) {
            ULog.d(TAG, String.format("Cannot parse float from %s.", str));
            return fallback;
        }
    }
}

