package org.o7planning.hale_2.AppUsageStats.app;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AlertDialog;

import org.o7planning.hale_2.AppUsageStats.DB.RoomDBAPI;
import org.o7planning.hale_2.AppUsageStats.Models.IgnoreItem;
import org.o7planning.hale_2.AppUsageStats.Service.AppService;
import org.o7planning.hale_2.AppUsageStats.data.DataManager;
import org.o7planning.hale_2.AppUsageStats.util.PreferencesManager;
import org.o7planning.hale_2.BuildConfig;
import org.o7planning.hale_2.utils.NotificationUtil;
import org.o7planning.hale_2.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * My Application
 * Created by zb on 18/12/2017.
 */

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

//        ToastUtil.makeLongToast(getApplicationContext(),"My Application");

        PreferencesManager.init(getApplicationContext());
        getApplicationContext().startService(new Intent(getApplicationContext(), AppService.class));

        RoomDBAPI.init((Application) getApplicationContext());
//        zz
//        DbIgnoreExecutor.init(getApplicationContext());
//        DbHistoryExecutor.init(getApplicationContext());
        DataManager.init(getApplicationContext());
//        if (AppConst.CRASH_TO_FILE) CrashHandler.getInstance().init();
        addDefaultIgnoreAppsToDB();

        NotificationUtil.createChannel(getApplicationContext(), "primary_notification_channel");
    }

    private void addDefaultIgnoreAppsToDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> mDefaults = new ArrayList<>();
                mDefaults.add("com.android.settings");
                mDefaults.add(BuildConfig.APPLICATION_ID);
                for (String packageName : mDefaults) {

//                    AppItem item = new AppItem();
                    IgnoreItem item = new IgnoreItem();

                    item.mPackageName = packageName;
                    item.mCreated = System.currentTimeMillis();
//                    zz
//                    DbIgnoreExecutor.getInstance().insertItem(item);
                    RoomDBAPI.insertIgnoreItem(item);
                }
            }
        }).run();
    }

}
