package com.apps.ezekiel.teoria;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

public class AboutActivity extends AppCompatActivity {

    public static final String EXTRA_ABOUT_ACTIVITY_TITLE = "AboutActivity.ABOUT_ACTIVITY_TITLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        int titleResId =
                getIntent().getIntExtra(EXTRA_ABOUT_ACTIVITY_TITLE, R.string.title_activity_about);

        setTitle(titleResId);

        WebView wvCredits = (WebView) findViewById(R.id.webView);
        if (wvCredits != null) {
            switch (titleResId){
                case R.string.title_activity_credits:
                    wvCredits.loadUrl("file:///android_asset/credits.html");
                    break;
                case R.string.title_activity_about:
                default:
                    wvCredits.loadUrl("file:///android_asset/about.html");
            }
        }
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

}