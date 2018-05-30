package com.ug.eon.android.tv.util;

import android.os.Build;

public class SystemInfo {

    public static int getWatchNextMinSupportedApiLevel() {
        return  Build.VERSION_CODES.O;
    }

    public static int getAndroidSDKVersion() {
        return Build.VERSION.SDK_INT;
    }
}
