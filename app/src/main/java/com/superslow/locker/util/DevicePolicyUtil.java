package com.superslow.locker.util;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.superslow.locker.service.LockerService;

public class DevicePolicyUtil {

    private static final String TAG = "DevicePolicyUtil";
    
    private DevicePolicyListener devicePolicyListener;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName componentName;
    private Context context;

    private static DevicePolicyUtil ourInstance;

    public static DevicePolicyUtil getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new DevicePolicyUtil(context);
        }
        return ourInstance;
    }


    private DevicePolicyUtil(Context context) {
        this.context = context;
        devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(context, LockerService.class);
    }

    public boolean isAdminActive() {
        return devicePolicyManager.isAdminActive(componentName);
    }


    public void activityAdmin(Activity activity, int requestCode) {
        Intent intent = new Intent(
                DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "LockScreen");
        activity.startActivityForResult(intent, requestCode);
    }

    public void removeActiveAdmin() {
        devicePolicyManager.removeActiveAdmin(componentName);
    }

    public void lockNow(){
        devicePolicyManager.lockNow();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void unlock(Activity activity){
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Activity.KEYGUARD_SERVICE);
        keyguardManager.requestDismissKeyguard(activity, new KeyguardManager.KeyguardDismissCallback() {
            @Override
            public void onDismissError() {
                super.onDismissError();
            }

            @Override
            public void onDismissSucceeded() {
                super.onDismissSucceeded();
            }

            @Override
            public void onDismissCancelled() {
                super.onDismissCancelled();
            }
        });
    }

    public void onActiveAdmin() {
        if (devicePolicyListener != null) {
            devicePolicyListener.onActiveAdmin();
        }
    }

    public void onRemoveActivate() {
        if (devicePolicyListener != null) {
            devicePolicyListener.onRemovedActiveAdmin();
        }
    }

    public DevicePolicyListener getDevicePolicyListener() {
        return devicePolicyListener;
    }

    public void setDevicePolicyListener(DevicePolicyListener devicePolicyListener) {
        this.devicePolicyListener = devicePolicyListener;
    }

    public interface DevicePolicyListener {
        void onActiveAdmin();

        void onRemovedActiveAdmin();
    }
}
