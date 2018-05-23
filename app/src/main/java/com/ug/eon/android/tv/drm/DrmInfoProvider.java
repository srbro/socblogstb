package com.ug.eon.android.tv.drm;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.ug.eon.android.tv.infoserver.InfoServerClient;
import com.ug.eon.android.tv.prefs.PreferenceManager;
import com.ug.eon.android.tv.util.Optional;

/**
 * Created by nemanja.todoric on 3/9/2018.
 */

public class DrmInfoProvider {
    private static final String TAG = DrmInfoProvider.class.getName();
    private static final String KEY_DEVICE_NUMBER = "deviceNumber";

    private InfoServerClient isClient;
    private PreferenceManager preferenceManager;
    private Optional<String> mDeviceNumber;

    public DrmInfoProvider(InfoServerClient isc, PreferenceManager prefsMgr) {
        isClient = isc;
        preferenceManager = prefsMgr;
        mDeviceNumber = preferenceManager.getValue(KEY_DEVICE_NUMBER);
    }

    /**
     * Requests drm token and creates drm info json.
     * @param drmListener Drm listener.
     */
    public void requestDrmInfo(DrmTokenListener drmListener) {
        isClient.getDrmToken(drmToken -> {
            if (drmToken == null || TextUtils.isEmpty(drmToken.getToken())) {
                drmListener.onDrmTokenFetched(null);
                return;
            }
            drmListener.onDrmTokenFetched(getDrmTokenJson(drmToken.getToken()));
        });
    }

    @Nullable
    public String getDrmInfo() {
        String token = isClient.getDrmToken();
        if (TextUtils.isEmpty(token)) {
            Log.w(TAG, "Unable to obtain DRM token");
            return null;
        }
        return getDrmTokenJson(token);
    }

    private String getDrmTokenJson(String drmToken) {
        DrmInfo drmInfo = DrmInfo.createDrmInfo();
        drmInfo.setCxAuthenticationDataToken(drmToken);

        DrmInfo.CxClientInfo cxClientInfo = drmInfo.getCxClientInfo();

        // Try to refresh device number, if not present
        if (!mDeviceNumber.isPresent()) {
            mDeviceNumber = preferenceManager.getValue(KEY_DEVICE_NUMBER);
        }

        if (!mDeviceNumber.isPresent()) {
            Log.w(TAG, "Unable to obtain device number");
            return null;
        }

        cxClientInfo.setCxDeviceId(mDeviceNumber.get());
        drmInfo.setCxClientInfo(cxClientInfo);
        return new Gson().toJson(drmInfo);
    }

    private static class DrmInfo {
        private String Version;
        private String CxAuthenticationDataToken;
        private CxClientInfo CxClientInfo;

        public static DrmInfo createDrmInfo() {
            DrmInfo drmInfo = new DrmInfo();
            drmInfo.setVersion("1.0.0");

            CxClientInfo cxClientInfo = new CxClientInfo();
            cxClientInfo.setDeviceType("Browser");
            cxClientInfo.setDrmClientType("Widevine-HTML5");
            cxClientInfo.setDrmClientVersion("1.0.0");

            drmInfo.setCxClientInfo(cxClientInfo);

            return drmInfo;
        }

        public String getVersion() {
            return Version;
        }

        public void setVersion(String version) {
            Version = version;
        }

        public String getCxAuthenticationDataToken() {
            return CxAuthenticationDataToken;
        }

        public void setCxAuthenticationDataToken(String cxAuthenticationDataToken) {
            CxAuthenticationDataToken = cxAuthenticationDataToken;
        }

        public DrmInfo.CxClientInfo getCxClientInfo() {
            return CxClientInfo;
        }

        public void setCxClientInfo(DrmInfo.CxClientInfo cxClientInfo) {
            CxClientInfo = cxClientInfo;
        }

        private static class CxClientInfo {
            private String DeviceType;
            private String DrmClientType;
            private String DrmClientVersion;
            private String CxDeviceId;

            public String getDeviceType() {
                return DeviceType;
            }

            public void setDeviceType(String deviceType) {
                DeviceType = deviceType;
            }

            public String getDrmClientType() {
                return DrmClientType;
            }

            public void setDrmClientType(String drmClientType) {
                DrmClientType = drmClientType;
            }

            public String getDrmClientVersion() {
                return DrmClientVersion;
            }

            public void setDrmClientVersion(String drmClientVersion) {
                DrmClientVersion = drmClientVersion;
            }

            public String getCxDeviceId() {
                return CxDeviceId;
            }

            public void setCxDeviceId(String cxDeviceId) {
                CxDeviceId = cxDeviceId;
            }
        }
    }

}
