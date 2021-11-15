package com.lockpad.sslockscreen;

import android.content.Context;
import androidx.test.InstrumentationRegistry;

import com.lockpad.sslockscreen.security.LPFingerprintPinCodeHelper;
import com.lockpad.sslockscreen.security.LPResult;
import com.lockpad.sslockscreen.security.LPSecurityUtils;
import com.lockpad.sslockscreen.security.LPSecurityUtilsOld;
import com.lockpad.sslockscreen.security.callbacks.LPPinCodeHelperCallback;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class LPSecurityUtilsTest {

    @Test
    public void pfSecurityUtils() throws Exception {
        // Context of the app under test.
        final String alias = "test_alias";
        final String pinCode = "1234";

        final Context appContext = InstrumentationRegistry.getTargetContext();

        LPSecurityUtils.getInstance().deleteKey(alias);

        final boolean isAliasFalse = LPSecurityUtils.getInstance().isKeystoreContainAlias(alias);
        assertFalse(isAliasFalse);

        final String encoded = LPSecurityUtils.getInstance().encode(null, alias, pinCode, false);
        assertNotNull(encoded);

        final boolean isAliasTrue = LPSecurityUtils.getInstance().isKeystoreContainAlias(alias);
        assertTrue(isAliasTrue);

        final String decoded = LPSecurityUtils.getInstance().decode(alias, encoded);
        assertEquals(decoded, pinCode);

        LPSecurityUtils.getInstance().deleteKey(alias);
        final boolean isAliasFalse2 = LPSecurityUtils.getInstance().isKeystoreContainAlias(alias);
        assertFalse(isAliasFalse2);
    }


    @Test
    public void pfSecurityUtilsOld() throws Exception {
        // Context of the app under test.
        final String alias = "test_alias_old";
        final String pinCode = "1234";

        final Context appContext = InstrumentationRegistry.getTargetContext();

        LPSecurityUtilsOld.getInstance().deleteKey(alias);

        final boolean isAliasFalse = LPSecurityUtilsOld.getInstance().isKeystoreContainAlias(alias);
        assertFalse(isAliasFalse);

        final String encoded = LPSecurityUtilsOld.getInstance().encode(appContext, alias, pinCode, false);
        assertNotNull(encoded);

        final boolean isAliasTrue = LPSecurityUtilsOld.getInstance().isKeystoreContainAlias(alias);
        assertTrue(isAliasTrue);

        final String decoded = LPSecurityUtilsOld.getInstance().decode(alias, encoded);
        assertEquals(decoded, pinCode);

        LPSecurityUtilsOld.getInstance().deleteKey(alias);
        final boolean isAliasFalse2 = LPSecurityUtilsOld.getInstance().isKeystoreContainAlias(alias);
        assertFalse(isAliasFalse2);
    }

    @Test
    public void pfFingerPrintPinCodeHelper() throws Exception {
        // Context of the app under test.
        final String pinCode = "1234";

        final Context appContext = InstrumentationRegistry.getTargetContext();

        LPFingerprintPinCodeHelper.getInstance().delete(new LPPinCodeHelperCallback<Boolean>() {
            @Override
            public void onResult(LPResult<Boolean> result) {
                assertNull(result.getError());
            }
        });

        LPFingerprintPinCodeHelper.getInstance().isPinCodeEncryptionKeyExist(
                new LPPinCodeHelperCallback<Boolean>() {
                    @Override
                    public void onResult(LPResult<Boolean> result) {
                        assertNull(result.getError());
                        assertFalse(result.getResult());
                    }
                });

        final StringBuilder stringBuilder = new StringBuilder();
        LPFingerprintPinCodeHelper.getInstance().encodePin(appContext, pinCode,
                new LPPinCodeHelperCallback<String>() {
            @Override
            public void onResult(LPResult<String> result) {
                assertNull(result.getError());
                final String encoded = result.getResult();
                stringBuilder.append(encoded);
                assertNotNull(encoded);
            }
        });

        LPFingerprintPinCodeHelper.getInstance().isPinCodeEncryptionKeyExist(
                new LPPinCodeHelperCallback<Boolean>() {
                    @Override
                    public void onResult(LPResult<Boolean> result) {
                        assertNull(result.getError());
                        assertTrue(result.getResult());
                    }
                }
        );

        final String encoded = stringBuilder.toString();
        LPFingerprintPinCodeHelper.getInstance().checkPin(appContext, encoded, pinCode,
                new LPPinCodeHelperCallback<Boolean>() {
            @Override
            public void onResult(LPResult<Boolean> result) {
                assertNull(result.getError());
                assertTrue(result.getResult());
            }
        });


        LPFingerprintPinCodeHelper.getInstance().checkPin(appContext, encoded, "1122",
                new LPPinCodeHelperCallback<Boolean>() {
            @Override
            public void onResult(LPResult<Boolean> result) {
                assertNull(result.getError());
                assertFalse(result.getResult());
            }
        });

        LPFingerprintPinCodeHelper.getInstance().delete(new LPPinCodeHelperCallback<Boolean>() {
            @Override
            public void onResult(LPResult<Boolean> result) {
                assertNull(result.getError());
            }
        });

        LPFingerprintPinCodeHelper.getInstance().isPinCodeEncryptionKeyExist(new LPPinCodeHelperCallback<Boolean>() {
            @Override
            public void onResult(LPResult<Boolean> result) {
                assertNull(result.getError());
                assertFalse(result.getResult());
            }
        });

    }

}
