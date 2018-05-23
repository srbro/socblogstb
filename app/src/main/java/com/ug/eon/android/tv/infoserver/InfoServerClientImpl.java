package com.ug.eon.android.tv.infoserver;

import android.text.TextUtils;
import android.util.Log;

import com.ug.eon.android.tv.infoserver.entities.AssetType;
import com.ug.eon.android.tv.infoserver.entities.Assets;
import com.ug.eon.android.tv.infoserver.entities.DrmToken;
import com.ug.eon.android.tv.infoserver.entities.WatchNextItem;
import com.ug.eon.android.tv.prefs.PreferenceManager;
import com.ug.eon.android.tv.util.Optional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nemanja.todoric on 3/9/2018.
 * Implementation of InfoServerClient interface. Uses Retrofit library to utilize Info Server REST API
 */

public class InfoServerClientImpl implements InfoServerClient {
    private static final String TAG = InfoServerClientImpl.class.getName();
    private Optional<ISApi> isApiClient;
    private AuthInterceptor authInterceptor;
    private PreferenceManager preferenceManager;
    private Call<DrmToken> mDrmCall;

    public InfoServerClientImpl(PreferenceManager pm) {
        authInterceptor = new AuthInterceptor();
        preferenceManager = pm;
    }

    @Override
    public AuthInterceptor getAuthInterceptor() {
        return authInterceptor;
    }

    @Override
    public void getDrmToken(InfoServiceCallback<DrmToken> callback) {
        if (mDrmCall != null) {
            mDrmCall.cancel();
            mDrmCall = null;
        }
        mDrmCall = getIsApiClient().get().getDrmToken(getRequestHeader());
        mDrmCall.enqueue(new Callback<DrmToken>() {
            @Override
            public void onResponse(Call<DrmToken> call, Response<DrmToken> response) {
                if (!response.isSuccessful()) {
                    callback.onResponse(null);
                    return;
                }
                callback.onResponse(response.body());
            }

            @Override
            public void onFailure(Call<DrmToken> call, Throwable t) {
                callback.onResponse(null);
            }
        });
    }

    @Override
    public String getDrmToken() {
        // Fetch drm token
        return getIsApiClient().map(isApi -> {
            Call<DrmToken> call = isApi.getDrmToken(getRequestHeader());
            DrmToken token = null;
            try {
                Response<DrmToken> response = call.execute();
                token = response.body();
            } catch (IOException e) {
                Log.d(TAG, "Fetch DRM token: I/O exception");
                e.printStackTrace();
            }
            return token != null ? token.getToken() : null;
        }).orElse("");
    }

    @Override
    public Assets searchAssets(String query) {
        return searchAssets(query, null);
    }

    @Override
    public Assets searchAssets(String query, AssetType assetType) {
        if(query == null) {
            return null;
        }

        String strAssetType = null;
        switch (assetType) {
            case LIVETV: strAssetType = "TV"; break;
            case CUTV: strAssetType = "CUTV"; break;
            case VOD: strAssetType = "VOD"; break;
        }

        final String searchAssetType = strAssetType;

        return getIsApiClient().map(isApi -> {
            Call<Assets> call = isApi.searchAssets(getRequestHeader(), "ASC", query, searchAssetType);
            try {
                Response<Assets> response = call.execute();
                return response.body();
            } catch (IOException e) {
                Log.d(TAG, "Search Assets: I/O exception");
                e.printStackTrace();
            }
            return null;
        }).orElse(null);
    }

    @Override
    public List<WatchNextItem> getWatchNextItems() {
        return getIsApiClient().map(isApi -> {
            List<WatchNextItem> watchNextItems = new ArrayList<>();
            Call<List<WatchNextItem>> call = isApi.getWatchNextItems(getRequestHeader());

            try {
                Response<List<WatchNextItem>> response = call.execute();
                watchNextItems = response.body();
            } catch (IOException e) {
                Log.d(TAG, "Fetch Watch Next items: I/O exception");
                e.printStackTrace();
            }

            return watchNextItems;

        }).orElse(new ArrayList<>());
    }

    private Optional<ISApi> getIsApiClient() {
        if (isApiClient == null || !isApiClient.isPresent()) {
            isApiClient = ISServiceGenerator.createISClient(authInterceptor, preferenceManager);
        }
        return isApiClient;
    }

    private String getRequestHeader() {
        return  "Bearer " + preferenceManager.getAuthToken().orElse("");
    }
}
