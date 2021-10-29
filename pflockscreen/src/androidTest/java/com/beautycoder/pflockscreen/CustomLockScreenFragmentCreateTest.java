package com.beautycoder.pflockscreen;

import android.content.Context;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.runner.AndroidJUnit4;

import com.beautycoder.pflockscreen.fragments.CustomLockScreenFragment;
import com.beautycoder.pflockscreen.rules.FragmentTestRule;
import com.beautycoder.pflockscreen.security.PFFingerprintPinCodeHelper;
import com.beautycoder.pflockscreen.security.PFResult;
import com.beautycoder.pflockscreen.security.callbacks.PFPinCodeHelperCallback;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CustomLockScreenFragmentCreateTest {

    private static final int CODE_LENGTH = 5;

    @Rule
    public FragmentTestRule<CustomLockScreenFragment> mFragmentTestRule
            = new FragmentTestRule<CustomLockScreenFragment>(CustomLockScreenFragment.class) {
        @Override
        public CustomLockScreenFragment getInstance(Context context) {
            CustomLockScreenFragment fragment = new CustomLockScreenFragment();
            PFFLockScreenConfiguration.Builder builder = new PFFLockScreenConfiguration.Builder(context)
                    .setTitle("Unlock with your pin code")
                    .setCodeLength(CODE_LENGTH)
                    .setUseFingerprint(true)
                    .setMode(PFFLockScreenConfiguration.MODE_CREATE);
            fragment.setConfiguration(builder.build());
            return fragment;
        }
    };


    @Test
    public void fragment_can_be_instantiated() {
        PFFingerprintPinCodeHelper.getInstance().delete(null);
        PFFingerprintPinCodeHelper.getInstance().isPinCodeEncryptionKeyExist(new PFPinCodeHelperCallback<Boolean>() {
            @Override
            public void onResult(PFResult<Boolean> result) {
                assertNull(result.getError());
                assertFalse(result.getResult());
            }
        });


        // Launch the activity to make the fragment visible
        mFragmentTestRule.launchActivity(null);

        // Then use Espresso to test the Fragment
        Espresso.onView(withId(R.id.fragment_pf)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.button_finger_print)).check(matches(not(isDisplayed())));
        Espresso.onView(withId(R.id.button_delete)).check(matches(not(isDisplayed())));
        Espresso.onView(withId(R.id.button_left)).check(matches(not(isDisplayed())));

        //INPUT CODE_LENGTH
        for (int i = 0; i < CODE_LENGTH; i ++) {
            Espresso.onView(withId(R.id.button_next)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
            Espresso.onView(withId(R.id.button_1)).perform(click());
            Espresso.onView(withId(R.id.button_delete)).check(matches(isDisplayed()));
        }

        //NEXT BUTTON SHOULD BE ON SCREEN
        Espresso.onView(withId(R.id.button_next)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        //DELETE WHOLE CODE
        for (int i = 0; i < CODE_LENGTH; i ++) {
            Espresso.onView(withId(R.id.button_delete)).check(matches(isDisplayed()));
            Espresso.onView(withId(R.id.button_delete)).perform(click());
            Espresso.onView(withId(R.id.button_next)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        }

        //DELETE BUTTON HAS TO BE HIDDEN
        Espresso.onView(withId(R.id.button_delete)).check(matches(not(isDisplayed())));

        //INPUT CODE_LENGTH
        for (int i = 0; i < CODE_LENGTH; i ++) {
            Espresso.onView(withId(R.id.button_next)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
            Espresso.onView(withId(R.id.button_1)).perform(click());
            Espresso.onView(withId(R.id.button_delete)).check(matches(isDisplayed()));
        }

        //NEXT BUTTON SHOULD BE ON SCREEN
        Espresso.onView(withId(R.id.button_next)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));


        Espresso.onView(withId(R.id.button_next)).perform(click());


        PFFingerprintPinCodeHelper.getInstance().isPinCodeEncryptionKeyExist(new PFPinCodeHelperCallback<Boolean>() {
            @Override
            public void onResult(PFResult<Boolean> result) {
                assertNull(result.getError());
                assertTrue(result.getResult());
            }
        });

    }


}
