package com.apps.ezekiel.teoria.networking;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.apps.ezekiel.teoria.BaseActivity;
import com.apps.ezekiel.teoria.R;
import com.apps.ezekiel.teoria.TeoriaApplication;
import com.apps.ezekiel.teoria.database.DataAccess;
import com.apps.ezekiel.teoria.entity.QuestionItem;
import com.apps.ezekiel.teoria.util.PersistTimes;

import java.util.List;
import java.util.Random;

public class SyncService extends IntentService {

    public static final int MAX_DELAY_IN_SECONDS = 10;
    private NotificationManager notificationManager;

    public SyncService() {
        super("SyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();
        notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            Thread.sleep(new Random().nextInt(MAX_DELAY_IN_SECONDS) * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sync(context);
    }

    private void sync(final Context context) {
        makeNotification(
                context,
                context.getString(R.string.notif_title_sync),
                context.getString(R.string.notif_message_ongoing_sync),
                BaseActivity.SYNC_NOTIFICATION_ID,
                R.drawable.ic_stat_sync
        );
//        VolleySingleton.getInstance(context).addToRequestQueue(new TeoriaRequest(
        ((TeoriaApplication)getApplication())
                .getVolleySingleton().addToRequestQueue(new TeoriaRequest(
                context, Request.Method.GET, BaseActivity.UPDATE_URL,
                new Response.Listener<List<QuestionItem>>() {
                    @Override
                    public void onResponse(List<QuestionItem> response) {
                        DataAccess.saveQuestions(context, response);
                        PersistTimes.recordPersistence(context);
                        makeNotification(
                                context,
                                context.getString(R.string.notif_title_sync),
                                context.getString(R.string.notif_text_sync_success),
                                BaseActivity.SYNC_NOTIFICATION_ID,
                                R.drawable.ic_stat_sync
                        );
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                makeNotification(
                        context,
                        context.getString(R.string.notif_title_sync),
                        context.getString(R.string.notif_text_sync_fail),
                        BaseActivity.SYNC_NOTIFICATION_ID,
                        R.drawable.ic_stat_sync
                );
            }
        }));
    }

    private void makeNotification(Context context, String title, String text, int notificationId,
                                  int iconResId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification syncNotification = builder.setSmallIcon(iconResId)
                .setContentTitle(title)
                .setContentText(text)
                .build();
        notificationManager.notify(notificationId, syncNotification);
    }
}
