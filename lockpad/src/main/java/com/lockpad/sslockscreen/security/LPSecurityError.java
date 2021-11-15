package com.lockpad.sslockscreen.security;

public class LPSecurityError {

    private final String mMessage;
    private final Integer mCode;

    LPSecurityError(String message, Integer code) {
        mMessage = message;
        mCode = code;
    }

    public String getMessage() {
        return mMessage;
    }

    public Integer getCode() {
        return mCode;
    }
}
