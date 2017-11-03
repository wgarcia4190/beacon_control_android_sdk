package com.sigma.beaconcontrol.beaconsdk.backend.model;

import android.graphics.Color;

import com.sigma.beaconcontrol.beaconsdk.util.ULog;

import java.io.Serializable;

/**
 * @author Wilson Garcia
 * Created on 10/24/17
 */

public class BeaconSDKColor implements Serializable {

    private static final String TAG = BeaconSDKColor.class.getSimpleName();

    private static final int DEFAULT_COLOR_VALUE = Color.WHITE;

    private String colorHex;
    private int colorValue;

    public BeaconSDKColor(String colorHex) {
        this.colorHex = colorHex;

        if (colorHex == null) {
            ULog.d(TAG, "Color hex is null.");
            colorValue = DEFAULT_COLOR_VALUE;
        } else {
            try {
                colorValue = Color.parseColor(colorHex.charAt(0) == '#' ? colorHex : "#" + colorHex);
            } catch (IllegalArgumentException e) {
                ULog.d(TAG, "Cannot parse color.");
                colorValue = DEFAULT_COLOR_VALUE;
            }
        }
    }

    public String getColorHex() {
        return colorHex;
    }

    public int getColorValue() {
        return colorValue;
    }
}

