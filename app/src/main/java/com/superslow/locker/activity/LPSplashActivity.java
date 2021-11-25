package com.superslow.locker.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.superslow.locker.R;

public class LPSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LPSlowLockerActivity.startActivity(this);
        finish();
    }
}
