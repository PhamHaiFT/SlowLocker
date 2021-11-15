package com.lockpad.sslockscreen.security.callbacks;

import android.content.Context;
import androidx.annotation.NonNull;

import com.lockpad.sslockscreen.security.LPSecurityException;

public interface IPFSecurityUtils {

    String encode(@NonNull Context context, String alias, String input, boolean isAuthorizationRequared)
            throws LPSecurityException;

    String decode(String alias, String encodedString) throws LPSecurityException;

    boolean isKeystoreContainAlias(String alias) throws LPSecurityException;

    void deleteKey(String alias) throws LPSecurityException;

}
