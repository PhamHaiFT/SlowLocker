package com.superslow.locker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.superslow.locker.activity.SlowLockerActivity;

public class LockerReceiver extends BroadcastReceiver {

    public LockerReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!TextUtils.isEmpty(action)) {
            if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
                //todo
            } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                SlowLockerActivity.startActivity(context);
            }
        }
    }
}
