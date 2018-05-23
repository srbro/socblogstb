package com.ug.eon.android.tv.infoserver;

import com.ug.eon.android.tv.infoserver.entities.AssetType;
import com.ug.eon.android.tv.infoserver.entities.Assets;
import com.ug.eon.android.tv.infoserver.entities.DrmToken;
import com.ug.eon.android.tv.infoserver.entities.WatchNextItem;

import java.util.List;

import retrofit2.Callback;

/**
 * Created by nemanja.todoric on 3/27/2018.
 * Provides high-level API for retrieving information from Info Server
 */

public interface InfoServerClient {

    /*
     * Returns DRM token used (by players) to obtain licence key to play DRM-protected content
     */
    String getDrmToken();

    AuthInterceptor getAuthInterceptor();

    /**
     * Get drm token async.
     * @param callback register IS callback.
     */
    void getDrmToken(InfoServiceCallback<DrmToken> callback);

    /*
      Search
     */
    Assets searchAssets(String query);
    Assets searchAssets(String query, AssetType assetType);

    /*
     * Watch Next
     */
    List<WatchNextItem> getWatchNextItems();
}