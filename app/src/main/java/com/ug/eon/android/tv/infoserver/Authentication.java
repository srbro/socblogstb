package com.ug.eon.android.tv.infoserver;

import android.util.Log;

import com.ug.eon.android.tv.web.AuthFailedHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nemanja.todoric on 3/15/2018.
 */

public class Authentication implements AuthInterceptor.AuthFailListener, AuthHandler {

    private static final String TAG = Authentication.class.getName();

    private AuthInterceptor authInterceptor;
    private AuthFailedHandler authFailedHandler;
    private List<AuthListener> authListenerList;

    public Authentication(InfoServerClient isClient, AuthFailedHandler afh) {
        authInterceptor = isClient.getAuthInterceptor();
        authFailedHandler = afh;
        authListenerList = new ArrayList<>();
    }

    /*
    Interceptor callback. Called when we detect HTTP 401 error
     */
    @Override
    public void onAuthFailed() {
        Log.d("UC_AUTH/" + TAG, "auth failed, notifying listeners");
        for (AuthListener listener : authListenerList)
            listener.onAuthFail();
        
        authFailedHandler.onAuthFailed();
        authInterceptor.unregisterListener();
    }

    /*
    JS callback (well, ucWebInterface callback)
    Called when we are authenticated
     */
    @Override
    public void onAuthenticated() {
        Log.d("UC_AUTH/" + TAG, "client authenticated, notifying listeners");
        authInterceptor.registerListener(this);
        for (AuthListener listener : authListenerList)
            listener.onAuthSuccess();
    }

    public void registerListener(AuthListener authListener) {
        authListenerList.add(authListener);
    }

    public void unregisterListener(AuthListener authListener) {
        authListenerList.remove(authListener);
    }

    public static interface AuthListener {
        public void onAuthSuccess();
        public void onAuthFail();
    }
}
