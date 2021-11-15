package com.lockpad.sslockscreen.actions;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import android.view.View;

import com.lockpad.sslockscreen.views.LPCodeView;

import org.hamcrest.Matcher;

import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;

public class PFCodeViewActionDelete implements ViewAction {

    @Override
    public Matcher<View> getConstraints(){
        return isAssignableFrom(LPCodeView.class);
    }


    @Override
    public String getDescription(){
        return "Delete code";
    }

    @Override
    public void perform(UiController uiController, View view){
        LPCodeView codeView = (LPCodeView) view;
        codeView.delete();
    }

}
