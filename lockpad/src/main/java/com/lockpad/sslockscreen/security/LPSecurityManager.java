package com.lockpad.sslockscreen.security;

import com.lockpad.sslockscreen.security.callbacks.ILPPinCodeHelper;

public class LPSecurityManager {
    private static final LPSecurityManager ourInstance = new LPSecurityManager();

    public static LPSecurityManager getInstance() {
        return ourInstance;
    }

    private LPSecurityManager() {
    }

    private ILPPinCodeHelper mPinCodeHelper = LPFingerprintPinCodeHelper.getInstance();

    public void setPinCodeHelper(ILPPinCodeHelper pinCodeHelper) {
        mPinCodeHelper = pinCodeHelper;
    }

    public ILPPinCodeHelper getPinCodeHelper() {
        return mPinCodeHelper;
    }
}
