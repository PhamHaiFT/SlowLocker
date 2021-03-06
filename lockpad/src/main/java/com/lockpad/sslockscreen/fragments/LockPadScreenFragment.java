package com.lockpad.sslockscreen.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.lockpad.sslockscreen.LPConfiguration;
import com.lockpad.sslockscreen.R;
import com.lockpad.sslockscreen.security.LPResult;
import com.lockpad.sslockscreen.viewmodels.LPPinCodeViewModel;
import com.lockpad.sslockscreen.views.LPCodeView;

public class LockPadScreenFragment extends Fragment {

    private static final String TAG = LockPadScreenFragment.class.getName();

    private static final String INSTANCE_STATE_CONFIG
            = "com.beautycoder.lockpad.instance_state_config";

    private View mFingerprintButton;
    private View mDeleteButton;
    private TextView mLeftButton;
    private Button mNextButton;
    private LPCodeView mPinCodeView;
    private TextView titleView;

    private boolean mUseFingerPrint = true;
    private boolean mFingerprintHardwareDetected = false;
    private boolean mIsCreateMode = false;

    private OnLPCodeCreateListener mCodeCreateListener;
    private OnLPLoginListener mLoginListener;
    private String mCode = "";
    private String mCodeValidation = "";
    private String mEncodedPinCode = "";

    private LPConfiguration mConfiguration;
    private View mRootView;

    private final LPPinCodeViewModel mLPPinCodeViewModel = new LPPinCodeViewModel();

    private View.OnClickListener mOnLeftButtonClickListener = null;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(INSTANCE_STATE_CONFIG, mConfiguration);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_lock_screen_pf, container,
                false);

        if (mConfiguration == null) {
            mConfiguration = (LPConfiguration) savedInstanceState.getSerializable(
                    INSTANCE_STATE_CONFIG
            );
        }

        mFingerprintButton = view.findViewById(R.id.button_finger_print);
        mDeleteButton = view.findViewById(R.id.button_delete);

        mLeftButton = view.findViewById(R.id.button_left);
        mNextButton = view.findViewById(R.id.button_next);

        mDeleteButton.setOnClickListener(mOnDeleteButtonClickListener);
        mDeleteButton.setOnLongClickListener(mOnDeleteButtonOnLongClickListener);
        mFingerprintButton.setOnClickListener(mOnFingerprintClickListener);

        mPinCodeView = view.findViewById(R.id.code_view);
        initKeyViews(view);

        mPinCodeView.setListener(mEnterCodeListener);

        if (!mUseFingerPrint) {
            mFingerprintButton.setVisibility(View.GONE);
        }

        mFingerprintHardwareDetected = isFingerprintApiAvailable(getContext());

        mRootView = view;
        applyConfiguration(mConfiguration);

        return view;
    }

    @Override
    public void onStart() {
        if (!mIsCreateMode && mUseFingerPrint && mConfiguration.isAutoShowFingerprint() &&
                isFingerprintApiAvailable(getActivity()) && isFingerprintsExists(getActivity())) {
            mOnFingerprintClickListener.onClick(mFingerprintButton);
        }
        super.onStart();
    }

    public void setConfiguration(LPConfiguration configuration) {
        this.mConfiguration = configuration;
        applyConfiguration(configuration);
    }

    private void applyConfiguration(LPConfiguration configuration) {
        if (mRootView == null || configuration == null) {
            return;
        }
        titleView = mRootView.findViewById(R.id.title_text_view);
        titleView.setText(configuration.getTitle());
        if (TextUtils.isEmpty(configuration.getLeftButton())) {
            mLeftButton.setVisibility(View.GONE);
        } else {
            mLeftButton.setText(configuration.getLeftButton());
            mLeftButton.setOnClickListener(mOnLeftButtonClickListener);
        }

        if (!TextUtils.isEmpty(configuration.getNextButton())) {
            mNextButton.setText(configuration.getNextButton());
        }

        mUseFingerPrint = configuration.isUseFingerprint();
        if (!mUseFingerPrint) {
            mFingerprintButton.setVisibility(View.GONE);
            mDeleteButton.setVisibility(View.VISIBLE);
        }
        mIsCreateMode = mConfiguration.getMode() == LPConfiguration.MODE_CREATE;

        if (mIsCreateMode) {
            mLeftButton.setVisibility(View.GONE);
            mFingerprintButton.setVisibility(View.GONE);
        }

        if (mIsCreateMode) {
            mNextButton.setOnClickListener(mOnNextButtonClickListener);
        } else {
            mNextButton.setOnClickListener(null);
        }

        mNextButton.setVisibility(View.INVISIBLE);
        mPinCodeView.setCodeLength(mConfiguration.getCodeLength());
    }

    private void initKeyViews(View parent) {
        parent.findViewById(R.id.button_0).setOnClickListener(clickListener);
        parent.findViewById(R.id.button_1).setOnClickListener(clickListener);
        parent.findViewById(R.id.button_2).setOnClickListener(clickListener);
        parent.findViewById(R.id.button_3).setOnClickListener(clickListener);
        parent.findViewById(R.id.button_4).setOnClickListener(clickListener);
        parent.findViewById(R.id.button_5).setOnClickListener(clickListener);
        parent.findViewById(R.id.button_6).setOnClickListener(clickListener);
        parent.findViewById(R.id.button_7).setOnClickListener(clickListener);
        parent.findViewById(R.id.button_8).setOnClickListener(clickListener);
        parent.findViewById(R.id.button_9).setOnClickListener(clickListener);
    }

    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v instanceof TextView) {
                final String string = ((TextView) v).getText().toString();
                if (string.length() != 1) {
                    return;
                }
                final int codeLength = mPinCodeView.input(string);
                configureRightButton(codeLength);
            }
        }
    };

    private final View.OnClickListener mOnDeleteButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int codeLength = mPinCodeView.delete();
            configureRightButton(codeLength);
        }
    };

    public void clearAllPinCode() {
        mPinCodeView.clearCode();
    }

    private final View.OnLongClickListener mOnDeleteButtonOnLongClickListener
            = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            mPinCodeView.clearCode();
            configureRightButton(0);
            return true;
        }
    };

    private final View.OnClickListener mOnFingerprintClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                    !isFingerprintApiAvailable(getActivity())) {
                return;
            }


            if (!isFingerprintsExists(getActivity())) {
                showNoFingerprintDialog();
                return;
            }

            final LPFingerprintAuthDialogFragment fragment
                    = new LPFingerprintAuthDialogFragment();
            fragment.show(getFragmentManager(), "FINGERPRINT");
            fragment.setAuthListener(new LPFingerprintAuthListener() {
                @Override
                public void onAuthenticated() {
                    if (mLoginListener != null) {
                        mLoginListener.onFingerprintSuccessful();
                    }
                    fragment.dismiss();
                }

                @Override
                public void onError() {
                    if (mLoginListener != null) {
                        mLoginListener.onFingerprintLoginFailed();
                    }
                }
            });
        }
    };

    private void configureRightButton(int codeLength) {
        if (mIsCreateMode) {
            if (codeLength > 0) {
                mDeleteButton.setVisibility(View.VISIBLE);
            } else {
                mDeleteButton.setVisibility(View.GONE);
            }
            return;
        }

        if (codeLength > 0) {
            mFingerprintButton.setVisibility(View.GONE);
            mDeleteButton.setVisibility(View.VISIBLE);
            mDeleteButton.setEnabled(true);
            return;
        }

        if (mUseFingerPrint && mFingerprintHardwareDetected) {
            mFingerprintButton.setVisibility(View.VISIBLE);
            mDeleteButton.setVisibility(View.GONE);
        } else {
            mFingerprintButton.setVisibility(View.GONE);
            mDeleteButton.setVisibility(View.VISIBLE);
        }

        mDeleteButton.setEnabled(false);

    }

    private boolean isFingerprintApiAvailable(Context context) {
        return FingerprintManagerCompat.from(context).isHardwareDetected();
    }

    private boolean isFingerprintsExists(Context context) {
        return FingerprintManagerCompat.from(context).hasEnrolledFingerprints();
    }


    private void showNoFingerprintDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.no_fingerprints_title_pf)
                .setMessage(R.string.no_fingerprints_message_pf)
                .setCancelable(true)
                .setNegativeButton(R.string.cancel_pf, null)
                .setPositiveButton(R.string.settings_pf, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(
                                new Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS)
                        );
                    }
                }).create().show();
    }


    private final LPCodeView.OnPFCodeListener mEnterCodeListener = new LPCodeView.OnPFCodeListener() {
        @Override
        public void onCodeCompleted(String code) {
            if (mIsCreateMode) {
                mNextButton.setVisibility(View.VISIBLE);
                mCode = code;
                return;
            }
            mCode = code;
            mLPPinCodeViewModel.checkPin(getContext(), mEncodedPinCode, mCode).observe(
                    LockPadScreenFragment.this,
                    new Observer<LPResult<Boolean>>() {
                        @Override
                        public void onChanged(@Nullable LPResult<Boolean> result) {
                            if (result == null) {
                                return;
                            }
                            if (result.getError() != null) {
                                return;
                            }
                            final boolean isCorrect = result.getResult();
                            if (mLoginListener != null) {
                                if (isCorrect) {
                                    mLoginListener.onCodeInputSuccessful();
                                } else {
                                    mLoginListener.onPinLoginFailed();
                                    errorAction();
                                }
                            }
                            if (!isCorrect && mConfiguration.isClearCodeOnError()) {
                                mPinCodeView.clearCode();
                            }
                        }
                    });

        }

        @Override
        public void onCodeNotCompleted(String code) {
            if (mIsCreateMode) {
                mNextButton.setVisibility(View.INVISIBLE);
                return;
            }
        }
    };


    private final View.OnClickListener mOnNextButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            checkNewCode();
        }
    };

    private void checkNewCode() {
        if (mConfiguration.isNewCodeValidation() && TextUtils.isEmpty(mCodeValidation)) {
            mCodeValidation = mCode;
            cleanCode();
            titleView.setText(mConfiguration.getNewCodeValidationTitle());
            return;
        }
        if (mConfiguration.isNewCodeValidation() && !TextUtils.isEmpty(mCodeValidation) && !mCode.equals(mCodeValidation)) {
            mCodeCreateListener.onNewCodeValidationFailed();
            titleView.setText(mConfiguration.getTitle());
            cleanCode();
            return;
        }
        mCodeValidation = "";
        mLPPinCodeViewModel.encodePin(getContext(), mCode).observe(
                LockPadScreenFragment.this,
                new Observer<LPResult<String>>() {
                    @Override
                    public void onChanged(@Nullable LPResult<String> result) {
                        if (result == null) {
                            return;
                        }
                        if (result.getError() != null) {
                            deleteEncodeKey();
                            return;
                        }
                        final String encodedCode = result.getResult();
                        if (mCodeCreateListener != null) {
                            mCodeCreateListener.onCodeCreated(encodedCode);
                        }
                    }
                }
        );
    }

    private void cleanCode() {
        mCode = "";
        mPinCodeView.clearCode();
    }


    private void deleteEncodeKey() {
        mLPPinCodeViewModel.delete().observe(
                this,
                new Observer<LPResult<Boolean>>() {
                    @Override
                    public void onChanged(@Nullable LPResult<Boolean> result) {
                        if (result == null) {
                            return;
                        }
                        if (result.getError() != null) {
                            return;
                        }

                    }
                }
        );
    }

    private void errorAction() {
        if (mConfiguration.isErrorVibration()) {
            final Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null) {
                v.vibrate(400);
            }
        }

        if (mConfiguration.isErrorAnimation()) {
            final Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.shake_pf);
            mPinCodeView.startAnimation(animShake);
        }
    }

    public void setOnLeftButtonClickListener(View.OnClickListener onLeftButtonClickListener) {
        this.mOnLeftButtonClickListener = onLeftButtonClickListener;
    }

    public void setCodeCreateListener(OnLPCodeCreateListener listener) {
        mCodeCreateListener = listener;
    }

    public void setLoginListener(OnLPLoginListener listener) {
        mLoginListener = listener;
    }

    public void setEncodedPinCode(String encodedPinCode) {
        mEncodedPinCode = encodedPinCode;
    }

    public interface OnLPCodeCreateListener {

        void onCodeCreated(String encodedCode);

        void onNewCodeValidationFailed();

    }

    public interface OnLPLoginListener {

        void onCodeInputSuccessful();

        void onFingerprintSuccessful();

        void onPinLoginFailed();

        void onFingerprintLoginFailed();

    }
}


