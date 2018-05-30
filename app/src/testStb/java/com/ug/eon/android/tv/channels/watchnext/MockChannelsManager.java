package com.ug.eon.android.tv.channels.watchnext;

import android.content.Context;

import com.ug.eon.android.tv.channels.ChannelsManager;

public class MockChannelsManager implements ChannelsManager {

    @Override
    public boolean syncMainEonChannel(Context context) {
        return true;
    }

    @Override
    public boolean syncWatchNextChannel(Context context) {
        return true;
    }
}
