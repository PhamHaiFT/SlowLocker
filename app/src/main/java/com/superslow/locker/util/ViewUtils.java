package com.superslow.locker.util;

import android.app.Activity;
import android.view.View;

public class ViewUtils {

    @SuppressWarnings("unchecked")
    public static <T extends View> T get(View parent, int id) {
        if (parent == null) {
            return null;
        }
        return (T) parent.findViewById(id);
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T get(Activity activity, int id) {
        if (activity == null) {
            return null;
        }
        return (T) activity.findViewById(id);
    }
}
