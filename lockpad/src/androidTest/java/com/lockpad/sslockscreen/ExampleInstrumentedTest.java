package com.lockpad.sslockscreen;

import android.content.Context;
import androidx.test.InstrumentationRegistry;

import org.junit.Test;

import static org.junit.Assert.*;

public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.beautycoder.lockpad.test", appContext.getPackageName());
    }
}
