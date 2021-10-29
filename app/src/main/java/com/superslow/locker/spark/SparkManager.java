package com.superslow.locker.spark;

import java.util.Random;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class SparkManager {


    private Paint mSparkPaint;
    private int X, Y;
    private float radius = 0;
    private float mDistance = 0;
    private float mCurDistance = 0;
    private static final float SPARK_RADIUS = 4.0F;
    private static final float BLUR_SIZE = 9.0F;
    private static final float PER_SPEED_SEC = 1.0F;
    private Random mRandom = new Random();
    private Point startPoint, endPoint, curve1, curve2;
    public boolean isActive = false;

    public SparkManager() {
        initSparkPaint();
    }

    public int[] drawSpark(Canvas canvas, int x, int y, int[] store) {

        this.X = x;
        this.Y = y;
        this.mCurDistance = store[0];
        this.mDistance = store[1];

        // tạo spark
        if (mCurDistance == mDistance && isActive) {
            mDistance = getRandom(SparkView.WIDTH / 2, mRandom.nextInt(15)) + 1;
            mCurDistance = 0;

            startPoint = new Point(X, Y);
            endPoint = getRandomPoint(startPoint.x, startPoint.y, (int) mDistance);
            curve1 = getRandomPoint(startPoint.x, startPoint.y, mRandom.nextInt(SparkView.WIDTH / 16));
            curve2 = getRandomPoint(endPoint.x, endPoint.y, mRandom.nextInt(SparkView.WIDTH / 16));
        }

        // cập nhật nếu đã có trong store
        else {
            startPoint.set(store[2], store[3]);
            endPoint.set(store[4], store[5]);
            curve1.set(store[6], store[7]);
            curve2.set(store[8], store[9]);
        }

        updateSparkPath();

        Point bezierPoint = calculateBezierPoint(mCurDistance / mDistance, startPoint, curve1, curve2, endPoint);
        mSparkPaint.setColor(Color.argb(mRandom.nextInt(128) + 128, mRandom.nextInt(128) + 128, mRandom.nextInt(128) + 128, mRandom.nextInt(128) + 128));
        canvas.drawCircle(bezierPoint.x, bezierPoint.y, radius, mSparkPaint);


        // reset
        if (mCurDistance == mDistance) {
            store[0] = 0;
            store[1] = 0;
        }

        // Lưu spark
        else {
            store[0] = (int) mCurDistance;
            store[1] = (int) mDistance;
            store[2] = (int) startPoint.x;
            store[3] = (int) startPoint.y;
            store[4] = (int) endPoint.x;
            store[5] = (int) endPoint.y;
            store[6] = (int) curve1.x;
            store[7] = (int) curve1.y;
            store[8] = (int) curve2.x;
            store[9] = (int) curve2.y;
        }

        return store;
    }

    /**
     * update spark
     */
    private void updateSparkPath() {
        mCurDistance += PER_SPEED_SEC;
        if (mCurDistance < (mDistance / 2) && (mCurDistance != 0)) {
            radius = SPARK_RADIUS * (mCurDistance / (mDistance / 2));
        }
        else if (mCurDistance > (mDistance / 2) && (mCurDistance < mDistance)) {
            radius = SPARK_RADIUS - SPARK_RADIUS * ((mCurDistance / (mDistance / 2)) - 1);
        }
        else if (mCurDistance >= mDistance) {
            mCurDistance = 0;
            mDistance = 0;
            radius = 0;
        }
    }

    /**
     * Lấy tọa độ ngẫu nhiên
     */
    private Point getRandomPoint(int baseX, int baseY, int r) {
        if (r <= 0) {
            r = 1;
        }
        int x = mRandom.nextInt(r);
        int y = (int) Math.sqrt(r * r - x * x);

        x = baseX + getRandomPNValue(x);
        y = baseY + getRandomPNValue(y);

        return new Point(x, y);
    }

    private int getRandom(int range, int chance) {
        int num;
        switch (chance) {
            case 0:
                num = mRandom.nextInt(range);
                break;
            default:
                num = mRandom.nextInt(range / 4);
                break;
        }

        return num;
    }

    private int getRandomPNValue(int value) {
        return mRandom.nextBoolean() ? value : 0 - value;
    }

    /**
     * Tính toán đường cong
     *
     * @param time  thời gian từ 0-1
     * @param start  điểm bắt đầu
     * @param curve1 đường cong 1
     * @param curve2 đường cong 2
     * @param end  điểm kết thúc
     * @return đường cong kết quả
     */
    private Point calculateBezierPoint(float time, Point start, Point curve1, Point curve2, Point end) {
        float u = 1 - time;
        float tt = time * time;
        float uu = u * u;
        float uuu = uu * u;
        float ttt = tt * time;

        Point p = new Point((int) (start.x * uuu), (int) (start.y * uuu));
        p.x += 3 * uu * time * curve1.x;
        p.y += 3 * uu * time * curve1.y;
        p.x += 3 * u * tt * curve2.x;
        p.y += 3 * u * tt * curve2.y;
        p.x += ttt * end.x;
        p.y += ttt * end.y;

        return p;
    }

    private void initSparkPaint() {
        this.mSparkPaint = new Paint();
        // Bật Khử răng cưa
        this.mSparkPaint.setAntiAlias(true);
        this.mSparkPaint.setDither(true);
        this.mSparkPaint.setStyle(Paint.Style.FILL);
        // Hiệu ứng
        this.mSparkPaint.setMaskFilter(new BlurMaskFilter(BLUR_SIZE, BlurMaskFilter.Blur.SOLID));
    }
}
