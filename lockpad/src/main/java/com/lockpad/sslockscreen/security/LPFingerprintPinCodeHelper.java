package com.lockpad.sslockscreen.security;

import android.content.Context;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import com.lockpad.sslockscreen.security.callbacks.ILPPinCodeHelper;
import com.lockpad.sslockscreen.security.callbacks.IPFSecurityUtils;
import com.lockpad.sslockscreen.security.callbacks.LPPinCodeHelperCallback;

public class LPFingerprintPinCodeHelper implements ILPPinCodeHelper {


    private static final String FINGERPRINT_ALIAS = "fp_fingerprint_lock_screen_key_store";
    private static final String PIN_ALIAS = "fp_pin_lock_screen_key_store";

    private static final LPFingerprintPinCodeHelper ourInstance = new LPFingerprintPinCodeHelper();

    public static LPFingerprintPinCodeHelper getInstance() {
        return ourInstance;
    }

    private final IPFSecurityUtils pfSecurityUtils
            = LPSecurityUtilsFactory.getPFSecurityUtilsInstance();

    private LPFingerprintPinCodeHelper() {

    }

    @Override
    public void encodePin(Context context, String pin, LPPinCodeHelperCallback<String> callback) {
        try {
            final String encoded = pfSecurityUtils.encode(context, PIN_ALIAS, pin, false);
            if (callback != null) {
                callback.onResult(new LPResult(encoded));
            }
        } catch (LPSecurityException e) {
            if (callback != null) {
                callback.onResult(new LPResult(e.getError()));
            }
        }
    }

    @Override
    public void checkPin(Context context, String encodedPin, String pin, LPPinCodeHelperCallback<Boolean> callback) {
        try {
            final String pinCode = pfSecurityUtils.decode(PIN_ALIAS, encodedPin);
            if (callback != null) {
                callback.onResult(new LPResult(pinCode.equals(pin)));
            }
        } catch (LPSecurityException e) {
            if (callback != null) {
                callback.onResult(new LPResult(e.getError()));
            }
        }
    }


    private boolean isFingerPrintAvailable(Context context) {
        return FingerprintManagerCompat.from(context).isHardwareDetected();
    }

    private boolean isFingerPrintReady(Context context) {
        return FingerprintManagerCompat.from(context).hasEnrolledFingerprints();
    }

    @Override
    public void delete(LPPinCodeHelperCallback<Boolean> callback) {
        try {
            pfSecurityUtils.deleteKey(PIN_ALIAS);
            if (callback != null) {
                callback.onResult(new LPResult(true));
            }
        } catch (LPSecurityException e) {
            if (callback != null) {
                callback.onResult(new LPResult(e.getError()));
            }
        }
    }

    @Override
    public void isPinCodeEncryptionKeyExist(LPPinCodeHelperCallback<Boolean> callback) {
        try {
            final boolean isExist = pfSecurityUtils.isKeystoreContainAlias(PIN_ALIAS);
            if (callback != null) {
                callback.onResult(new LPResult(isExist));
            }
        } catch (LPSecurityException e) {
            if (callback != null) {
                callback.onResult(new LPResult(e.getError()));
            }
        }
    }

}
