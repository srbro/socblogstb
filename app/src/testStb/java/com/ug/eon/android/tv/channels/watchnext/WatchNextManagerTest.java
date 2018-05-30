package com.ug.eon.android.tv.channels.watchnext;

import android.content.Context;

import com.ug.eon.android.tv.channels.ChannelsManager;
import com.ug.eon.android.tv.config.FeatureFactory;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class WatchNextManagerTest {

    @Test
    public void syncWatchNextFeatureEnabledTest() {
        boolean requestAccepted = syncWatchNext(true);
        assertTrue(requestAccepted);
    }

    @Test
    public void syncWatchNextFeatureDisabledTest() {
        boolean requestAccepted = syncWatchNext(false);
        assertFalse(requestAccepted);
    }

    private boolean syncWatchNext(boolean featureEnabled) {
        Context context = mock(Context.class);
        ChannelsManager channelsManager = new MockChannelsManager();
        MockConfigurationManager configurationManager = new MockConfigurationManager();
        configurationManager.setWatchNextEnabled(featureEnabled);
        WatchNextManager watchNextManager = FeatureFactory.getWatchNextManager(channelsManager, configurationManager);
        return watchNextManager.syncWatchNext(context);
    }
}