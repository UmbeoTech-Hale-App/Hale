package org.o7planning.hale_2.GFitActivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;

import org.o7planning.hale_2.R;
import org.o7planning.hale_2.utils.ToastUtil;

public class StepsActivity extends AppCompatActivity {

    public static final String TAG = "StepCounter";
    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;

    TextView mStepsTextView;
    Button mReadStepsButton;
    Switch mEnableStepsMonitoringSwitch;
    LinearLayout mEnableStepTrackingLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gfit_steps);

        mReadStepsButton = findViewById(R.id.read_steps_button);
        mStepsTextView = findViewById(R.id.steps_textview);
        mEnableStepsMonitoringSwitch = findViewById(R.id.switch_enable_step_monitoring);
        mEnableStepTrackingLinearLayout = findViewById(R.id.linearlayout_enable_step_monitoring);

        mEnableStepsMonitoringSwitch.setChecked(false);


        mReadStepsButton.setOnClickListener(v -> {
            readStepData();
        });

        FitnessOptions fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .addDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                        .addDataType(DataType.TYPE_HEART_RATE_BPM)
                        .addDataType(DataType.TYPE_CALORIES_EXPENDED)
                        .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            mStepsTextView.setVisibility(View.GONE);
            mReadStepsButton.setVisibility(View.GONE);
            mEnableStepTrackingLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mStepsTextView.setVisibility(View.VISIBLE);
            mReadStepsButton.setVisibility(View.VISIBLE);
            mEnableStepTrackingLinearLayout.setVisibility(View.GONE);
            subscribe();
        }

        // This method sets up our custom logger, which will print all log messages to the device
        // screen, as well as to adb logcat.
//        initializeLogging();

        mEnableStepsMonitoringSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
//                if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
                GoogleSignIn.requestPermissions(this, REQUEST_OAUTH_REQUEST_CODE,
                        GoogleSignIn.getLastSignedInAccount(this), fitnessOptions);
//                } else {
//                    subscribe();
//                }
            }
//            mEnableStepsMonitoringSwitch.setChecked(false);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                subscribe();
                mStepsTextView.setVisibility(View.VISIBLE);
                mReadStepsButton.setVisibility(View.VISIBLE);
                mEnableStepTrackingLinearLayout.setVisibility(View.GONE);
            } else {
//                mEnableStepsMonitoringSwitch.setChecked(false);
            }
        }
//        mEnableStepsMonitoringSwitch.setChecked(false);
    }

    /**
     * Records step data by requesting a subscription to background step data.
     */
    public void subscribe() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
//                                    Log.i(TAG, "Successfully subscribed!");
//                                ToastUtil.makeLongToast(getApplicationContext(), "Subscribed Successfully");
                            } else {
//                                    Log.w(TAG, "There was a problem subscribing.", task.getException());
                                ToastUtil.makeLongToast(getApplicationContext(), "There was a problem subscribing.");
                            }
                        });
    }

    /**
     * Reads the current daily step total, computed from midnight of the current day on the device's
     * current timezone.
     */
    private void readStepData() {
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        dataSet -> {
                            long total =
                                    dataSet.isEmpty()
                                            ? 0
                                            : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
//                                Log.i(TAG, "Total steps: " + total);
                            mStepsTextView.setText(Long.toString(total));
                            ToastUtil.makeLongToast(getApplicationContext(), Long.toString(total));
                        })
                .addOnFailureListener(
                        e -> {
//                                Log.w(TAG, "There was a problem getting the step count.", e);
                            ToastUtil.makeLongToast(getApplicationContext(), "Failed to Read Step Data");
                        });
    }


}
