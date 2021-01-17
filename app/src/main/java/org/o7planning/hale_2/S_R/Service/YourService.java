package org.o7planning.hale_2.S_R.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import org.o7planning.hale_2.AppUsageStats.DB.RoomDBAPI;
import org.o7planning.hale_2.AppUsageStats.Models.ScreenItem;
import org.o7planning.hale_2.AppUsageStats.util.PreferencesManager;
import org.o7planning.hale_2.utils.NotificationUtil;
import org.o7planning.hale_2.utils.ToastUtil;

import java.util.Timer;
import java.util.TimerTask;

public class YourService extends Service {
    public int counter = 0;
    private static BroadcastReceiver m_ScreenOffReceiver = null;

    private boolean restart = true;

    @Override
    public void onCreate() {
        super.onCreate();

//        ToastUtil.makeLongToast(getApplicationContext(), "YourService");

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

        if (m_ScreenOffReceiver == null) {
//            ToastUtil.makeLongToast(getApplicationContext(), "Registering Receiver");
        }
        registerScreenOffReceiver();

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
//        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("Monitoring Screen ON OFF")
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        boolean shouldClose = intent.getBooleanExtra("close", false);
        if (shouldClose) {
            stopSelf();
            restart = false;
        } else {
            // Continue to action here
        }

        startTimer();

        return START_STICKY;
//        return START_REDELIVER_INTENT;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stoptimertask();

//        if(PreferencesManager.getInstance().getBoolean("track_screen"))
//        {

        if (restart) {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("restartservice");
            broadcastIntent.setClass(this, Restarter.class);
            this.sendBroadcast(broadcastIntent);
        }

//        }
    }


    private Timer timer;
    private TimerTask timerTask;

    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                Log.i("Count", "=========  " + (counter++));
            }
        };
        timer.schedule(timerTask, 1000, 1000); //
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    // -------------------------------------------------------------------------
    private void registerScreenOffReceiver() {
        m_ScreenOffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {

                    RoomDBAPI.insertScreenItem(new ScreenItem("opened", System.currentTimeMillis()));
                    NotificationUtil.setNotification(context, "Screen ON", "Screen ON onReceive YourService", 1000);

                } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    RoomDBAPI.insertScreenItem(new ScreenItem("closed", System.currentTimeMillis()));
                    NotificationUtil.setNotification(context, "Screen OFF", "Screen OFF onReceive YourService", 1000);
//                    PreferencesManager.getInstance().putLong("last_screen_off",System.currentTimeMillis());
                }
            }
        };

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
//        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(m_ScreenOffReceiver, filter);
    }

}

