package org.o7planning.hale_2.GFitActivities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.o7planning.hale_2.AppUsageStats.DB.RoomDBAPI;
import org.o7planning.hale_2.AppUsageStats.Models.ScreenItem;
import org.o7planning.hale_2.AppUsageStats.util.PreferencesManager;
import org.o7planning.hale_2.R;
import org.o7planning.hale_2.S_R.Service.YourService;
import org.o7planning.hale_2.logger.Log;
import org.o7planning.hale_2.logger.LogView;
import org.o7planning.hale_2.logger.LogWrapper;
import org.o7planning.hale_2.logger.MessageOnlyLogFilter;
import org.o7planning.hale_2.utils.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ScreenAnalysisActivity extends AppCompatActivity {

    Button mFetchScreenDataButton;
    Switch mScreenOnOffTrackingButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gfit_screen_monitoring);

        initializeLogging();
        mFetchScreenDataButton = findViewById(R.id.fetch_screen_data_button);
        mScreenOnOffTrackingButton = findViewById(R.id.screen_on_off_tracking_button);

//        Intent sintent = new Intent(this, YourService.class);
//        startService(sintent);

//        ToastUtil.makeLongToast(getApplicationContext(),Boolean.toString(PreferencesManager.getInstance().getBoolean("track_screen")));

        if(PreferencesManager.getInstance().getBoolean("track_screen"))
        {
            mScreenOnOffTrackingButton.setChecked(true);
            Intent intent = new Intent(this, YourService.class);
            startService(intent);
        }
        else
        {
            mScreenOnOffTrackingButton.setChecked(false);
            Intent intent = new Intent(this, YourService.class);
            intent.putExtra("close",true);
            startService(intent);
        }

        mFetchScreenDataButton.setOnClickListener(v -> {
            Log.i("A", "Hello");

            ArrayList list;
            list = (ArrayList) RoomDBAPI.getScreenItemList();

            ToastUtil.makeLongToast(getApplicationContext(), "List Length:" + Integer.toString(list.size()));

            for (int i = 0; i < list.size(); i++) {
                ScreenItem screenItem = (ScreenItem) list.get(i);
                Log.i("G", screenItem.getState() + ":" + LongTimeToStringFormat(screenItem.getTime()));
            }
        });

        mScreenOnOffTrackingButton.setOnCheckedChangeListener(
                ((compoundButton, b) -> {
                    if(b)
                    {
                        PreferencesManager.getInstance().putBoolean("track_screen",true);
                        Intent intent = new Intent(this, YourService.class);
                        startService(intent);
                    }
                    else
                    {
                        PreferencesManager.getInstance().putBoolean("track_screen",false);
                        Intent intent = new Intent(this, YourService.class);
                        intent.putExtra("close",true);
                        startService(intent);
                    }
                })
        );

    }

    String LongTimeToStringFormat(Long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
        Date date = new Date(time);
        return simpleDateFormat.format(date);
    }

    private void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);
        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);
        // On screen logging via a customized TextView.
        LogView logView = findViewById(R.id.sample_logview);

        // Fixing this lint error adds logic without benefit.
        // noinspection AndroidLintDeprecation
        //logView.setTextAppearance(R.style.Log);

        logView.setBackgroundColor(Color.WHITE);
        msgFilter.setNext(logView);
        Log.i("TAG", "Ready");
    }


}
