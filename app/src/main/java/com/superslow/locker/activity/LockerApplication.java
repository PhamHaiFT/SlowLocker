package com.superslow.locker.activity;


import android.app.Application;
import android.os.Build;

import com.superslow.locker.service.LockerService;
import com.superslow.locker.service.TraceService;
import com.superslow.locker.task.ExecuteTaskManager;
import com.xdandroid.hellodaemon.DaemonEnv;

public class LockerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //start service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LockerService.startForegroundService(getBaseContext());
        } else {
            LockerService.startService(getBaseContext());
        }
        ExecuteTaskManager.getInstance().init();
        DaemonEnv.initialize(this, TraceService.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
        TraceService.sShouldStopService = false;
        DaemonEnv.startServiceMayBind(TraceService.class);
    }
}
