package com.superslow.locker.activity;

import android.app.Activity;
import android.os.Bundle;

import com.superslow.locker.spark.SparkView;
import com.superslow.locker.util.PrefManager;


public class SparkActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean sparkState = PrefManager.getSparkState(getApplicationContext());
        SparkView sparkView = new SparkView(this);
        setContentView(sparkView);
    }
}
