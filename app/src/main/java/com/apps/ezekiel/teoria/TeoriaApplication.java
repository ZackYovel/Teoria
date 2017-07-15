package com.apps.ezekiel.teoria;

import android.app.Application;

import com.apps.ezekiel.teoria.networking.VolleySingleton;

/**
 * Created by eezekiel on 7/15/17.
 */

public class TeoriaApplication extends Application {

    private VolleySingleton volleySingleton;

    @Override
    public void onCreate() {
        super.onCreate();
        volleySingleton = VolleySingleton.getInstance(this);
    }

    public VolleySingleton getVolleySingleton(){
        return volleySingleton;
    }
}
