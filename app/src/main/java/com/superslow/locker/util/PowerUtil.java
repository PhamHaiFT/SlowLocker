package com.superslow.locker.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class PowerUtil {

    public static boolean isCharging(Context context) {
        int status = getBatteryStatus(context, BatteryManager.EXTRA_STATUS, -1);

        return (status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL);
    }

    public static int getLevel(Context context) {
        int level = getBatteryStatus(context, BatteryManager.EXTRA_LEVEL, 0);
        return level;
    }

    public static int getScale(Context context) {
        int scale = getBatteryStatus(context, BatteryManager.EXTRA_SCALE, -1);
        return scale;
    }

    private static int getBatteryStatus(Context context, String extraName, int defaultValue) {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, intentFilter);
        if (batteryStatus == null) {
            return defaultValue;
        } else {
            return batteryStatus.getIntExtra(extraName, defaultValue);
        }
    }

}
