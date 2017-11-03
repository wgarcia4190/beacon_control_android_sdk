package com.sigma.beaconcontrol.beaconsdk.util;

import java.util.Date;
import java.util.Random;

/**
 * Created by Wilson on 10/25/17.
 */

public class RandUtils {

    private RandUtils() {
    }

    private static Random instance = new Random(new Date().getTime());

    public static long nextLong() {
        return instance.nextLong();
    }
}
