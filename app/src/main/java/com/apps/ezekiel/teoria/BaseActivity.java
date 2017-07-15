package com.apps.ezekiel.teoria;

import android.app.Notification;
import android.app.NotificationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.apps.ezekiel.teoria.database.DataAccess;
import com.apps.ezekiel.teoria.entity.QuestionItem;
import com.apps.ezekiel.teoria.networking.TeoriaRequest;
import com.apps.ezekiel.teoria.networking.VolleySingleton;
import com.apps.ezekiel.teoria.util.PersistTimes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;


public abstract class BaseActivity extends AppCompatActivity {

    public static final String TAG = "BaseActivity";

    public static final String STORAGE_FILE_NAME = "TeoriaStorageFile";

    public static final String UPDATE_URL = "http://media.mot.gov.il/XML/TheoryExamHE.xml";
    public static final int SYNC_NOTIFICATION_ID = 1;

    public static final String PREF_SYNC_FREQUENCY = "sync_frequency";
    public static final int SYNC_FREQUENCY_ON_APP_STARTS = 0;
    public static final int SYNC_FREQUENCY_HOURLY = 1;
    public static final int SYNC_FREQUENCY_DAILY = 24;
    public static final int SYNC_FREQUENCY_MANUAL = -1;
    public static final int SYNC_FREQUENCY_DEFAULT = SYNC_FREQUENCY_DAILY;

    protected Long lastPersist;
    private PersistTask persistTask;
    private NotificationManager notificationManager;
    private PersistTimes persistTimes = new PersistTimes(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (lastPersist == null) {
            lastPersist = persistTimes.getLastPersist();
        }
    }

    protected void loadData() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        makeNotification(
                getString(R.string.notif_title_sync),
                getString(R.string.notif_message_ongoing_sync),
                SYNC_NOTIFICATION_ID,
                R.drawable.ic_stat_sync
        );

        TeoriaRequest request = new TeoriaRequest(this, Request.Method.GET, UPDATE_URL,
                new Response.Listener<List<QuestionItem>>() {
                    @Override
                    public void onResponse(List<QuestionItem> response) {
                        Log.d(TAG, "onResponse");
                        persistTask = new PersistTask(response);
                        persistTask.execute();
                        Log.d(TAG, "onResponse done");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        downloadFinished(false);
                    }
                });
//        VolleySingleton.getInstance(this).addToRequestQueue(request);
        ((TeoriaApplication)getApplication()).getVolleySingleton().addToRequestQueue(request);
    }

    protected void downloadFinished(boolean success) {
        makeNotification(
                getString(R.string.notif_title_sync),
                getString(success ?
                        R.string.notif_text_sync_success :
                        R.string.notif_text_sync_fail),
                SYNC_NOTIFICATION_ID,
                R.drawable.ic_stat_sync
        );
    }

    private class PersistTask extends AsyncTask<Void, Void, Void> {

        public final String TAG = "PersistTask";
        private List<QuestionItem> items;

        public PersistTask(List<QuestionItem> items) {
            this.items = items;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "doInBackground");
            DataAccess dataAccess = new DataAccess(getApplicationContext());
            dataAccess.save(items);
            Log.d(TAG, "doInBackground done");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(TAG, "onPostExecute");
            persistTimes.recordPersistTime();
            downloadFinished(true);
            Log.d(TAG, "onPostExecute done");
        }
    }

    private void makeNotification(String title, String text, int notificationId, int iconResId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Notification syncNotification = builder.setSmallIcon(iconResId)
                .setContentTitle(title)
                .setContentText(text)
                .build();
        notificationManager.notify(notificationId, syncNotification);
    }
}
