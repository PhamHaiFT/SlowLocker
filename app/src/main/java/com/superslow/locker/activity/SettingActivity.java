package com.superslow.locker.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.beautycoder.pflockscreen.PFFLockScreenConfiguration;
import com.beautycoder.pflockscreen.fragments.CustomLockScreenFragment;
import com.beautycoder.pflockscreen.security.PFResult;
import com.beautycoder.pflockscreen.security.PFSecurityManager;
import com.beautycoder.pflockscreen.security.callbacks.PFPinCodeHelperCallback;
import com.beautycoder.pflockscreen.viewmodels.PFPinCodeViewModel;
import com.superslow.locker.R;
import com.superslow.locker.receiver.AdminReceiver;
import com.superslow.locker.util.BitmapUtil;
import com.superslow.locker.util.DevicePolicyUtil;
import com.superslow.locker.util.PrefManager;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener, Switch.OnCheckedChangeListener {

    private static final String TAG = "SettingActivity";
    @BindView(R.id.btnChoose)
    Button btnChoose;
    @BindView(R.id.imgPreview)
    ImageView imgPreview;

    @BindView(R.id.swPassword)
    Switch swPassword;
    @BindView(R.id.swSpark)
    Switch swSpark;
    @BindView(R.id.swBurstParticle)
    Switch swBurstParticle;
    @BindView(R.id.swTitle)
    Switch swTitle;
    @BindView(R.id.swTitleAnimation)
    Switch swTitleAnimation;

    @BindView(R.id.edtTitle)
    EditText edtTitle;

    @BindView(R.id.btnSaveTitle)
    Button btnSaveTitle;
    @BindView(R.id.btnDefault)
    Button btnDefault;
    @BindView(R.id.btnApply)
    Button btnApply;

    @BindView(R.id.rlChangeTitle)
    RelativeLayout rlChangeTitle;
    @BindView(R.id.rlTitleAnimation)
    RelativeLayout rlTitleAnimation;

    private final int CHOOSE_IMAGE = 1903;
    private final int REQUEST_ADMIN = 1904;
    private final int OVERLAY_PERMISSION_CODE = 1905;
    private Bitmap currentPic;
    private boolean isCodeExisted = false;
    private boolean askedForOverlayPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(getSystemUiVisibility());
        setContentView(R.layout.activity_setting);
//        getSupportActionBar().hide();
        ButterKnife.bind(this);

//        checkHasAdminActive();
        checkAlertPermission();
        initView();
    }

    private void checkAlertPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                askedForOverlayPermission = true;
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_CODE);
            }
        }
    }

    private void checkHasAdminActive() {
        if (DevicePolicyUtil.getInstance(getApplicationContext()).isAdminActive() == false) {
            Intent intent = new Intent(
                    DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    new ComponentName(getApplicationContext(), AdminReceiver.class));
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "LockScreen");
            startActivityForResult(intent, REQUEST_ADMIN);
        }
    }

    private void initView() {
        btnChoose.setOnClickListener(this);
        btnSaveTitle.setOnClickListener(this);
        btnDefault.setOnClickListener(this);
        btnApply.setOnClickListener(this);

//        swPassword.setOnCheckedChangeListener(this);
        swSpark.setOnCheckedChangeListener(this);
        swBurstParticle.setOnCheckedChangeListener(this);
        swTitle.setOnCheckedChangeListener(this);
        swTitleAnimation.setOnCheckedChangeListener(this);

        boolean sparkState = PrefManager.getSparkState(getApplicationContext());
        boolean particleState = PrefManager.getParticleState(getApplicationContext());
        boolean titleState = PrefManager.getTitleState(getApplicationContext());
        boolean titleAnimationState = PrefManager.getTitleAnimationState(getApplicationContext());
        updateSwitchView(swSpark, sparkState);
        updateSwitchView(swBurstParticle, particleState);
        updateSwitchView(swTitle, titleState);
        updateSwitchView(swTitleAnimation, titleAnimationState);

        String currentTitle = PrefManager.getTitle(getApplicationContext());
        edtTitle.setText(currentTitle);

        Bitmap currentPic = PrefManager.getCurrentPic(getApplicationContext());
        imgPreview.setImageBitmap(null);
        if (currentPic != null) {
            imgPreview.setImageBitmap(currentPic);
        } else {
            imgPreview.setImageResource(R.drawable.bg_six);
        }

        boolean passwordState = PrefManager.getPasswordState(getApplicationContext());
        updateSwitchView(swPassword, passwordState);
        swPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showLockScreenFragment();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChoose:
                choosePhotoFromGallery();
                break;
            case R.id.btnSaveTitle:
                saveNewTitle();
                break;
            case R.id.btnApply:
                applyLockScreen();
                break;
            case R.id.btnDefault:
                resetSetting();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
//            case R.id.swPassword:
//                break;
            case R.id.swSpark:
                PrefManager.saveSparkState(getApplicationContext(), isChecked);
                updateSwitchView(buttonView, isChecked);
                break;
            case R.id.swBurstParticle:
                PrefManager.saveParticleState(getApplicationContext(), isChecked);
                updateSwitchView(buttonView, isChecked);
                break;
            case R.id.swTitle:
                PrefManager.saveTitleState(getApplicationContext(), isChecked);
                updateSwitchView(buttonView, isChecked);
                changeEditTitleState(isChecked);
                break;
            case R.id.swTitleAnimation:
                PrefManager.saveTitleAnimationState(getApplicationContext(), isChecked);
                updateSwitchView(buttonView, isChecked);
                break;
        }
    }

    private void resetSetting() {
        PrefManager.saveSparkState(getApplicationContext(), true);
        PrefManager.saveParticleState(getApplicationContext(), true);
        PrefManager.saveTitleState(getApplicationContext(), false);
        PrefManager.saveTitle(getApplicationContext(), "Welcome back");
        PrefManager.saveCurrentPic(getApplicationContext(), null);
        selfReload();
    }

    private void selfReload() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    private void applyLockScreen() {
        gotoLockScreen();
    }

    private void showLockScreenFragment() {
        new PFPinCodeViewModel().isPinCodeEncryptionKeyExist().observe(this, new Observer<PFResult<Boolean>>() {
            @Override
            public void onChanged(PFResult<Boolean> result) {
                if (result == null) {
                    return;
                }

                if (result.getError() != null) {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    return;
                }

                showLockScreenFragment(result.getResult());
            }
        });
    }

    private void showLockScreenFragment(Boolean isPinExist) {
        final PFFLockScreenConfiguration.Builder builder = new PFFLockScreenConfiguration
                .Builder(this)
                .setTitle(isPinExist ? "Input code to disable" : "Create pin code")
                .setCodeLength(4)
                .setNewCodeValidation(true)
                .setLeftButton("Cancel")
                .setNewCodeValidationTitle("Input code again")
                .setUseFingerprint(false);

        final CustomLockScreenFragment lockScreenFragment = new CustomLockScreenFragment();
        lockScreenFragment.setOnLeftButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSwitchView(swPassword, PrefManager.getPasswordState(getApplicationContext()));
                getSupportFragmentManager().beginTransaction().remove(lockScreenFragment).commit();
            }
        });
        builder.setMode(isPinExist ? PFFLockScreenConfiguration.MODE_AUTH : PFFLockScreenConfiguration.MODE_CREATE);

        if (isPinExist) {
            lockScreenFragment.setEncodedPinCode(PrefManager.getPassword(getApplicationContext()));
            lockScreenFragment.setLoginListener(loginListener(lockScreenFragment));
        } else {
            lockScreenFragment.setCodeCreateListener(codeCreateListener(lockScreenFragment));
        }
        lockScreenFragment.setConfiguration(builder.build());
        getSupportFragmentManager().beginTransaction().replace(R.id.container, lockScreenFragment).commit();
    }

    private CustomLockScreenFragment.OnPFLockScreenLoginListener loginListener(CustomLockScreenFragment fragment) {
        CustomLockScreenFragment.OnPFLockScreenLoginListener loginListener = new CustomLockScreenFragment.OnPFLockScreenLoginListener() {
            @Override
            public void onCodeInputSuccessful() {
                boolean currentPasswordState = PrefManager.getPasswordState(getApplicationContext());
                PFSecurityManager.getInstance().getPinCodeHelper().delete(new PFPinCodeHelperCallback<Boolean>() {
                    @Override
                    public void onResult(PFResult<Boolean> result) {
                        Toast.makeText(getApplicationContext(), "Disable password", Toast.LENGTH_SHORT).show();
                        updateSwitchView(swPassword, false);
                        PrefManager.savePasswordState(getApplicationContext(), false);
                        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    }
                });
            }

            @Override
            public void onFingerprintSuccessful() {

            }

            @Override
            public void onPinLoginFailed() {
                Toast.makeText(getApplicationContext(), "Pin failed", Toast.LENGTH_SHORT).show();
                fragment.clearAllPinCode();
            }

            @Override
            public void onFingerprintLoginFailed() {

            }
        };
        return loginListener;
    }

    private CustomLockScreenFragment.OnPFLockScreenCodeCreateListener codeCreateListener(CustomLockScreenFragment fragment) {
        CustomLockScreenFragment.OnPFLockScreenCodeCreateListener createCodeListener = new CustomLockScreenFragment.OnPFLockScreenCodeCreateListener() {
            @Override
            public void onCodeCreated(String encodedCode) {
                //Save new password
                Toast.makeText(getApplicationContext(), "Pin code created", Toast.LENGTH_SHORT).show();
                PrefManager.savePassword(getApplicationContext(), encodedCode);
                PrefManager.savePasswordState(getApplicationContext(), true);
                updateSwitchView(swPassword, true);
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }

            @Override
            public void onNewCodeValidationFailed() {
                Toast.makeText(getApplicationContext(), "Please input new pin code", Toast.LENGTH_SHORT).show();
                fragment.clearAllPinCode();
            }
        };

        return createCodeListener;
    }

    private void changeEditTitleState(boolean isChecked) {
        if (isChecked) {
            rlChangeTitle.setVisibility(View.VISIBLE);
            rlTitleAnimation.setVisibility(View.VISIBLE);
        } else {
            rlChangeTitle.setVisibility(View.GONE);
            rlTitleAnimation.setVisibility(View.GONE);
        }
    }

    private void updateSwitchView(CompoundButton buttonView, boolean isChecked) {
        buttonView.setChecked(isChecked);
    }

    private void saveNewTitle() {
        String newTitle = edtTitle.getText().toString();
        if (!newTitle.isEmpty()) {
            hideKeyboard();
            PrefManager.saveTitle(getApplicationContext(), newTitle);
            edtTitle.clearFocus();
            Toast.makeText(getApplicationContext(), "Save new title success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Please fill title!", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }

        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void choosePhotoFromGallery() {
        Intent chooseIntent = new Intent(Intent.ACTION_PICK);
        chooseIntent.setType("image/*");
        startActivityForResult(chooseIntent, CHOOSE_IMAGE);
    }

    @Override
    public void onBackPressed() {
//        gotoLockScreen();
        return;
    }

    private void gotoLockScreen() {
//        DevicePolicyUtil.getInstance(getApplicationContext()).lockNow();
        Intent intent = new Intent(SettingActivity.this, SlowLockerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finishAffinity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CHOOSE_IMAGE:
                    Intent imageResponse = data;
                    Uri selectedImage = imageResponse.getData();
                    CropImage.activity(selectedImage).setGuidelines(CropImageView.Guidelines.ON).start(this);
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    Uri imageUri = result.getUri();
                    currentPic = BitmapUtil.getBitmapFromUri(getApplicationContext(), imageUri);
                    imgPreview.setImageBitmap(null);
                    imgPreview.setImageBitmap(currentPic);
                    PrefManager.saveCurrentPic(getApplicationContext(), currentPic);
                    break;
                case OVERLAY_PERMISSION_CODE:
                    if (requestCode == OVERLAY_PERMISSION_CODE) {
                        askedForOverlayPermission = false;
                        if (Settings.canDrawOverlays(this)) {
                            //Permission granted
                        } else {
                            Toast.makeText(getApplicationContext(), "ACTION_MANAGE_OVERLAY_PERMISSION Permission Denied", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                    break;
            }
        } else {
            if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                CropImage.ActivityResult cropperResult = CropImage.getActivityResult(data);
                String error = cropperResult.getError().getMessage();
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public int getSystemUiVisibility() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    }
}