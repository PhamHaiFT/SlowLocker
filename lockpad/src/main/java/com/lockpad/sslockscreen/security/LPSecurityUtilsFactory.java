package com.lockpad.sslockscreen.security;

import com.lockpad.sslockscreen.security.callbacks.IPFSecurityUtils;

public class LPSecurityUtilsFactory {

    public static IPFSecurityUtils getPFSecurityUtilsInstance() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            return LPSecurityUtils.getInstance();
        } else {
            return LPSecurityUtilsOld.getInstance();
        }
    }

}
