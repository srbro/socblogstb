package com.ug.eon.android.tv;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import net.quber.eonsettingservice.IEonSettingService;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by milan.adamovic on 3/14/18.
 */
public class UcServiceConnection implements ServiceConnection {
    private static final String TAG = UcServiceConnection.class.getName();
    private static final String PACKAGE_SETTINGS_SERVICE = "net.quber.eonsettingservice";
    private static final String ACTION_SETTINGS_SERVICE = "net.quber.eonsettingservice.IEonSettingService";
    private IEonSettingService mService;
    private Listener mListener;
    private boolean mConnected;

    public void connect(Context context, Listener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null!");
        }
        mListener = listener;
        if (mService != null && mConnected) {
            mListener.onConnection(true, mService);
            return;
        }
        context.bindService(getSettingsIntent(), this, BIND_AUTO_CREATE);
    }

    public void reconnect(Context context) {
        context.bindService(getSettingsIntent(), this, BIND_AUTO_CREATE);
    }

    public void disconnect(Context context) {
        if (mService != null && mConnected) {
            context.unbindService(this);
        }
    }

    private Intent getSettingsIntent() {
        Intent startIntent = new Intent();
        startIntent.setPackage(PACKAGE_SETTINGS_SERVICE);
        startIntent.setAction(ACTION_SETTINGS_SERVICE);
        return startIntent;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "Settings Service connected");
        mService = IEonSettingService.Stub.asInterface(service);
        mListener.onConnection(true, mService);
        mConnected = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "Settings Service disconnected");
        mService = null;
        mListener.onConnection(false, null);
        mConnected = false;
    }

    @Override
    public void onBindingDied(ComponentName name) {
        Log.d(TAG, "Settings Service binding died");
        mService = null;
        mListener.onConnection(false, null);
        mConnected = false;
    }

    public interface Listener {
        void onConnection(boolean connected, @Nullable IEonSettingService service);
    }
}