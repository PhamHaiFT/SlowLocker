package com.superslow.locker.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class PrefManager {
    private static final String TAG = "PrefManager";
    public static final String PREF_NAME = "PREF_NAME";
    public static final String TITLE_KEY = "TITLE_KEY";
    public static final String PASSWORD_STATE = "PASSWORD_STATE";
    public static final String PASSWORD = "PASSWORD";
    public static final String SPARK_STATE = "SPARK_STATE";
    public static final String PARTICLE_STATE = "PARTICLE_STATE";
    public static final String TITLE_STATE = "TITLE_STATE";
    public static final String TITLE_ANIM_STATE = "TITLE_ANIM_STATE";
    public static final String CURRENT_PIC = "CURRENT_PIC";

    public static void saveTitle(Context context, String title) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pref.edit().putString(TITLE_KEY, title).commit();
    }

    public static String getTitle(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String title = pref.getString(TITLE_KEY, "Welcome back");
        return title;
    }

    public static void savePasswordState(Context context, boolean isEnable) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pref.edit().putBoolean(PASSWORD_STATE, isEnable).commit();
    }

    public static boolean getPasswordState(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean state = pref.getBoolean(PASSWORD_STATE, false);
        return state;
    }

    public static void savePassword(Context context, String password) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pref.edit().putString(PASSWORD, password).commit();
    }

    public static String getPassword(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String pass = pref.getString(PASSWORD, null);
        return pass;
    }

    public static void saveSparkState(Context context, boolean isEnable) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pref.edit().putBoolean(SPARK_STATE, isEnable).commit();
    }

    public static boolean getSparkState(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean state = pref.getBoolean(SPARK_STATE, true);
        return state;
    }

    public static void saveParticleState(Context context, boolean isEnable) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pref.edit().putBoolean(PARTICLE_STATE, isEnable).commit();
    }

    public static boolean getParticleState(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean state = pref.getBoolean(PARTICLE_STATE, true);
        return state;
    }

    public static void saveTitleState(Context context, boolean isEnable) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pref.edit().putBoolean(TITLE_STATE, isEnable).commit();
    }

    public static boolean getTitleState(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean state = pref.getBoolean(TITLE_STATE, true);
        return state;
    }

    public static void saveTitleAnimationState(Context context, boolean isEnable) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pref.edit().putBoolean(TITLE_ANIM_STATE, isEnable).commit();
    }

    public static boolean getTitleAnimationState(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean state = pref.getBoolean(TITLE_ANIM_STATE, true);
        return state;
    }

    public static void saveCurrentPic(Context context, Bitmap image) {
        String encode;
        if (image != null) {
            ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayInputStream);
            byte[] b = byteArrayInputStream.toByteArray();
            encode = Base64.encodeToString(b, Base64.DEFAULT);

        } else {
            encode = null;
        }
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pref.edit().putString(CURRENT_PIC, encode).commit();
    }

    public static Bitmap getCurrentPic(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String encode = pref.getString(CURRENT_PIC, null);
        Bitmap bitmap = null;
        if (encode != null) {
            byte[] decode = Base64.decode(encode.getBytes(), Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
        } else {
        }
        return bitmap;
    }
}
