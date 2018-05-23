package com.ug.eon.android.tv.prefs;

import com.ug.eon.android.tv.util.Optional;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by nemanja.todoric on 3/27/2018.
 * Preference Manager tests.
 */

public class PreferenceManagerTest {

    @Test
    public void testAuthPrefs() {
        SharedPrefsProvider sharedPrefsProvider = new MockSharedPrefsProvider();
        PreferenceManager preferenceManager = new PreferenceManagerImpl(sharedPrefsProvider);

        Optional<AuthPrefs> authPrefs = preferenceManager.getAuthPrefs();
        assertEquals(MockSharedPrefsProvider.MockAccessToken, authPrefs.get().getAccess_token());
        assertEquals(MockSharedPrefsProvider.MockRefreshToken, authPrefs.get().getRefresh_token());
        assertEquals(MockSharedPrefsProvider.MockDeviceNumber, authPrefs.get().getDevice_number());
    }

    @Test
    public void testServerPrefs() {
        SharedPrefsProvider sharedPrefsProvider = new MockSharedPrefsProvider();
        PreferenceManager preferenceManager = new PreferenceManagerImpl(sharedPrefsProvider);

        Optional<ServerPrefs> serverPrefs = preferenceManager.getServerPrefs();
        assertEquals(MockSharedPrefsProvider.MockServerEnv, serverPrefs.get().getName());
        assertEquals(MockSharedPrefsProvider.MockApiVersion, serverPrefs.get().getApiVersion());
        assertEquals(MockSharedPrefsProvider.MockImageServerUrl, serverPrefs.get().getImageServerUrl());
        assertEquals(MockSharedPrefsProvider.MockInfoServerBaseUrl, serverPrefs.get().getInfoServerBaseUrl());
        assertEquals(MockSharedPrefsProvider.MockStaticServer, serverPrefs.get().getStaticServer());
    }

    @Test
    public void testServiceProviderPrefs() {
        SharedPrefsProvider sharedPrefsProvider = new MockSharedPrefsProvider();
        PreferenceManager preferenceManager = new PreferenceManagerImpl(sharedPrefsProvider);

        Optional<ServiceProviderPrefs> serviceProviderPrefs = preferenceManager.getServiceProviderPrefs();
        assertEquals(MockSharedPrefsProvider.MockWidevineLicenceServer, serviceProviderPrefs.get().getLicenseServerUrlWidewine());
    }

}
