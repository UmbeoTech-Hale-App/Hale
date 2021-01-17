package org.o7planning.hale_2.Screens;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.o7planning.hale_2.AppUsageStats.util.PreferencesManager;
import org.o7planning.hale_2.R;
import org.o7planning.hale_2.Screens.Fragments.UpdateUserInfoFragment;
import org.o7planning.hale_2.utils.ToastUtil;

import params.com.stepprogressview.StepProgressView;


public class WaterMainActivity extends AppCompatActivity {

    LinearLayout m50ml_LinearLayout;
    LinearLayout m100ml_LinearLayout;
    LinearLayout m150ml_LinearLayout;
    LinearLayout m200ml_LinearLayout;
    LinearLayout m250ml_LinearLayout;
    LinearLayout mCustom_ml_LinearLayout;

    FloatingActionButton mAddWaterIntakeButton;
    FloatingActionButton mEnableDisableNotificationButton;
    FloatingActionButton mWaterIntakeStatsButton;

    TextView mCurrentIntakeTextView;
    TextView mTargetIntakeTextView;

    ImageView mSettingsImageView;
    ConstraintLayout mMainConstraintLayout;

    StepProgressView mStepProgressView;

    int targetWaterIntake = PreferencesManager.getInstance().getInt("target_water_intake");
    int currentWaterIntake = PreferencesManager.getInstance().getInt("current_water_intake");
    int index = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        targetWaterIntake = PreferencesManager.getInstance().getInt("target_water_intake");
        currentWaterIntake = PreferencesManager.getInstance().getInt("current_water_intake");

        setContentView(R.layout.activity_umbeo_water_main);

        m50ml_LinearLayout = findViewById(R.id.umbeo_water_50ml_layout);
        m100ml_LinearLayout = findViewById(R.id.umbeo_water_100ml_layout);
        m150ml_LinearLayout = findViewById(R.id.umbeo_water_150ml_layout);
        m200ml_LinearLayout = findViewById(R.id.umbeo_water_200ml_layout);
        m250ml_LinearLayout = findViewById(R.id.umbeo_water_250ml_layout);
        mCustom_ml_LinearLayout = findViewById(R.id.umbeo_custom_water_intake_layout);

        mAddWaterIntakeButton = findViewById(R.id.umbeo_water_intake_button);
        mEnableDisableNotificationButton = findViewById(R.id.umbeo_water_notification_button);
        mWaterIntakeStatsButton = findViewById(R.id.umbeo_water_stats_button);

        mMainConstraintLayout = findViewById(R.id.umbeo_activity_water_main_coordinator_layout);

        mCurrentIntakeTextView = findViewById(R.id.current_intake_water_main_textview);
        mTargetIntakeTextView = findViewById(R.id.umbeo_target_intake_water_main_textview);

        mStepProgressView = findViewById(R.id.intake_step_progress_view_water_main_activity);
        mSettingsImageView = findViewById(R.id.umbeo_water_main_settings_imageview);

        ///--------------------------------------------------------------------------------------------------------
        mStepProgressView.setCurrentProgress(0);

        mCurrentIntakeTextView.setText(String.valueOf(currentWaterIntake));

        if (targetWaterIntake == 0) {
            mTargetIntakeTextView.setText("3000 ml");
            mStepProgressView.setTotalProgress(3000);
            PreferencesManager.getInstance().putInt("target_water_intake",3000);
        } else {
            mTargetIntakeTextView.setText(String.valueOf(targetWaterIntake) + " ml");
            mStepProgressView.setTotalProgress(targetWaterIntake);
        }

        mStepProgressView.setCurrentProgress(currentWaterIntake);
        mStepProgressView.setProgressColor(Color.WHITE);

        setupListeners();

    }

    public void showMessage(String title, String Message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setTitle(title);
        alert.setMessage(Message);
        alert.show();
    }

    private void customIntakeAlert() {

//        View promptView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_alertbox_input, this);
//        TextView customIntakeTextview = promptView.findViewById(R.id.custom_intake_alertbox_textview);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Enter Custom intake in ml's");
        input.setPadding(5, 5, 5, 5);

        input.setHeight(50);
        input.setMinHeight(30);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setView(input);

//        alert.setView(promptView);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                int customIntake = Integer.parseInt(customIntakeTextview.getText().toString());

                int customIntake;
                try {
                    customIntake = Integer.parseInt(input.getText().toString());
                } catch (NumberFormatException numberFormatException) {
                    ToastUtil.makeShortToast(getApplicationContext(), "Please enter numeric value");
                    return;
                }

                currentWaterIntake += customIntake;
                mCurrentIntakeTextView.setText(String.valueOf(currentWaterIntake));
                mStepProgressView.setCurrentProgress(Math.min(currentWaterIntake, targetWaterIntake));
                PreferencesManager.getInstance().putInt("current_water_intake",currentWaterIntake);

                dialogInterface.cancel();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        alert.show();
    }

    private void setupListeners() {

        mSettingsImageView.setOnClickListener(v -> {
            BottomSheetDialogFragment updateUserInfoFragment = new UpdateUserInfoFragment();
            updateUserInfoFragment.show(getSupportFragmentManager(), updateUserInfoFragment.getTag());
        });

        mAddWaterIntakeButton.setOnClickListener(v -> {
            int currentIntake = currentWaterIntake;

            if (index >= 1 && index <= 6) {
                if (index == 1) {
                    currentIntake += 50;
                }
                if (index == 2) {
                    currentIntake += 100;
                }
                if (index == 3) {
                    currentIntake += 150;
                }
                if (index == 4) {
                    currentIntake += 200;
                }
                if (index == 5) {
                    currentIntake += 250;
                }
                if (index == 6) {
                    customIntakeAlert();
                }

                currentWaterIntake = currentIntake;

                index = 0;
                mCurrentIntakeTextView.setText(String.valueOf(currentWaterIntake));
                mStepProgressView.setCurrentProgress(Math.min(currentWaterIntake, targetWaterIntake));
                PreferencesManager.getInstance().putInt("current_water_intake",currentWaterIntake);

                m50ml_LinearLayout.setBackgroundResource(0);
                m100ml_LinearLayout.setBackgroundResource(0);
                m150ml_LinearLayout.setBackgroundResource(0);
                m200ml_LinearLayout.setBackgroundResource(0);
                m250ml_LinearLayout.setBackgroundResource(0);
                mCustom_ml_LinearLayout.setBackgroundResource(0);
            }

            mStepProgressView.setCurrentProgress(Math.min(currentIntake, targetWaterIntake));

        });

//        mEnableDisableNotificationButton.setOnClickListener(v->{
//
//        });

        mWaterIntakeStatsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, WaterStatsActivity.class);
            startActivity(intent);
        });

        /////////////////////////////////////////////////////////////////////////////////

        mMainConstraintLayout.setOnClickListener(v -> {
            index = 0;
            m50ml_LinearLayout.setBackgroundResource(0);
            m100ml_LinearLayout.setBackgroundResource(0);
            m150ml_LinearLayout.setBackgroundResource(0);
            m200ml_LinearLayout.setBackgroundResource(0);
            m250ml_LinearLayout.setBackgroundResource(0);
            mCustom_ml_LinearLayout.setBackgroundResource(0);
        });

        m50ml_LinearLayout.setOnClickListener(v -> {
            index = 1;
            setup(1);
        });

        m100ml_LinearLayout.setOnClickListener(v -> {
            index = 2;
            setup(2);
        });

        m150ml_LinearLayout.setOnClickListener(v -> {
            index = 3;
            setup(3);
        });

        m200ml_LinearLayout.setOnClickListener(v -> {
            index = 4;
            setup(4);
        });

        m250ml_LinearLayout.setOnClickListener(v -> {
            index = 5;
            setup(5);
        });

        mCustom_ml_LinearLayout.setOnClickListener(v -> {
            index = 6;
            setup(6);
        });

    }

    private void setup(int i) {
        m50ml_LinearLayout.setBackgroundResource(0);
        m100ml_LinearLayout.setBackgroundResource(0);
        m150ml_LinearLayout.setBackgroundResource(0);
        m200ml_LinearLayout.setBackgroundResource(0);
        m250ml_LinearLayout.setBackgroundResource(0);
        mCustom_ml_LinearLayout.setBackgroundResource(0);

        if (i == 1) {
            m50ml_LinearLayout.setBackgroundResource(R.drawable.option_select_bg);
        }
        if (i == 2) {
            m100ml_LinearLayout.setBackgroundResource(R.drawable.option_select_bg);
        }
        if (i == 3) {
            m150ml_LinearLayout.setBackgroundResource(R.drawable.option_select_bg);
        }
        if (i == 4) {
            m200ml_LinearLayout.setBackgroundResource(R.drawable.option_select_bg);
        }
        if (i == 5) {
            m250ml_LinearLayout.setBackgroundResource(R.drawable.option_select_bg);
        }
        if (i == 6) {
            mCustom_ml_LinearLayout.setBackgroundResource(R.drawable.option_select_bg);
        }
    }


}
