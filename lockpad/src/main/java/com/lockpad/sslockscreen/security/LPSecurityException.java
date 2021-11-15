package com.lockpad.sslockscreen.security;

public class LPSecurityException extends Exception {

    private final Integer mCode;

    public LPSecurityException(String message, Integer code) {
        super(message);
        mCode = code;
    }

    public Integer getCode() {
        return mCode;
    }
    
    public LPSecurityError getError() {
        return new LPSecurityError(getMessage(), getCode());
    }
}
