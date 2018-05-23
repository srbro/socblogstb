package com.ug.eon.android.tv.infoserver;

import com.ug.eon.android.tv.infoserver.entities.Assets;
import com.ug.eon.android.tv.infoserver.entities.DrmToken;
import com.ug.eon.android.tv.infoserver.entities.WatchNextItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by nemanja.todoric on 3/27/2018.
 */

public interface ISApi {

    @POST("drm/token")
    Call<DrmToken> getDrmToken(@Header("Authorization") String authHeader); //TODO: move auth token header to interceptor

    @GET("search")
    Call<Assets> searchAssets(@Header("Authorization") String authHeader,
                              @Query("sortDir") String sortDirection,
                              @Query("q") String query,
                              @Query("assetType") String assetType);

    @GET("launcher/watchnext")
    Call<List<WatchNextItem>> getWatchNextItems(@Header("Authorization") String authHeader);
}
