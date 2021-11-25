package com.superslow.locker.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;

import com.superslow.locker.receiver.LPLockerReceiver;

import io.reactivex.annotations.Nullable;

public class LPLockerService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerLockerReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterLockerReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public static void startService(Context context) {
        try {
            Intent intent = new Intent(context, LPLockerService.class);
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            context.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void startForegroundService(Context context) {
        try {
            Intent intent = new Intent(context, LPLockerService.class);
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            context.startForegroundService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LPLockerReceiver lockerReceiver;

    private void registerLockerReceiver() {
        if (lockerReceiver != null) {
            return;
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);

        lockerReceiver = new LPLockerReceiver();
        registerReceiver(lockerReceiver, filter);
    }

    private void unregisterLockerReceiver() {
        if (lockerReceiver == null) {
            return;
        }
        unregisterReceiver(lockerReceiver);
        lockerReceiver = null;
    }
}
