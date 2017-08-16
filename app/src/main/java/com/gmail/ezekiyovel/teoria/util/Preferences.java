package com.gmail.ezekiyovel.teoria.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.gmail.ezekiyovel.teoria.SettingsActivity;
import com.gmail.ezekiyovel.teoria.entity.QuestionItem;

/**
 * Created by Hezkel on 14/08/17.
 */

public class Preferences {

    public static long getClassFromPreferences(Context context){
        SharedPreferences defaultPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return QuestionItem.binClassForStrClass(defaultPreferences
                .getString(SettingsActivity.LICENCE_CLASS,
                        SettingsActivity.DEFAULT_LICENCE_CLASS));
    }
}
