package com.ug.eon.android.tv.util;

/**
 * Created by ucmilos on 2/6/17.
 */

public interface EventListener {
    int EVENT_ERROR = 1;
    int EVENT_STOPPED = 2;
    int EVENT_PLAYED = 3;
    int EVENT_DVB_ERROR = 4;

    void sendEvent(int event);
    void onBitrateChange(int bitrate);
}