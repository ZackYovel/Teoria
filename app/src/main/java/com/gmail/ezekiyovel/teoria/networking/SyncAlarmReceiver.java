package com.gmail.ezekiyovel.teoria.networking;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class SyncAlarmReceiver extends BroadcastReceiver {

    public static final String SYNC_ALARM_INTENT = "com.gmail.ezekiyovel.SYNC_ALARM_INTENT";
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    public SyncAlarmReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, SyncService.class);
        context.startService(service);
    }

    public void setAlarm(Context context, long interval) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(SYNC_ALARM_INTENT);
        alarmIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        boolean alarmUp =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE) !=
                        null;

        if (!alarmUp) {
            alarmManager.setInexactRepeating(
                    AlarmManager.ELAPSED_REALTIME,
                    interval,
                    interval,
                    alarmIntent
            );
        }

        ComponentName receiver = new ComponentName(context, SyncAlarmReceiver.class);
        PackageManager pacMan = context.getPackageManager();

        pacMan.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
        );
    }

    public void cancelAlarm(Context context) {
        if (alarmManager != null) {
            alarmManager.cancel(alarmIntent);
        }

        ComponentName receiver = new ComponentName(context, SyncAlarmReceiver.class);
        PackageManager pacMan = context.getPackageManager();

        pacMan.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
        );
    }
}
