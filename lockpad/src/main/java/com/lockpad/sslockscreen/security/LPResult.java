package com.lockpad.sslockscreen.security;

public class LPResult<T> {

    private LPSecurityError mError = null;
    private T mResult = null;

    public LPResult(LPSecurityError mError) {
        this.mError = mError;
    }

    public LPResult(T result) {
        mResult = result;
    }

    public LPSecurityError getError() {
        return mError;
    }

    public T getResult() {
        return mResult;
    }
}
