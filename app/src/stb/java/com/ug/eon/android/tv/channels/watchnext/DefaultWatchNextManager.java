package com.ug.eon.android.tv.channels.watchnext;

import android.content.Context;

import com.ug.eon.android.tv.channels.ChannelsManager;

public class DefaultWatchNextManager implements WatchNextManager {

    private ChannelsManager channelsManager;

    public DefaultWatchNextManager(ChannelsManager channelsManager) {
        this.channelsManager = channelsManager;
    }

    @Override
    public boolean syncWatchNext(Context context) {
        return channelsManager.syncWatchNextChannel(context);
    }
}
