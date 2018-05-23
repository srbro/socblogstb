package com.ug.eon.android.tv.prefs;

import com.ug.eon.android.tv.util.Optional;

/**
 * Created by nemanja.todoric on 3/27/2018.
 */

public interface PreferenceManager {

    /*
     * Returns entire value for preference key
     * If 'key' does not exist, method will return empty String
     */
    public Optional<String> getValue(String key);

    /*
     * Returns parsed auth preference via AuthPrefs object
     */
    public Optional<AuthPrefs> getAuthPrefs();

    /*
     * Convenient method for auth token
     */
    public Optional<String> getAuthToken();

    /*
     * Return server preferences
     */
    public Optional<ServerPrefs> getServerPrefs();

    /*
     * Return Service Provider preferences
     */
    public Optional<ServiceProviderPrefs> getServiceProviderPrefs();

    /*
     * Sets the value for preference key
     */
    public void setValue(String key, String value);

    /*
     * Removes a "key" from the preferences
     */
    public void remove(String key);
}
