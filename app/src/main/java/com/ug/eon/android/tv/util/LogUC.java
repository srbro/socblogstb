package com.ug.eon.android.tv.util;

import android.util.Log;

import java.lang.reflect.Field;

import static android.util.Log.VERBOSE;

/**
 * Wrapper around Android logcat for simpler use in UC application
 *
 * Created by nikola.djokic on 11/30/2016.
 */

public class LogUC {
    /**
     * Logcat output tag
     */
    public static final String TAG = "UC_LOG";

    private static boolean SHOW_LOG = true;

    /**
     * Priority constant for defining logging level
     *
     * Log levels from @{@link Log} class
     * public static final int VERBOSE = 2;
     * public static final int DEBUG = 3;
     * public static final int INFO = 4;
     * public static final int WARN = 5;
     * public static final int ERROR = 6;
     * public static final int ASSERT = 7;
     *
     */
    private static final int LOG_LEVEL = VERBOSE;


    public static void enableLoging(){
        Log.i(TAG, "UC_LOGCAT enabled");
        SHOW_LOG = true;
    }

    public static void disableLoging() {
        Log.i(TAG, "UC_LOGCAT disabled");
        SHOW_LOG = false;
    }

    /**
     * Outputs an verbose log message with @TAG tag
     * @param msg			Log message to output to the console.
     */
    public static void v(String msg)
    {
        if (SHOW_LOG && LOG_LEVEL <= Log.VERBOSE )
            Log.v(TAG, msg);
    }

    /**
     * Outputs a verbose log message with different tag
     * @param tag           Tag for message
     * @param msg			Log message to output to the console.
     */
    public static void v(String tag, String msg) { //TODO implement this for all log levels
        if (SHOW_LOG && LOG_LEVEL <= Log.VERBOSE)
            Log.v(tag, msg);
    }

    /**
     * Outputs and debug log message with @TAG tag
     * @param msg			Log message to output to the console.
     */
    public static void d(String msg)
    {
        if (SHOW_LOG && LOG_LEVEL <= Log.DEBUG )
            Log.d(TAG, msg);
    }

    /**
     * Outputs a debug log message with different tag
     * @param tag           Tag for message
     * @param msg			Log message to output to the console.
     */
    public static void d(String tag, String msg) { //TODO implement this for all log levels
        if (SHOW_LOG && LOG_LEVEL <= Log.DEBUG)
            Log.d(tag, msg);
    }

    /**
     * Outputs an info log message with @TAG tag
     * @param msg			Log message to output to the console.
     */
    public static void i(String msg)
    {
        if (SHOW_LOG && LOG_LEVEL <= Log.INFO )
            Log.i(TAG, msg);
    }

    /**
     * Outputs a info log message with different tag
     * @param tag           Tag for message
     * @param msg			Log message to output to the console.
     */
    public static void i(String tag, String msg) { //TODO implement this for all log levels
        if (SHOW_LOG && LOG_LEVEL <= Log.INFO)
            Log.i(tag, msg);
    }

    /**
     * Outputs an warn log message with @TAG tag
     * @param msg			Log message to output to the console.
     */
    public static void w(String msg)
    {
        if (SHOW_LOG && LOG_LEVEL <= Log.WARN )
            Log.w(TAG, msg);
    }

    /**
     * Outputs a warn log message with different tag
     * @param tag           Tag for message
     * @param msg			Log message to output to the console.
     */
    public static void w(String tag, String msg) { //TODO implement this for all log levels
        if (SHOW_LOG && LOG_LEVEL <= Log.WARN)
            Log.w(tag, msg);
    }

    /**
     * Outputs an onError log message with @TAG tag
     * @param msg			Log message to output to the console.
     */
    public static void e(String msg)
    {
        if (SHOW_LOG && LOG_LEVEL <= Log.ERROR )
            Log.e(TAG, msg);
    }

    /**
     * Outputs a onError log message with different tag
     * @param tag           Tag for message
     * @param msg			Log message to output to the console.
     */
    public static void e(String tag, String msg) { //TODO implement this for all log levels
        if (SHOW_LOG && LOG_LEVEL <= Log.ERROR)
            Log.e(tag, msg);
    }

    /**
     * This method uses reflection to access all fields of object and outputs them to logcat debug channel
     *  <STRONG>Only to be used in development</STRONG>
     * @param o object to be loged
     */
    public static void logObject(Object o) {
        if (SHOW_LOG) {
            Class c = o.getClass();
            for (Field field : c.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Log.d(TAG, field.getName() + " : " + field.get(o));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
