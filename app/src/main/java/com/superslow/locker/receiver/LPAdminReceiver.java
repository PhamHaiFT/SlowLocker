package com.superslow.locker.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

import com.superslow.locker.util.DevicePolicyUtil;

public class LPAdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        DevicePolicyUtil.getInstance(context).onActiveAdmin();
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        DevicePolicyUtil.getInstance(context).onRemoveActivate();
    }
}
