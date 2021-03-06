package org.o7planning.hale_2.AppUsageStats.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.o7planning.hale_2.AppUsageStats.Service.AlarmService;
import org.o7planning.hale_2.utils.ToastUtil;

/**
 * Alarm receiver
 * Created by zb on 02/01/2018.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, AlarmService.class));
        ToastUtil.makeShortToast(context,"AlarmReceiver");
    }
}

