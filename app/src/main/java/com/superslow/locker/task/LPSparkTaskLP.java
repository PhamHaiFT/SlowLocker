package com.superslow.locker.task;

import com.superslow.locker.spark.LPSparkView;

import java.util.Random;

public class LPSparkTaskLP extends LPExecuteTask {

    private boolean sparkState;
    private LPSparkView spSpark;

    public LPSparkTaskLP(boolean sparkState, LPSparkView LPSparkView) {
        this.sparkState = sparkState;
        this.spSpark = LPSparkView;
    }

    @Override
    public LPExecuteTask doTask() {
        if (sparkState) {
            for (int i = 0; i < LPSparkView.WIDTH; i++) {
                spSpark.setActive(true);
                Random random = new Random();
                spSpark.startSpark(i, random.nextInt(LPSparkView.HEIGHT));
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
