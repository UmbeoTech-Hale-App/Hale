package org.o7planning.hale_2.GFitActivities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.o7planning.hale_2.AppUsageStats.util.PreferencesManager;
import org.o7planning.hale_2.R;
import org.o7planning.hale_2.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class StepsActivity extends AppCompatActivity {

    public static final String TAG = "StepCounter";
    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;

    TextView mStepsTextView;
    TextView mCircularProgressbarStepsTextView;

    TextView mCurrentStepsTextView;
    TextView mTargetTextView;

    Button mReadStepsButton;
    Button mSetGoalButton;

    Switch mEnableStepsMonitoringSwitch;
    LinearLayout mEnableStepTrackingLinearLayout;
    CircularProgressBar mCircularProgressbar;

    RelativeLayout mStepUiComponentsRelativeLayout;

    private Timer pieChartRefreshTimer;
    private PieChart pieChart;

    ConstraintLayout mParentConstraintLayout;

    int targetSteps = PreferencesManager.getInstance().getInt("target_steps");
    Handler mHandler = new Handler();

    int CHECK_INTERVAL = 2000;

    private Runnable mRepeatCheckTask = new Runnable() {
        @Override
        public void run() {
//            ToastUtil.makeLongToast(getApplicationContext(),"Running AppService");
            mHandler.postDelayed(mRepeatCheckTask, CHECK_INTERVAL);
            readStepData();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gfit_steps);

        mReadStepsButton = findViewById(R.id.read_steps_button);
        mSetGoalButton = findViewById(R.id.set_goal_button_steps_activity);

        mStepsTextView = findViewById(R.id.steps_textview);
        mCircularProgressbarStepsTextView = findViewById(R.id.progressbar_steps_count_textview_steps_activity);

        mCurrentStepsTextView = findViewById(R.id.current_steps_textview_steps_activity);
        mTargetTextView = findViewById(R.id.target_steps_textview_step_activity);

        mEnableStepsMonitoringSwitch = findViewById(R.id.switch_enable_step_monitoring);
        mEnableStepTrackingLinearLayout = findViewById(R.id.linearlayout_enable_step_monitoring);
        mCircularProgressbar = findViewById(R.id.circular_progressbar_steps_activity);
        pieChart = findViewById(R.id.progressChart);

        mStepUiComponentsRelativeLayout = findViewById(R.id.relative_layout_step_components);
        mParentConstraintLayout = findViewById(R.id.parent_coordinator_layout_steps_activity);

        ///--------------------------------------------------------------------------------------------------

        mEnableStepsMonitoringSwitch.setChecked(false);

        mSetGoalButton.setOnClickListener(v -> {

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
            input.setHint("Enter Target Steps");

            input.setPadding(5, 5, 5, 5);
            input.setHeight(20);

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setCancelable(true);
            alert.setView(input);

//        alert.setView(promptView);

            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
//                int customIntake = Integer.parseInt(customIntakeTextview.getText().toString());
                    int target ;

                    try {
                        target = Integer.parseInt(input.getText().toString());
                    } catch (NumberFormatException numberFormatException) {
                        ToastUtil.makeShortToast(getApplicationContext(), "Please enter Numeric Value");
                        return;
                    }

//                    mCircularProgressbar.setProgressMax(targetSteps);
                    mTargetTextView.setText(String.valueOf(targetSteps));

                    PreferencesManager.getInstance().putInt("target_steps", targetSteps);
                    targetSteps = target;
                    readStepData();

                    dialogInterface.cancel();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });

            alert.show();
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
//            mStepsTextView.setVisibility(View.GONE);
//            mReadStepsButton.setVisibility(View.GONE);
            mEnableStepTrackingLinearLayout.setVisibility(View.VISIBLE);
            mStepUiComponentsRelativeLayout.setVisibility(View.GONE);
            mParentConstraintLayout.setBackgroundResource(0);
        } else {
//            mStepsTextView.setVisibility(View.VISIBLE);
//            mReadStepsButton.setVisibility(View.VISIBLE);
            mEnableStepTrackingLinearLayout.setVisibility(View.GONE);
            mStepUiComponentsRelativeLayout.setVisibility(View.VISIBLE);
            mParentConstraintLayout.setBackgroundResource(R.drawable.ic_app_bg);
            mStepUiComponentsRelativeLayout.setVisibility(View.VISIBLE);
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

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void setupPiechart() {
        Description description = new Description();
        description.setText("");
        pieChart.setDescription(description);
        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(35f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterText("Step Tracker");
        pieChart.setCenterTextSize(13);

        pieChartRefreshTimer = new Timer();
        pieChartRefreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }

        }, 0, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (pieChartRefreshTimer != null)
            pieChartRefreshTimer.cancel();
    }

    private void TimerMethod() {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(Timer_Tick);
    }

    private Runnable Timer_Tick = new Runnable() {
        public void run() {
//            ToastUtil.makeShortToast(getApplicationContext(), "timer");
            readStepData();
        }
    };

    // Refresh the pie chart dataset based on latest step counts.
    private void RefreshDataSet(long steps, long target) {

        Log.d(TAG, "addDataSet started");

        ArrayList<PieEntry> yEntrys = new ArrayList<>();

        if (target == -1) {
            target = 2000;
        }

        yEntrys.add(new PieEntry(steps, "Total Steps Taken"));
        yEntrys.add(new PieEntry((target - steps > 0 ? target - steps : 0f), "Remaining Steps"));

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "");
        pieDataSet.setSliceSpace(1);
        pieDataSet.setValueTextSize(20);
        pieDataSet.setValueTextColor(Color.rgb(255, 255, 255));
//        pieDataSet.setValueTextColors(Collections.singletonList(Color.rgb(255, 255, 255)));

        //add colors to dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ColorTemplate.getHoloBlue());
        colors.add(Color.BLUE);
        pieDataSet.setColors(colors);

        //add legend to chart
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                subscribe();
//                mStepsTextView.setVisibility(View.VISIBLE);
//                mReadStepsButton.setVisibility(View.VISIBLE);
                mEnableStepTrackingLinearLayout.setVisibility(View.GONE);
                mStepUiComponentsRelativeLayout.setVisibility(View.VISIBLE);
                mParentConstraintLayout.setBackgroundResource(R.drawable.ic_app_bg);
                mStepUiComponentsRelativeLayout.setVisibility(View.VISIBLE);
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
                                readStepData();
                                setupPiechart();
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
                            RefreshDataSet(total, targetSteps);

                            mStepsTextView.setText(Long.toString(total));
                            mCircularProgressbarStepsTextView.setText(Long.toString(total));
                            mCircularProgressbar.setProgress(total);

                            mTargetTextView.setText(String.valueOf(targetSteps));
                            mCurrentStepsTextView.setText(String.valueOf(total));

//                            ToastUtil.makeLongToast(getApplicationContext(), Long.toString(total));
                        })
                .addOnFailureListener(
                        e -> {
//                                Log.w(TAG, "There was a problem getting the step count.", e);
                            ToastUtil.makeLongToast(getApplicationContext(), "Failed to Read Step Data");
                        });

    }


}
