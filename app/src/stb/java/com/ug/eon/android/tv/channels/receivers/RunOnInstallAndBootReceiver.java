package com.ug.eon.android.tv.channels.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ug.eon.android.tv.channels.ChannelsManager;
import com.ug.eon.android.tv.channels.DefaultChannelsManager;

public class RunOnInstallAndBootReceiver extends BroadcastReceiver {

    private static final String TAG = "RunOnInstallAndBootRecv";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive(): " + intent);
        ChannelsManager channelsManager = new DefaultChannelsManager();
        channelsManager.syncMainEonChannel(context);
    }
}
