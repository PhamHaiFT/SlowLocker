package com.superslow.locker.spark;

import java.util.Date;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class LPSparkView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder mHolder;

    private Canvas mCanvas;

    private boolean isRun;

    private LPSparkManager LPSparkManager;

    private double X, Y;

    public static int WIDTH, HEIGHT;

    private Random random = new Random();

    public LPSparkView(Context context) {
        super(context);
        init();
    }

    public LPSparkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LPSparkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public void init() {
        this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        DisplayMetrics metric = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metric);
        WIDTH = metric.widthPixels;
        HEIGHT = metric.heightPixels;

        LPSparkManager = new LPSparkManager();

        mHolder = this.getHolder();
        mHolder.addCallback(this);
    }


    @Override
    public void run() {

        int[][] sparks = new int[400][10];

        Date date;
        while (isRun) {
            date = new Date();
            try {
                mCanvas = mHolder.lockCanvas(null);
                if (mCanvas != null) {
                    synchronized (mHolder) {
                        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                        for (int[] n : sparks) {
                            n = LPSparkManager.drawSpark(mCanvas, (int) X, (int) Y, n);
                        }

                        Thread.sleep(Math.max(0, 10 - (new Date().getTime() - date.getTime())));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (mCanvas != null) {
                    mHolder.unlockCanvasAndPost(mCanvas);
                }
            }
        }
    }


    public void startSpark(float x, float y) {
        X = x;
        Y = y;
    }

    public void setActive(boolean isActive) {
        LPSparkManager.isActive = isActive;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getPointerCount()) {
            case 1:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        LPSparkManager.isActive = true;
                        X = event.getX();
                        Y = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        LPSparkManager.isActive = false;
                        break;
                    default:
                        break;
                }
                break;
        }

        return true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRun = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder argholder0) {
        isRun = false;
    }

    private void drawBackground(SurfaceHolder holder) {
        mCanvas = mHolder.lockCanvas();
        mCanvas.drawColor(Color.parseColor("#00000000"));
        mHolder.unlockCanvasAndPost(mCanvas);
    }
}
