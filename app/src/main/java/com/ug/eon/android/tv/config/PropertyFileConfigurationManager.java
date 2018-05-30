package com.ug.eon.android.tv.config;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;

import com.ug.eon.android.tv.util.SystemInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * An implementation of {@link ConfigurationManager} interface with configuration held in property
 * file.
 */
public class PropertyFileConfigurationManager implements ConfigurationManager {

    private static final String TAG = PropertyFileConfigurationManager.class.getName();
    private static final String FEATURES_PROPERTY_FILE = "config/features.properties";

    public static final String WATCH_NEXT_PROPERTY = "watchNext";
    public static final String WATCH_NEXT_MIN_API = "watchNextMinAPI";

    private Properties properties;

    public PropertyFileConfigurationManager(Context context, PropertyFileProvider propertyFileProvider) {
        properties = propertyFileProvider.getProperties(context, FEATURES_PROPERTY_FILE);
    }

    /**
     * Return whether a feature is enabled or disabled.
     * If property is not in the property file, method defaults to true.
     *
     * @param eonFeature feature to check
     * @return true if feature is enabled, otherwise false
     */
    @Override
    public boolean isFeatureEnabled(EonFeature eonFeature) {
        switch (eonFeature) {
            case WatchNext:
                return isWatchNextEnabled() && isWatchNextEnabledByMinApiLevel();
            default:
                break;
        }

        Log.d(TAG, "Unknown feature requested: isFeatureEnabled()");
        return false;
    }

    private boolean isWatchNextEnabledByMinApiLevel() {
        // Watch Next is introduced in Android O
        int watchNextMinSupportedVersionResult = SystemInfo.getWatchNextMinSupportedApiLevel();
        String watchNextMinApiProperty = properties.getProperty(WATCH_NEXT_MIN_API);

        if (watchNextMinApiProperty != null) {
            try {
                int watchNextMinApiVal = Integer.parseInt(watchNextMinApiProperty);
                // Get maximum of Android O or configured param.
                watchNextMinSupportedVersionResult = Math.max(watchNextMinSupportedVersionResult, watchNextMinApiVal);
            } catch (NumberFormatException e) {
                Log.d(TAG, WATCH_NEXT_MIN_API + " config param has invalid value: " + watchNextMinApiProperty);
            }
        }

        return SystemInfo.getAndroidSDKVersion() >= watchNextMinSupportedVersionResult;
    }

    private boolean isWatchNextEnabled() {
        String watchNextEnabled = properties.getProperty(WATCH_NEXT_PROPERTY);
        if (watchNextEnabled == null)
            return true; // default is enabled

        return watchNextEnabled.equals("true");
    }
}
