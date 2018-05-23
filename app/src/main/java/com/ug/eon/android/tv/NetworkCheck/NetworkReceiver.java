package com.ug.eon.android.tv.NetworkCheck;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by milan.adamovic on 1/29/18.
 */
public class NetworkReceiver extends BroadcastReceiver {
    private static final String TAG = NetworkReceiver.class.getName();
    private OnNetworkChange mConnectionListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, intent.getAction());
        boolean networkStatus = isNetworkConnected(context);
        Log.d(TAG, "Network status: " + networkStatus);
        if (mConnectionListener != null)
            mConnectionListener.onConnectionChange(networkStatus);
    }

    /**
     * Checks is possible to establish connections and pass data.
     */
    private boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null)
            return false;

        Log.d(TAG, "NetworkType: " + activeNetwork.getTypeName());
        return activeNetwork.isConnected();
    }

    public void setOnConnectionChangeListener(OnNetworkChange listener) {
        mConnectionListener = listener;
    }
}