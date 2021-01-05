package org.o7planning.hale_2.S_R.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import org.o7planning.hale_2.AppUsageStats.DB.RoomDBAPI;
import org.o7planning.hale_2.AppUsageStats.Models.ScreenItem;
import org.o7planning.hale_2.AppUsageStats.util.PreferencesManager;
import org.o7planning.hale_2.S_R.Service.YourService;
import org.o7planning.hale_2.utils.NotificationUtil;


// User Present works even when app killed
public class ScreenReminderBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                Log.e("ReminderBroadcast","onReceive" + intent.getAction());
//                Log.i("ReminderBroadcast","onReceive" + intent.getAction());

                if (PreferencesManager.getInstance().getBoolean("track_screen")) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(new Intent(context, YourService.class));
                    } else {
                        context.startService(new Intent(context, YourService.class));
                    }
                }

//                ToastUtil.makeLongToast(context,"ScreenReminderBroadcast"+intent.getAction());
            }
        },5000);


        if(intent.getAction().equals(Intent.ACTION_USER_PRESENT))
        {
////            ToastUtil.makeLongToast(context,"Screen ON");
            RoomDBAPI.insertScreenItem(new ScreenItem("opened",System.currentTimeMillis()));
            NotificationUtil.setNotification(context,"Screen ON","Screen ON onReceive ScreenReminderBroadcast",1000);
        }
        else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
        {
//            NotificationUtil.setNotification(context,"Screen OFF onReceive ScreenReminderBroadcast","Screen OFF",5000);
////            PreferencesManager.getInstance().putLong("last_screen_off",System.currentTimeMillis());
        }

//        NotificationUtil.setNotification(context, "m" + intent.getAction() + intent.getAction().equals(Intent.ACTION_SCREEN_OFF) ,"Screen ",4000);
//        ToastUtil.makeLongToast(context,"Screen Receiver Called" + Long.toString(System.currentTimeMillis()));
    }

}

