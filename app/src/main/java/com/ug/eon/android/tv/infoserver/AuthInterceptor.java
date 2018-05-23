package com.ug.eon.android.tv.infoserver;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by nemanja.todoric on 3/15/2018.
 */

public class AuthInterceptor implements Interceptor {

    private static final String TAG = AuthInterceptor.class.getName();

    public static interface AuthFailListener {
        public void onAuthFailed();
    }

    private AuthFailListener listener;

    void registerListener(AuthFailListener listener) {
        this.listener = listener;
    }

    void unregisterListener() {
        listener = null;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (response.code() == 401) {
            Log.w("UC_AUTH/" + TAG, "received HTTP error 401. notifying..");
            authFailed();
        }
        return response;
    }

    private void authFailed() {
        if (listener != null)
            listener.onAuthFailed();
    }

}
