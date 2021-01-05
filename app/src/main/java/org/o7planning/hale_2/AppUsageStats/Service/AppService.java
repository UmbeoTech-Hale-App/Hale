package org.o7planning.hale_2.AppUsageStats.Service;

import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.o7planning.hale_2.AppUsageStats.data.DataManager;
import org.o7planning.hale_2.MainActivity;
import org.o7planning.hale_2.R;
import org.o7planning.hale_2.utils.ToastUtil;


public class AppService extends Service {

    public static final String SERVICE_ACTION = "service_action";
    public static final String SERVICE_ACTION_CHECK = "service_action_check";

    static final long CHECK_INTERVAL = 400;

    private DataManager mManager;
    private Context mContext;
    private Handler mHandler = new Handler();

    private Runnable mRepeatCheckTask = new Runnable() {
        @Override
        public void run() {
//            ToastUtil.makeLongToast(getApplicationContext(),"Running AppService");
            if (!mManager.hasPermission(mContext)) {
                mHandler.postDelayed(mRepeatCheckTask, CHECK_INTERVAL);
            } else {
                mHandler.removeCallbacks(mRepeatCheckTask);
                Toast.makeText(mContext, "grant_success", Toast.LENGTH_SHORT).show();
                startService(new Intent(mContext, AlarmService.class));
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mManager = new DataManager();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getStringExtra(SERVICE_ACTION);
            if (!TextUtils.isEmpty(action)) {
                switch (action) {
                    case SERVICE_ACTION_CHECK:
                        startIntervalCheck();
                        break;
                }
            }
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        try {
            startService(new Intent(mContext, AppService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startIntervalCheck() {
        boolean valid = true;
        try {
            mManager.requestPermission(mContext);
        } catch (ActivityNotFoundException e) {
            valid = false;
        }
        if (valid) {
            ToastUtil.makeLongToast(mContext,"Please Find Hale App and grant permission");
            mHandler.post(mRepeatCheckTask);
        } else {
            ToastUtil.makeLongToast(mContext,"Your device does not support this feature");
        }
    }

}

