package com.lockpad.sslockscreen.security.callbacks;

import com.lockpad.sslockscreen.security.LPResult;

public interface LPPinCodeHelperCallback<T> {
    void onResult(LPResult<T> result);
}
