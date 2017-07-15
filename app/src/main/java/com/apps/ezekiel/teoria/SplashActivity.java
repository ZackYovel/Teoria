package com.apps.ezekiel.teoria;

import android.app.AlarmManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.apps.ezekiel.teoria.fragment.WelcomeSlideFragment;
import com.apps.ezekiel.teoria.networking.SyncAlarmReceiver;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends BaseActivity {

    private static final long TIMER_DELAY = 1500;
    private static final String PREF_APP_FIRST_RUN = "SplashActivity.PREF_APP_FIRST_RUN";
    private static final int MESSAGE_WELCOME_SLIDES = 1;
    public static final String TAG = "SplashActivity";

    private SyncAlarmReceiver syncAlarmReceiver = new SyncAlarmReceiver();

    private boolean timerUp;
    private boolean downloadDone;
    private boolean nextRan;

//    private Handler handler = new Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what == MESSAGE_WELCOME_SLIDES) {
//                initWelcomeSlides();
//            }
//        }
//    };
    private ImageView imageView;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        }

        SharedPreferences defaultSharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        boolean appFirstRun = defaultSharedPreferences
                .getBoolean(PREF_APP_FIRST_RUN, true);

        if (appFirstRun) {
            defaultSharedPreferences.edit().putBoolean(PREF_APP_FIRST_RUN, false).apply();
        }

        int syncFrequency = Integer.parseInt(defaultSharedPreferences
                .getString(PREF_SYNC_FREQUENCY, String.valueOf(SYNC_FREQUENCY_DEFAULT)));

        if (syncFrequency == SYNC_FREQUENCY_ON_APP_STARTS || appFirstRun) {
            loadData();
        }

        setTimer();

        if (syncFrequency == SYNC_FREQUENCY_HOURLY || syncFrequency == SYNC_FREQUENCY_DAILY) {
            setSyncAlarm(syncFrequency);
        }
    }

    private void setTimer() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                timerUp = true;
                if (downloadDone /* || lastPersist != null */) {
                    Log.d(TAG, "timer up");
                    next();
//                } else {
//                    handler.obtainMessage(MESSAGE_WELCOME_SLIDES).sendToTarget();
                }
            }
        }, TIMER_DELAY);
    }

//    private void initWelcomeSlides() {
//        imageView = (ImageView) findViewById(R.id.imageView);
//        viewPager = (ViewPager) findViewById(R.id.viewPager);
//        imageView.setVisibility(View.GONE);
//        viewPager.setVisibility(View.VISIBLE);
//        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
//            private final int[] screens = {
//                    R.drawable.screen1,
//                    R.drawable.screen2,
//                    R.drawable.screen3,
//                    R.drawable.screen4,
//                    R.drawable.screen5
//            };
//
//            @Override
//            public Fragment getItem(int position) {
//                return WelcomeSlideFragment.newInstance(screens[position]);
//            }
//
//            @Override
//            public int getCount() {
//                return screens.length;
//            }
//        });
//    }

    @Override
    protected void downloadFinished(boolean success) {
        downloadDone = true;
        if (timerUp) {
            Log.d(TAG, "download done, success = " + success);
//            if (lastPersist != null) {
                next();
//            }
        }
        super.downloadFinished(success);
    }

    private void next() {
        if (!nextRan) {
            startActivity(new Intent(this, MainActivity.class));
            nextRan = true;
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        // Don't close the activity
        viewPager.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
    }

    public void setSyncAlarm(int syncFrequencyPreference) {
        syncAlarmReceiver.cancelAlarm(this);
        long interval = syncFrequencyPreference == SYNC_FREQUENCY_DAILY ?
                AlarmManager.INTERVAL_DAY :
                AlarmManager.INTERVAL_HALF_HOUR;
        syncAlarmReceiver.setAlarm(this, interval);
    }
}
