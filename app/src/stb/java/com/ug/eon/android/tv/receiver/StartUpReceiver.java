package com.ug.eon.android.tv.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ug.eon.android.tv.TvActivity;

/**
 * Created by milan.adamovic on 2/26/18.
 */
public class StartUpReceiver extends BroadcastReceiver {
    private static final String TAG = StartUpReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "received boot completed");
            Intent startIntent = new Intent(context, TvActivity.class);
            context.startActivity(startIntent);
        }
    }
}