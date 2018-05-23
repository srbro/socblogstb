package com.ug.eon.android.tv.infoserver;

import com.ug.eon.android.tv.prefs.PreferenceManager;
import com.ug.eon.android.tv.prefs.ServerPrefs;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.ug.eon.android.tv.util.Optional;

/**
 * Created by nemanja.todoric on 3/9/2018.
 */

class ISServiceGenerator {

    private static AuthInterceptor authInterceptor;

    static Optional<ISApi> createISClient(AuthInterceptor authInterceptor, PreferenceManager preferenceManager) {
        ISServiceGenerator.authInterceptor = authInterceptor;
        Optional<String> apiBaseUrl = generateApiBaseUrl(preferenceManager);

        if(!apiBaseUrl.isPresent()) {
            return Optional.empty();
        }

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(authInterceptor);

        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(apiBaseUrl.get())
                        .addConverterFactory(
                                GsonConverterFactory.create()
                        );

        Retrofit retrofit =
                builder
                        .client(
                                httpClient.build()
                        )
                        .build();

        return Optional.of(retrofit.create(ISApi.class));
    }

    private static Optional<String> generateApiBaseUrl(PreferenceManager preferenceManager) {
        return preferenceManager.getServerPrefs().map((ServerPrefs servers) -> {
            if(servers.getInfoServerBaseUrl() == null || servers.getInfoServerBaseUrl().isEmpty())
                return null;
            if(servers.getApiVersion() == null || servers.getApiVersion().isEmpty())
                return null;
            return servers.getInfoServerBaseUrl() + "/" + servers.getApiVersion() + "/";
        });
    }
}
