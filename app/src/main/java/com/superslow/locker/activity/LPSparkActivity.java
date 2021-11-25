package com.superslow.locker.activity;

import android.app.Activity;
import android.os.Bundle;

import com.superslow.locker.spark.LPSparkView;
import com.superslow.locker.util.PrefManager;


public class LPSparkActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean sparkState = PrefManager.getSparkState(getApplicationContext());
        LPSparkView LPSparkView = new LPSparkView(this);
        setContentView(LPSparkView);
    }
}
