package org.o7planning.hale_2.Screens;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import org.o7planning.hale_2.AppUsageStats.util.PreferencesManager;
import org.o7planning.hale_2.R;
import org.o7planning.hale_2.utils.ToastUtil;

import me.itangqi.waveloadingview.WaveLoadingView;

public class WaterStatsActivity extends AppCompatActivity {

    WaveLoadingView mWaveloadingView;
    TextView mRemainingIntakeTextview;
    TextView mTargetIntakeTextView;

    double currentWaterIntake;
    double targetWaterIntake;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_umbeo_water_stats);

        currentWaterIntake = PreferencesManager.getInstance().getInt("current_water_intake");
        targetWaterIntake = PreferencesManager.getInstance().getInt("target_water_intake");

        mWaveloadingView = findViewById(R.id.progress_wave_loading_view_water_stats_activity);
        mRemainingIntakeTextview = findViewById(R.id.remaining_intake_textview_water_stats_activity);
        mTargetIntakeTextView = findViewById(R.id.target_intake_textview_water_stats_activity);

        ;
        mRemainingIntakeTextview.setText(String.valueOf(Math.max((int)(targetWaterIntake-currentWaterIntake),0)) + " ml");
        mTargetIntakeTextView.setText(String.valueOf((int)targetWaterIntake) + " ml");

        double progress = (currentWaterIntake / targetWaterIntake)*100 ;

        mWaveloadingView.setProgressValue((int) Math.min(100, progress));
        mWaveloadingView.setCenterTitle(String.valueOf((int)progress) + "%");

//        ToastUtil.makeShortToast(getApplicationContext(), String.valueOf(progress));
    }
}
