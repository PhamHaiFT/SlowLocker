package com.superslow.locker.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.beautycoder.pflockscreen.PFFLockScreenConfiguration;
import com.beautycoder.pflockscreen.fragments.CustomLockScreenFragment;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.superslow.locker.R;
import com.superslow.locker.burst.BurstParticleSystem;
import com.superslow.locker.burst.MyTextureAtlasFactory;
import com.superslow.locker.spark.SparkView;
import com.superslow.locker.task.ExecuteTask;
import com.superslow.locker.task.ExecuteTaskManager;
import com.superslow.locker.util.DateUtils;
import com.superslow.locker.util.DimenUtils;
import com.superslow.locker.util.PowerUtil;
import com.superslow.locker.util.PrefManager;
import com.superslow.locker.util.ViewUtils;
import com.superslow.locker.widget.TouchPullDownView;
import com.superslow.locker.widget.TouchToUnLockView;
import com.github.shchurov.particleview.ParticleView;
import com.zyyoona7.lib.EasyPopup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;

public class SlowLockerActivity extends AppCompatActivity {

    private static final String TAG = "LockerActivity";
    private TouchPullDownView mPullDownView;
    private TouchToUnLockView mUnlockView;

    private View mChargeContainer, mSetting;

    private TextView mLockTime, mLockDate, mChargePercent;
    private ImageView mBatteryIcon;
    private ImageView imgPreview;

    private View mContainerView;
    private RelativeLayout lockView;
    private WindowManager windowManager;

    private Calendar calendar = GregorianCalendar.getInstance();
    private SimpleDateFormat weekFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMM d", Locale.getDefault());

    private ParticleView particleView;
    private BurstParticleSystem particleSystem;
    private Random random = new Random();

    private SparkView spSpark;
    private SparkTask sparkTask;

    private boolean passwordState;
    private boolean sparkState;
    private boolean particleState;
    private boolean titleState;
    private boolean titleAnimationState;
    private String title;

    private Handler handler = new Handler();
    @BindView(R.id.tvTitle)
    ShimmerTextView tvTitle;
    private PFFLockScreenConfiguration.Builder builder;
    private CustomLockScreenFragment lockScreenFragment;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLockerWindow(getWindow());
        registerLockerReceiver();
//        getSystemUiVisibility();
        setContentView(R.layout.activity_locker);

        ButterKnife.bind(this);
        initView();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasAlertPermission() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterLockerReceiver();
    }

    private EasyPopup mCirclePop;

    private void initView() {
        lockView = ViewUtils.get(this, R.id.lockView);

        mCirclePop = new EasyPopup(this)
                .setContentView(R.layout.locker_setting_item)
                .setFocusAndOutsideEnable(true)
                .setBackgroundDimEnable(true)
                .setDimValue(0.4f)
                .setDimColor(getResources().getColor(R.color.common_half_alpha))
                .setDimView(mUnlockView)
                .createPopup();
        mCirclePop.getView(R.id.txtv_LockerSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mCirclePop.getView(R.id.tv_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSetting();
            }
        });
        mSetting = ViewUtils.get(this, R.id.settings);
        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCirclePop.showAsDropDown(mSetting);
            }
        });

        mChargeContainer = ViewUtils.get(this, R.id.llChargeContainer);
        mContainerView = ViewUtils.get(this, R.id.rlContentContainer);

        mLockTime = ViewUtils.get(this, R.id.tvLockTime);
        mLockDate = ViewUtils.get(this, R.id.tvLockDate);
        mBatteryIcon = ViewUtils.get(this, R.id.imgBattery);
        mChargePercent = ViewUtils.get(this, R.id.tvChargePercent);

        initUnlockedView();

        passwordState = PrefManager.getPasswordState(getApplicationContext());
        sparkState = PrefManager.getSparkState(getApplicationContext());
        particleState = PrefManager.getParticleState(getApplicationContext());
        titleState = PrefManager.getTitleState(getApplicationContext());
        titleAnimationState = PrefManager.getTitleAnimationState(getApplicationContext());

        spSpark = ViewUtils.get(this, R.id.sparkView);

        if (passwordState) {
            initPassword();
        }

        if (sparkState) {
            initSpark();
        } else {
            spSpark.setVisibility(View.GONE);
        }

        mPullDownView = ViewUtils.get(this, R.id.pullDownViewLocker);
        initPullDownView();
        if (particleState) {
            initParticle();
        } else {
        }

        updateTimeUI();
        updateBatteryUI();

        if (titleState) {
            tvTitle.setVisibility(View.VISIBLE);
            title = PrefManager.getTitle(getApplicationContext());
            tvTitle.setText(title);
        } else {
            tvTitle.setVisibility(View.GONE);
        }

        if (titleAnimationState) {
            Shimmer shimmer = new Shimmer();
            shimmer.start(tvTitle);
        } else {
        }

        imgPreview = ViewUtils.get(this, R.id.imgPreview);
        Bitmap bitmap = PrefManager.getCurrentPic(getApplicationContext());
        imgPreview.setImageBitmap(null);
        if (bitmap != null) {
            imgPreview.setImageBitmap(bitmap);
        } else {
            imgPreview.setBackgroundResource(R.drawable.bg_seven);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        }

        return false;
    }

    private void initPassword() {
        builder = new PFFLockScreenConfiguration.Builder(this)
                .setMode(PFFLockScreenConfiguration.MODE_AUTH)
                .setTitle("Enter pin code")
                .setCodeLength(4);

        lockScreenFragment = new CustomLockScreenFragment();

        lockScreenFragment.setEncodedPinCode(PrefManager.getPassword(getApplicationContext()));
        lockScreenFragment.setLoginListener(new CustomLockScreenFragment.OnPFLockScreenLoginListener() {
            @Override
            public void onCodeInputSuccessful() {
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction().remove(lockScreenFragment).commit();
                finish();
            }

            @Override
            public void onFingerprintSuccessful() {

            }

            @Override
            public void onPinLoginFailed() {
                Toast.makeText(getApplicationContext(), "Code invalid", Toast.LENGTH_SHORT).show();
                lockScreenFragment.clearAllPinCode();
            }

            @Override
            public void onFingerprintLoginFailed() {
                Toast.makeText(getApplicationContext(), "Fingerprint invalid", Toast.LENGTH_SHORT).show();
                lockScreenFragment.clearAllPinCode();
            }
        });
        lockScreenFragment.setConfiguration(builder.build());
    }

    private void initUnlockedView() {
        mUnlockView = ViewUtils.get(this, R.id.touchUnlockView);
        mUnlockView.setOnTouchToUnlockListener(new TouchToUnLockView.OnTouchToUnlockListener() {
            @Override
            public void onTouchLockArea() {
                if (mContainerView != null) {
                    mContainerView.setBackgroundColor(Color.parseColor("#66000000"));
                }
            }

            @Override
            public void onSlidePercent(float percent) {
                if (mContainerView != null) {
                    mContainerView.setAlpha(1 - percent < 0.05f ? 0.05f : 1 - percent);
                    mContainerView.setScaleX(1 + (percent > 1f ? 1f : percent) * 0.08f);
                    mContainerView.setScaleY(1 + (percent > 1f ? 1f : percent) * 0.08f);
                }
            }

            @Override
            public void onSlideToUnlock() {
                if (passwordState) {
                    showPinCode();
                } else {
                    finish();
                }
            }

            @Override
            public void onSlideAbort() {
                if (mContainerView != null) {
                    mContainerView.setAlpha(1.0f);
                    mContainerView.setBackgroundColor(0);
                    mContainerView.setScaleX(1f);
                    mContainerView.setScaleY(1f);
                }
            }
        });
    }

    private void showPinCode() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, lockScreenFragment).commit();
    }

    //PullDown to burst particle
    private void initPullDownView() {
        mPullDownView.setOnTouchPullDownListener(new TouchPullDownView.OnTouchPullDownListener() {
            @Override
            public void onTouchGiftBoxArea() {
            }

            @Override
            public void onPullPercent(float percent) {
            }

            @Override
            public void onPullCanceled() {
            }

            @Override
            public void onGiftBoxPulled() {
                if (particleState) {
                    particleSystem.addBurst(random.nextInt(DimenUtils.getScreenWidth(getBaseContext())),
                            random.nextInt(DimenUtils.getScreenHeight(getBaseContext())));
                } else {
                    Toast.makeText(getApplicationContext(), "Please enable burst particle in setting", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onGiftBoxClick() {
                if (particleState) {
                    particleSystem.addBurst(random.nextInt(DimenUtils.getScreenWidth(getBaseContext())),
                            random.nextInt(DimenUtils.getScreenHeight(getBaseContext())));
                } else {
                    Toast.makeText(getApplicationContext(), "Please enable burst particle in setting", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initSpark() {
        sparkTask = new SparkTask();
    }

    private void initParticle() {
        particleView = ViewUtils.get(this, R.id.particleViewLocker);
        particleSystem = new BurstParticleSystem();
        particleView.setTextureAtlasFactory(new MyTextureAtlasFactory(getResources()));
        particleView.setParticleSystem(particleSystem);
    }

    private void goToSetting() {
        Intent intent = new Intent(SlowLockerActivity.this, SettingActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLockScreen() {
        windowManager = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        windowManager.addView(lockView, getLayoutParams());
    }

    public void unlockScreen() {
        windowManager.removeView(lockView);
    }

    public void onBackPressed() {
        return;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    protected UIChangingReceiver mUIChangingReceiver;

    public void registerLockerReceiver() {
        if (mUIChangingReceiver != null) {
            return;
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        mUIChangingReceiver = new UIChangingReceiver();
        registerReceiver(mUIChangingReceiver, filter);
    }

    public void unregisterLockerReceiver() {
        if (mUIChangingReceiver == null) {
            return;
        }
        unregisterReceiver(mUIChangingReceiver);
        mUIChangingReceiver = null;
    }


    private class UIChangingReceiver extends BroadcastReceiver {

        public UIChangingReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                onActionReceived(action);
            }
        }
    }

    private void updateTimeUI() {
        mLockTime.setText(DateUtils.getHourString(this, System.currentTimeMillis()));
        mLockDate.setText(weekFormat.format(calendar.getTime()) + "    " + monthFormat.format(calendar.getTime()));
    }


    private void updateBatteryUI() {
        int level = PowerUtil.getLevel(this);
        mChargePercent.setText(level + "%");

        if (level <= 30) {
            mBatteryIcon.setImageResource(R.drawable.lock_battery_charging_30);
        } else if (level <= 60) {
            mBatteryIcon.setImageResource(R.drawable.lock_battery_charging_60);
        } else if (level < 100) {
            mBatteryIcon.setImageResource(R.drawable.lock_battery_charging_90);
        } else if (level == 100) {
            mBatteryIcon.setImageResource(R.drawable.ic_lock_charge_four);
        }

        if (level < 100 && mBatteryIcon.getDrawable() instanceof Animatable) {
            Animatable animatable = (Animatable) mBatteryIcon.getDrawable();
            if (PowerUtil.isCharging(this)) {
                animatable.start();
            } else {
                animatable.stop();
            }
        }
    }


    protected void onActionReceived(String action) {
        if (!TextUtils.isEmpty(action)) {
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                updateBatteryUI();
            } else if (action.equals(Intent.ACTION_TIME_TICK)) {
                updateTimeUI();
            } else if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
                mChargeContainer.setVisibility(View.VISIBLE);
            } else if (action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
                mChargeContainer.setVisibility(View.GONE);
            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mUnlockView.startAnim();
        if (particleState) {
            particleView.startRendering();
        }
        if (sparkState) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ExecuteTaskManager.getInstance().newExecuteTask(sparkTask);
                }
            }, 500);
        }
    }

    private class SparkTask extends ExecuteTask {
        @Override
        public ExecuteTask doTask() {
            if (sparkState) {
                for (int i = 0; i < SparkView.WIDTH; i++) {
                    spSpark.setActive(true);
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

    @Override
    public void onPause() {
        super.onPause();
        mUnlockView.stopAnim();
        if (sparkState) {
            ExecuteTaskManager.getInstance().removeExecuteTask(sparkTask);
        }

        if (particleState) {
            particleView.stopRendering();
        }
    }

    public static void startActivity(Context context) {
        Intent screenIntent = getIntent(context);
        context.startActivity(screenIntent);
    }

    @NonNull
    private static Intent getIntent(Context context) {
        Intent screenIntent = new Intent();
        screenIntent.setClass(context, SlowLockerActivity.class);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        return screenIntent;
    }

    public int getSystemUiVisibility() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    }

    private void setLockerWindow(Window window) {
        WindowManager.LayoutParams lp = window.getAttributes();
        if (Build.VERSION.SDK_INT > 18) {
            lp.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        }

        window.setAttributes(lp);
        window.getDecorView().setSystemUiVisibility(getSystemUiVisibility());
        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.setFormat(PixelFormat.TRANSLUCENT);
    }

    private WindowManager.LayoutParams getLayoutParams() {
        return new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,
                0, 0, WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD, PixelFormat.TRANSLUCENT);
    }
}
