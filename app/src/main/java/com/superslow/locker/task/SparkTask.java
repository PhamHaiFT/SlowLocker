package com.superslow.locker.task;

import com.superslow.locker.spark.SparkView;

import java.util.Random;

public class SparkTask extends ExecuteTask {

    private boolean sparkState;
    private SparkView spSpark;

    public SparkTask(boolean sparkState, SparkView sparkView) {
        this.sparkState = sparkState;
        this.spSpark = sparkView;
    }

    @Override
    public ExecuteTask doTask() {
        if (sparkState) {
            for (int i = 0; i < SparkView.WIDTH; i++) {
                spSpark.setActive(true);
                Random random = new Random();
                spSpark.startSpark(i, random.nextInt(SparkView.HEIGHT));
                try {
                    Thread.sleep(2 + random.nextInt(8));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                spSpark.setActive(false);
            }
        }
        return null;
    }
}
