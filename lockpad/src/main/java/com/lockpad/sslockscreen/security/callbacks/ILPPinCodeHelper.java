package com.lockpad.sslockscreen.security.callbacks;

import android.content.Context;

import com.lockpad.sslockscreen.security.callbacks.LPPinCodeHelperCallback;

public interface ILPPinCodeHelper {

    void encodePin(Context context, String pin, LPPinCodeHelperCallback<String> callBack);

    void checkPin(Context context, String encodedPin, String pin, LPPinCodeHelperCallback<Boolean> callback);

    void delete(LPPinCodeHelperCallback<Boolean> callback);

    void isPinCodeEncryptionKeyExist(LPPinCodeHelperCallback<Boolean> callback);

}
