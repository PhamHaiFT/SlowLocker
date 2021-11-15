package com.lockpad.sslockscreen;

import android.content.Context;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;

import com.lockpad.sslockscreen.actions.PFCodeViewActionDelete;
import com.lockpad.sslockscreen.actions.PFCodeViewActionInput;
import com.lockpad.sslockscreen.matchers.PFViewMatchers;
import com.lockpad.sslockscreen.rules.ViewTestRule;
import com.lockpad.sslockscreen.views.LPCodeView;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class LPCodeViewTest {

    private static final int CODE_LENGTH = 5;

    @Rule
    public ViewTestRule<LPCodeView> mViewTestRule
            = new ViewTestRule<LPCodeView>(LPCodeView.class) {
        @Override
        public LPCodeView getView(Context context) {
            LPCodeView view = new LPCodeView(context);
            view.setCodeLength(CODE_LENGTH);
            view.setId(R.id.code_view);
            return view;
        }
    };

    @Test
    public void pfCodeViewTest() {

        mViewTestRule.launchActivity(null);

        Espresso.onView(withId(R.id.code_view)).check(ViewAssertions.matches(
                PFViewMatchers.withCodeValue("")));

        for (int i = 0; i < CODE_LENGTH * 2; i ++) {
            Espresso.onView(withId(R.id.code_view)).perform(new PFCodeViewActionInput(i));
        }

        Espresso.onView(withId(R.id.code_view)).check(ViewAssertions.matches(
                PFViewMatchers.withCodeValue("01234")));

        Espresso.onView(withId(R.id.code_view)).check(ViewAssertions.matches(
                PFViewMatchers.withCodeLength(CODE_LENGTH)));

        Espresso.onView(withId(R.id.code_view)).perform(new PFCodeViewActionDelete());

        Espresso.onView(withId(R.id.code_view)).check(ViewAssertions.matches(
                PFViewMatchers.withCodeValue("0123")));

        Espresso.onView(withId(R.id.code_view)).check(ViewAssertions.matches(
                PFViewMatchers.withCodeLength(CODE_LENGTH - 1)));

        for (int i = 0; i < CODE_LENGTH * 3; i ++) {
            Espresso.onView(withId(R.id.code_view)).perform(new PFCodeViewActionDelete());
        }

        Espresso.onView(withId(R.id.code_view)).check(ViewAssertions.matches(
                PFViewMatchers.withCodeLength(0)));

        Espresso.onView(withId(R.id.code_view)).check(ViewAssertions.matches(
                PFViewMatchers.withCodeValue("")));


    }

}
