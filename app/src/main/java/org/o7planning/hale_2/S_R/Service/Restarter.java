package org.o7planning.hale_2.S_R.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import org.o7planning.hale_2.AppUsageStats.util.PreferencesManager;
import org.o7planning.hale_2.utils.NotificationUtil;

public class Restarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Broadcast Listened", "Service tried to stop");

//        NotificationUtil.setNotification(context,"TF",Boolean.toString(PreferencesManager.getInstance().getBoolean("track_screen")),5000);

        if (PreferencesManager.getInstance().getBoolean("track_screen")) {
//            Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, YourService.class));
            } else {
                context.startService(new Intent(context, YourService.class));
            }
        }
    }
}
