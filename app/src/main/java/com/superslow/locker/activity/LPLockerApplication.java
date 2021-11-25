package com.superslow.locker.activity;


import android.app.Application;
import android.os.Build;

import com.superslow.locker.service.LPLockerService;
import com.superslow.locker.service.LPTraceService;
import com.superslow.locker.task.LPExecuteTaskManager;
import com.xdandroid.hellodaemon.DaemonEnv;

public class LPLockerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LPLockerService.startForegroundService(getBaseContext());
        } else {
            LPLockerService.startService(getBaseContext());
        }
        LPExecuteTaskManager.getInstance().init();
        DaemonEnv.initialize(this, LPTraceService.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
        LPTraceService.sShouldStopService = false;
        DaemonEnv.startServiceMayBind(LPTraceService.class);
    }
}
