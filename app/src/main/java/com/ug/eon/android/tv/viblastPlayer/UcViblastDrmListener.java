package com.ug.eon.android.tv.viblastPlayer;

import android.text.TextUtils;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.ug.eon.android.tv.drm.DrmInfoProvider;
import com.ug.eon.android.tv.prefs.PreferenceManager;
import com.ug.eon.android.tv.prefs.ServiceProviderPrefs;
import com.ug.eon.android.tv.util.Optional;
import com.viblast.android.drm.ViblastDrmCallback;
import com.viblast.android.drm.ViblastDrmInfo;

import java.util.Set;
import java.util.UUID;

/**
 * Created by talic on 12/4/17.
 * DRM callback class. Invoked by Viblast when DRM protected channel is played.
 */
public class UcViblastDrmListener implements ViblastDrmCallback {
    private static final String TAG = UcViblastDrmListener.class.getName();
    private PreferenceManager preferenceManager;
    private String mWidevineLicenceServerUrl;
    private String mDrmToken;

    public UcViblastDrmListener(PreferenceManager preferenceManager) {
        this.preferenceManager = preferenceManager;
        mWidevineLicenceServerUrl = getWidevineLicenceServerUrl();
    }

    @Override
    public ViblastDrmInfo onDrmInfoNeed(Set<UUID> drms) {
        if (mDrmToken == null) {
            Log.w(TAG, "DRM info is empty. Cancelling current action..");
            return null;
        }

        Log.d(TAG, "drmToken: " + mDrmToken);

        // preparing ViblastDrmInfo
        if (drms.contains(C.WIDEVINE_UUID)) {
            ViblastDrmInfo viblastDrmInfo = new ViblastDrmInfo(C.WIDEVINE_UUID
                    , getWidevineLicenceServerUrl());
            viblastDrmInfo.addDrmKeyRequestProperty("Conax-Custom-Data", mDrmToken);
            return viblastDrmInfo;
        }

        return null;
    }

    private String getWidevineLicenceServerUrl() {
        if (!TextUtils.isEmpty(mWidevineLicenceServerUrl)) {
            return mWidevineLicenceServerUrl;
        }
        Optional<ServiceProviderPrefs> prefs = preferenceManager.getServiceProviderPrefs();
        mWidevineLicenceServerUrl = prefs.map(ServiceProviderPrefs::getLicenseServerUrlWidewine)
                .orElse("");
        return mWidevineLicenceServerUrl;
    }

    public void setDrmToken(String drmToken) {
        mDrmToken = drmToken;
    }
}