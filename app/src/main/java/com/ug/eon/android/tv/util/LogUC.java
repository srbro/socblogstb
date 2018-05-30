package com.ug.eon.android.tv.util;

import android.text.TextUtils;
import android.util.Log;

import com.ug.eon.android.tv.BuildConfig;

import java.lang.reflect.Field;

import static android.util.Log.VERBOSE;

/**
 * Wrapper around Android logcat for simpler use in UC application
 *
 */
public class LogUC {

    public static void d(String tag, String msg) {
        log(Log.DEBUG, tag, msg);
    }

    public static void i(String tag, String msg) {
        log(Log.INFO, tag, msg);
    }

    public static void e(String tag, String msg) {
        log(Log.ERROR, tag, msg);
    }

    public static void v(String tag, String msg) {
        log(Log.VERBOSE, tag, msg);
    }

    public static void w(String tag, String msg) {
        log(Log.WARN, tag, msg);
    }

    private static void log(int level, String tag, String msg) {
        if (BuildConfig.DEBUG && !TextUtils.isEmpty(msg)) {
            Log.println(level, tag, msg);
        }
    }
}