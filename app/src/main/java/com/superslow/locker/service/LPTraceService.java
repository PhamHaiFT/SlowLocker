package com.superslow.locker.service;


import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;

import java.util.concurrent.TimeUnit;

import com.xdandroid.hellodaemon.*;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class LPTraceService extends AbsWorkService {

    public static boolean sShouldStopService;
    public static Disposable sDisposable;

    public static void stopService() {
        sShouldStopService = true;
        if (sDisposable != null) sDisposable.dispose();
        cancelJobAlarmSub();
    }

    @Override
    public Boolean shouldStopService(Intent intent, int flags, int startId) {
        return sShouldStopService;
    }

    @Override
    public void startWork(Intent intent, int flags, int startId) {
        sDisposable = Flowable
                .interval(3, TimeUnit.SECONDS)
                .doOnCancel(new Action() {
                    @Override
                    public void run() throws Exception {
                        cancelJobAlarmSub();
                    }
                })
                .subscribe(new Consumer<Long>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void accept(Long count) throws Exception {
//
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            LPLockerService.startForegroundService(getBaseContext());
                        } else {
                            LPLockerService.startService(getBaseContext());
                        }
                        LPLockerService.startService(getBaseContext());

                        System.out.println("count = " + count);
                        if (count > 0 && count % 18 == 0)
                            System.out.println("saveCount = " + (count / 18 - 1));
                    }
                });
    }

    @Override
    public void stopWork(Intent intent, int flags, int startId) {
        stopService();
    }

    @Override
    public Boolean isWorkRunning(Intent intent, int flags, int startId) {
        return sDisposable != null && !sDisposable.isDisposed();
    }

    @Override
    public IBinder onBind(Intent intent, Void v) {
        return null;
    }

    @Override
    public void onServiceKilled(Intent rootIntent) {
        System.out.println("Service killed");
    }
}