package com.sigma.beaconcontrol.beaconsdk.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;

import com.sigma.beaconcontrol.beaconsdk.backend.service.BeaconSDKService;
import com.sigma.beaconcontrol.beaconsdk.core.Config;
import com.sigma.beaconcontrol.beaconsdk.core.error.BeaconSDKErrorListener;
import com.sigma.beaconcontrol.beaconsdk.core.error.ErrorCode;
import com.sigma.beaconcontrol.beaconsdk.core.error.ExceptionHandler;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Wilson on 10/25/17.
 */

public class ApplicationUtils {

    public static boolean isBLEAvailable(Context context, BeaconSDKErrorListener beaconSDKErrorListener) {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            if (beaconSDKErrorListener != null)
                beaconSDKErrorListener.onError(ErrorCode.BLE_NOT_SUPPORTED);
            return false;
        }

        return true;
    }

    public static boolean isBluetoothEnabled(Context context, BeaconSDKErrorListener beaconSDKErrorListener) {
        if (!((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE))
                .getAdapter().isEnabled()) {
            if (beaconSDKErrorListener != null)
                beaconSDKErrorListener.onError(ErrorCode.BLUETOOTH_NOT_ENABLED);
            return false;
        }

        return true;
    }

    private static ComponentName getRunningBeaconService(Context context, Config config) {
        final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert am != null;
        List<ActivityManager.RunningServiceInfo> services =
                am.getRunningServices(config.getMaxRunningServices());

        for (ActivityManager.RunningServiceInfo rsi : services) {
            if (rsi.service.getClassName().equals(BeaconSDKService.class.getName())) {
                return rsi.service;
            }
        }
        return null;
    }

    public static ComponentName getAppropriateBeaconService(final Context context, Config config, Intent i) {
        ComponentName cn = getRunningBeaconService(context, config);
        if (cn != null) {
            return cn;
        }

        @SuppressLint("WrongConstant")
        List<ResolveInfo> infos = context.getPackageManager()
                .queryIntentServices(i, Context.BIND_AUTO_CREATE);

        if (infos.isEmpty()) return null;

        Collections.sort(infos, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo l, ResolveInfo r) {
                String lLabel = l.loadLabel(context.getPackageManager()).toString();
                String rLabel = r.loadLabel(context.getPackageManager()).toString();
                return lLabel.compareTo(rLabel);
            }
        });

        ServiceInfo si = infos.get(0).serviceInfo;
        return new ComponentName(si.packageName, si.name);
    }

    public static void close(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException e) {
            ExceptionHandler.handleException(e);
        }
    }
}
