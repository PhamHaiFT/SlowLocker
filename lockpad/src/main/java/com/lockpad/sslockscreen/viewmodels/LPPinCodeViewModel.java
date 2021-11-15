package com.lockpad.sslockscreen.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import android.content.Context;

import com.lockpad.sslockscreen.security.LPSecurityManager;
import com.lockpad.sslockscreen.security.LPResult;
import com.lockpad.sslockscreen.security.callbacks.LPPinCodeHelperCallback;
import com.lockpad.sslockscreen.security.livedata.LPLiveData;


public class LPPinCodeViewModel extends ViewModel {

    public LiveData<LPResult<String>> encodePin(Context context, String pin) {
        final LPLiveData<LPResult<String>> liveData = new LPLiveData<>();
        LPSecurityManager.getInstance().getPinCodeHelper().encodePin(
                context,
                pin,
                new LPPinCodeHelperCallback<String>() {
                    @Override
                    public void onResult(LPResult<String> result) {
                        liveData.setData(result);
                    }
                }
        );
        return liveData;
    }

    public LiveData<LPResult<Boolean>> checkPin(Context context, String encodedPin, String pin) {
        final LPLiveData<LPResult<Boolean>> liveData = new LPLiveData<>();
        LPSecurityManager.getInstance().getPinCodeHelper().checkPin(
                context,
                encodedPin,
                pin,
                new LPPinCodeHelperCallback<Boolean>() {
                    @Override
                    public void onResult(LPResult<Boolean> result) {
                        liveData.setData(result);
                    }
                }
        );
        return liveData;
    }

    public LiveData<LPResult<Boolean>> delete() {
        final LPLiveData<LPResult<Boolean>> liveData = new LPLiveData<>();
        LPSecurityManager.getInstance().getPinCodeHelper().delete(
                new LPPinCodeHelperCallback<Boolean>() {
                    @Override
                    public void onResult(LPResult<Boolean> result) {
                        liveData.setData(result);
                    }
                }
        );
        return liveData;
    }

    public LiveData<LPResult<Boolean>> isPinCodeEncryptionKeyExist() {
        final LPLiveData<LPResult<Boolean>> liveData = new LPLiveData<>();
        LPSecurityManager.getInstance().getPinCodeHelper().isPinCodeEncryptionKeyExist(
                new LPPinCodeHelperCallback<Boolean>() {
                    @Override
                    public void onResult(LPResult<Boolean> result) {
                        liveData.setData(result);
                    }
                }
        );
        return liveData;
    }

}
