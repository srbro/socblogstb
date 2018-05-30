package com.ug.eon.android.tv.channels.watchnext;

import com.ug.eon.android.tv.config.ConfigurationManager;
import com.ug.eon.android.tv.config.EonFeature;

public class MockConfigurationManager implements ConfigurationManager {

    private boolean watchNextEnabled;

    @Override
    public boolean isFeatureEnabled(EonFeature eonFeature) {
        switch (eonFeature) {
            case WatchNext:
                return watchNextEnabled;
            default:
                break;
        }

        return false;
    }

    public void setWatchNextEnabled(boolean watchNextEnabled) {
        this.watchNextEnabled = watchNextEnabled;
    }

}
