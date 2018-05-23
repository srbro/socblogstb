package com.ug.eon.android.tv.prefs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ug.eon.android.tv.util.Optional;


/**
 * Created by nemanja.todoric on 3/13/2018.
 * Implementation of PreferenceManager interface. Provides information about
 * 1. Authentication tokens and related data
 * 2. Server information, such as Info Server URL
 * 3. Service Provider information, such as licence server URL used when playing DRM protected content
 */

public class PreferenceManagerImpl implements PreferenceManager {

    private static final String TAG = PreferenceManagerImpl.class.getName();

    private static final String PREF_AUTH = "accessObj";
    private static final String PREF_SERVERS = "servers";
    private static final String PREF_SERVICE_PROVIDER = "serviceProviders";

    private SharedPrefsProvider sharedPrefsProvider;

    public PreferenceManagerImpl(SharedPrefsProvider sharedPrefsProvider) {
        this.sharedPrefsProvider = sharedPrefsProvider;
    }

    @Override
    public Optional<String> getValue(String key) {
        String result = sharedPrefsProvider.getString(key, null);
        if(result != null) {
            return Optional.of(result);
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getAuthToken() {
        return getAuthPrefs().map(AuthPrefs::getAccess_token);
    }

    @Override
    public Optional<AuthPrefs> getAuthPrefs() {
        return getPrefVal(PREF_AUTH, AuthPrefs.class);
    }

    @Override
    public Optional<ServerPrefs> getServerPrefs() {
        return getPrefVal(PREF_SERVERS, ServerPrefs.class);
    }

    @Override
    public Optional<ServiceProviderPrefs> getServiceProviderPrefs() {
        return getPrefVal(PREF_SERVICE_PROVIDER, ServiceProviderPrefs.class);
    }

    private <T> Optional<T> getPrefVal(String prefName, Class<T> type) {
        return getValue(prefName).map((String preferences) -> {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            return gson.fromJson(preferences, type);
        });
    }

    @Override
    public void setValue(String key, String value) {
        sharedPrefsProvider.setString(key, value);
    }

    @Override
    public void remove(String key) {
        sharedPrefsProvider.remove(key);
    }
}
