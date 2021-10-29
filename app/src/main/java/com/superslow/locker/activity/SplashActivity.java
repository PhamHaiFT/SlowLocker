package com.superslow.locker.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.superslow.locker.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SlowLockerActivity.startActivity(this);
        finish();
    }
}
