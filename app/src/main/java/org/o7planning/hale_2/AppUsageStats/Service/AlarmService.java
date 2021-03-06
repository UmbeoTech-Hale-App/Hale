package org.o7planning.hale_2.AppUsageStats.Service;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;


import org.o7planning.hale_2.AppUsageStats.DB.RoomDBAPI;
import org.o7planning.hale_2.AppUsageStats.Models.AppItem;
import org.o7planning.hale_2.AppUsageStats.Models.HistoryItem;
import org.o7planning.hale_2.AppUsageStats.data.DataManager;
import org.o7planning.hale_2.AppUsageStats.util.AlarmUtil;
import org.o7planning.hale_2.AppUsageStats.util.AppUtil;
import org.o7planning.hale_2.utils.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Alarm service
 * Created by zb on 02/01/2018.
 */

public class AlarmService extends IntentService {

    private static final String ALARM_SERVICE_NAME = "alarm.service";

    public AlarmService() {
        super(ALARM_SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        ToastUtil.makeLongToast(getApplicationContext(),"Alarm Service");

        DataManager manager = DataManager.getInstance();
        List<AppItem> items = manager.getApps(this.getApplicationContext(), 0, 1);
        for (AppItem item : items) {
            HistoryItem historyItem = new HistoryItem();
            historyItem.mName = item.mName;
            historyItem.mPackageName = item.mPackageName;
            historyItem.mMobileTraffic = item.mMobile;
            historyItem.mIsSystem = AppUtil.isSystemApp(getPackageManager(), item.mPackageName) ? 1 : 0;
            historyItem.mDuration = item.mUsageTime;
            historyItem.mTimeStamp = AppUtil.getYesterdayTimestamp();
            historyItem.mDate = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(new Date(historyItem.mTimeStamp));

            RoomDBAPI.insertHistoryItem(historyItem);
        }

//        FileLogManager fileLogManager = FileLogManager.getInstance();
//        fileLogManager.log("alarm " + new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(new Date(System.currentTimeMillis())) + "\n");

        AlarmUtil.setAlarm(this.getApplicationContext());
    }
}

