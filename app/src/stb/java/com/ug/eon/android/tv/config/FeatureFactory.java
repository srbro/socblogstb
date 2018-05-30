package com.ug.eon.android.tv.config;

import android.util.Log;

import com.ug.eon.android.tv.channels.ChannelsManager;
import com.ug.eon.android.tv.channels.watchnext.DefaultWatchNextManager;
import com.ug.eon.android.tv.channels.watchnext.NoOpWatchNextManager;
import com.ug.eon.android.tv.channels.watchnext.WatchNextManager;

public class FeatureFactory {

    private static final String TAG = FeatureFactory.class.getName();

    /**
     * Obtain an implentation of {@link WatchNextManager} based on current configuration.
     * @param channelsManager an implementation of {@link ChannelsManager} interface.
     * @param configurationManager
     * @return
     */
    public static WatchNextManager getWatchNextManager(ChannelsManager channelsManager, ConfigurationManager configurationManager) {
        if (configurationManager.isFeatureEnabled(EonFeature.WatchNext)) {
            Log.i(TAG, "Watch Next feature is enabled");
            return new DefaultWatchNextManager(channelsManager);
        }

        Log.i(TAG, "Watch Next feature is disabled");
        return new NoOpWatchNextManager();
    }
}
