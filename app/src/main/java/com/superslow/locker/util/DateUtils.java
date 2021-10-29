package com.superslow.locker.util;

import android.content.Context;


import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateUtils {
    private static SimpleDateFormat sHourFormat24 = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static SimpleDateFormat sHourFormat12 = new SimpleDateFormat("hh:mm", Locale.getDefault());

    public static String getHourString(Context context, long time) {
        String strTimeFormat = android.provider.Settings.System.getString(context.getContentResolver(),
                android.provider.Settings.System.TIME_12_24);
        if (("12").equals(strTimeFormat)) {
            try {
                return sHourFormat12.format(time);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sHourFormat24.format(time);
    }

}
