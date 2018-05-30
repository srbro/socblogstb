package com.ug.eon.android.tv.config;

/**
 * An interface for configuration management.
 * Interface does not assume anything regarding configuration storage.
 * It is expected to see multiple implementations such as:
 * 1. configuration (property) file
 * 2. external service (server)
 * 3. static configuration (hard-code)
 */
public interface ConfigurationManager {

    /**
     * Provides an information whether a particular feature is currently enabled/disabled
     * @param eonFeature feature to check
     * @return true if feature is enabled, otherwise false
     */
    public boolean isFeatureEnabled(EonFeature eonFeature);
}
