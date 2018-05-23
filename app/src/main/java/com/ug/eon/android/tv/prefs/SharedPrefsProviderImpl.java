package com.ug.eon.android.tv.prefs;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by nemanja.todoric on 3/27/2018.
 */

public class SharedPrefsProviderImpl implements SharedPrefsProvider {

    private static final String PREFS_NAME = "EData";
    private Context context;

    public SharedPrefsProviderImpl(Context c) {
        context = c;
    }

    @Override
    public String getString(String key, String defaultValue) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPrefs.getString(key, defaultValue);
    }

    @Override
    public void setString(String key, String value) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    @Override
    public void remove(String key) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.remove(key);
        editor.apply();
    }
}
