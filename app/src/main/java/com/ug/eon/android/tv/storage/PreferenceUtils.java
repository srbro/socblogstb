package com.ug.eon.android.tv.storage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by milan.adamovic on 3/19/18.
 */
public class PreferenceUtils {
    private static final String EON_PREFERENCE_STORE = "eon.preferences";

    /**
     * Sets boolean value in private eon preferences.
     * @param context Context.
     * @param key Key
     * @param value Boolean value.
     */
    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getEditor(context.getApplicationContext());
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Gets boolean value from private eon preference for given key.
     * @param context Context.
     * @param key Value key.
     */
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(EON_PREFERENCE_STORE
                , Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    public static void setValue(Context context, String key, long value) {
        SharedPreferences.Editor editor = getEditor(context.getApplicationContext());
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getValue(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(EON_PREFERENCE_STORE
                , Context.MODE_PRIVATE);
        return preferences.getLong(key, -1);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(EON_PREFERENCE_STORE
                , Context.MODE_PRIVATE);
        return preferences.edit();
    }
}