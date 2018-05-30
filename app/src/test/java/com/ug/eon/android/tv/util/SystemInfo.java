package com.ug.eon.android.tv.util;

public class SystemInfo {

    public static int WATCH_NEXT_MIN_API_LEVEL = 26;
    public static int ANDROID_SDK_VERSION = 26;

    public static void resetToDefaults() {
        WATCH_NEXT_MIN_API_LEVEL = 26;
        ANDROID_SDK_VERSION = 26;
    }

    public static int getWatchNextMinSupportedApiLevel() {
        return WATCH_NEXT_MIN_API_LEVEL;
    }

    public static int getAndroidSDKVersion() {
        return ANDROID_SDK_VERSION;
    }
}
