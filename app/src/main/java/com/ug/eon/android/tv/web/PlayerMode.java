package com.ug.eon.android.tv.web;

/**
 * Created by milan.adamovic on 3/20/18.
 */

public enum PlayerMode {
    DVB_C, OTT;

    public static boolean isDvb(PlayerMode playerMode) {
        return playerMode == DVB_C;
    }

    public static boolean isOtt(PlayerMode playerMode) {
        return playerMode == OTT;
    }
}
