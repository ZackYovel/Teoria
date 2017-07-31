package com.apps.ezekiel.teoria;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.apps.ezekiel.teoria.fragment.SimulationFragment;
import com.apps.ezekiel.teoria.fragment.TrainingFragment;

/**
 * Displays the main activity - shows one of two fragments: by default, a
 * {@link com.apps.ezekiel.teoria.fragment.TrainingFragment}, switchable via menu action to
 * {@link com.apps.ezekiel.teoria.fragment.SimulationFragment}
 */
public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        TrainingFragment.Listener, SimulationFragment.Listener {

    public static final String PREF_DEFAULT_FOR_MAIN_ACTIVITY = "default_for_main_activity";
    private static final String PREF_SELECTED_FOR_MAIN_ACTIVITY = "selected_for_main_activity";

    private static final String TAG = "MainActivity";

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setIcon(R.mipmap.ic_launcher);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        } else {
            Log.e(TAG, "drawer is null in onCreate");
        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        } else {
            Log.e(TAG, "navigationView is null in onCreate");
        }

        if (lastPersist != null){
            initFragment(null);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_title_need_update)
                    .setMessage(R.string.dialog_message_need_update)
                    .setIcon(R.drawable.ic_alert);
            builder.create().show();
        }
    }

    private void initFragment(String selectedTask) {
        Log.d(TAG, "initFragment - start");

        String training = getString(R.string.title_activity_training);

        if (selectedTask == null || selectedTask.isEmpty()) {
            preferences = PreferenceManager.getDefaultSharedPreferences(this);

            String selectedTaskPref =
                    preferences.getString(PREF_DEFAULT_FOR_MAIN_ACTIVITY, training);
            String selection = selectedTaskPref;
            if (selectedTaskPref.equals("last selected")) {
                selection =
                        preferences.getString(PREF_SELECTED_FOR_MAIN_ACTIVITY, selectedTaskPref);
            }
            selectedTask = selection;
        }

        Fragment fragment;
        if (selectedTask.equals(training)) {
            fragment = getSupportFragmentManager()
                    .findFragmentByTag(TrainingFragment.class.getName());
            if (fragment == null) {
                fragment = new TrainingFragment();
            }
        } else {
            fragment = getSupportFragmentManager()
                    .findFragmentByTag(SimulationFragment.class.getName());
            if (fragment == null) {
                fragment = new SimulationFragment();
            }
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rl_fragment_container, fragment, fragment.getClass().getName())
                .commitAllowingStateLoss();

        Log.d(TAG, "initFragment - done");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_simulation) {
            startLearningTask(getString(R.string.title_activity_simulation));
        } else if (id == R.id.nav_training) {
            startLearningTask(getString(R.string.title_activity_training));
        } else if (id == R.id.nav_update) {
            loadData();
        } else if (id == R.id.nav_stats){
            Toast.makeText(this, "stats", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_credits) {
            startInfoActivity(R.string.title_activity_credits);
        } else if (id == R.id.nav_about) {
            startInfoActivity(R.string.title_activity_about);
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Log.e(TAG, "drawer is null in onNavigationItemSelected");
        }
        return true;
    }

    private void startInfoActivity(int title_activity_credits) {
        Intent intent = new Intent(this, AboutActivity.class);
        intent.putExtra(AboutActivity.EXTRA_ABOUT_ACTIVITY_TITLE,
                title_activity_credits);
        startActivity(intent);
    }

    private void startLearningTask(String taskTitle) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_SELECTED_FOR_MAIN_ACTIVITY, taskTitle);
        editor.apply();
        initFragment(taskTitle);
    }

    @Override
    public void onSimulationRequested() {
        startLearningTask(getString(R.string.title_activity_simulation));
    }

    @Override
    public void onTrainingRequested() {
        startLearningTask(getString(R.string.title_activity_training));
    }
}
