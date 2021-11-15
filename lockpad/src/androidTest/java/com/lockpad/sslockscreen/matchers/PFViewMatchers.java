package com.lockpad.sslockscreen.matchers;

import androidx.test.espresso.matcher.BoundedMatcher;
import android.view.View;

import com.lockpad.sslockscreen.views.LPCodeView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Created by aleksandr on 2018/03/10.
 */

public class PFViewMatchers {

    public static Matcher<View> withCodeLength(final int expectedLength) {
        return new BoundedMatcher<View, LPCodeView>(LPCodeView.class) {
            @Override
            protected boolean matchesSafely(LPCodeView item) {
                return item.getInputCodeLength() == expectedLength;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Checking the matcher on received view: ");
                description.appendText("with expectedLength=" + expectedLength);
            }
        };
    }


    public static Matcher<View> withCodeValue(final String expectedValue) {
        return new BoundedMatcher<View, LPCodeView>(LPCodeView.class) {
            @Override
            protected boolean matchesSafely(LPCodeView item) {
                return item.getCode().equals(expectedValue);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Checking the matcher on received view: ");
                description.appendText("with expectedValue=" + expectedValue);
            }
        };
    }



}
