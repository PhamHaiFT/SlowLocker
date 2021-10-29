package com.beautycoder.pflockscreen;

import android.content.Context;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.runner.AndroidJUnit4;
import android.view.View;

import com.beautycoder.pflockscreen.fragments.CustomLockScreenFragment;
import com.beautycoder.pflockscreen.rules.FragmentTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CustomLockScreenFragmentAuthTest {

    private static final String LEFT_BUTTON = "Can't remember";
    private static final int CODE_LENGTH = 5;

    @Rule
    public FragmentTestRule<CustomLockScreenFragment> mFragmentTestRule
            = new FragmentTestRule<CustomLockScreenFragment>(CustomLockScreenFragment.class) {
        @Override
        public CustomLockScreenFragment getInstance(Context context) {
            final CustomLockScreenFragment fragment = new CustomLockScreenFragment();
            final PFFLockScreenConfiguration.Builder builder =
                    new PFFLockScreenConfiguration.Builder(context)
                            .setCodeLength(CODE_LENGTH)
                            .setLeftButton(LEFT_BUTTON)
                            .setUseFingerprint(true)
                            .setMode(PFFLockScreenConfiguration.MODE_AUTH);
            fragment.setConfiguration(builder.build());
            return fragment;
        }
    };


    @Test
    public void fragment_can_be_instantiated() {

        // Launch the activity to make the fragment visible
        mFragmentTestRule.launchActivity(null);

        // Then use Espresso to test the Fragment
        Espresso.onView(withId(R.id.fragment_pf)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.button_finger_print)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.button_delete)).check(matches(not(isDisplayed())));
        Espresso.onView(withId(R.id.button_left)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.button_next)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));

        Espresso.onView(withId(R.id.button_left)).check(matches(withText(LEFT_BUTTON)));


        for (int i = 0; i < CODE_LENGTH; i++) {
            Espresso.onView(withId(R.id.button_1)).perform(click());
            Espresso.onView(withId(R.id.button_finger_print)).check(matches(not(isDisplayed())));
            Espresso.onView(withId(R.id.button_delete)).check(matches(isDisplayed()));
        }

        for (int i = 0; i < CODE_LENGTH - 1; i++) {
            Espresso.onView(withId(R.id.button_delete)).perform(click());
            Espresso.onView(withId(R.id.button_delete)).check(matches(isDisplayed()));
            Espresso.onView(withId(R.id.button_finger_print)).check(matches(not(isDisplayed())));
        }

        Espresso.onView(withId(R.id.button_delete)).perform(click());
        Espresso.onView(withId(R.id.button_finger_print)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.button_delete)).check(matches(not(isDisplayed())));


        Espresso.onView(withId(R.id.button_next)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));


    }


}
