package com.lockpad.sslockscreen.actions;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import android.view.View;

import com.lockpad.sslockscreen.views.LPCodeView;

import org.hamcrest.Matcher;

import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;

public class PFCodeViewActionInput implements ViewAction {

    private int mCode;

    public PFCodeViewActionInput(int code) {
        mCode = code;
    }

    @Override
    public Matcher<View> getConstraints(){
        return isAssignableFrom(LPCodeView.class);
    }


    @Override
    public String getDescription(){
        return "Input code " + mCode;
    }

    @Override
    public void perform(UiController uiController, View view){
        LPCodeView codeView = (LPCodeView) view;
        codeView.input(String.valueOf(mCode));
    }

}
