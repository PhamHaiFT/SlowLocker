package com.superslow.locker.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.superslow.locker.service.LockerService;

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //start service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LockerService.startForegroundService(context);
        } else {
            LockerService.startService(context);
        }
    }
}
