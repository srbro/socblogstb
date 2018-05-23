package com.ug.eon.android.tv.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.ug.eon.android.tv.TvActivity;

/**
 * Created by milan.adamovic on 3/19/18.
 */
public class FirstTimeStartUpReceiver extends BroadcastReceiver {
    private static final String TAG = FirstTimeStartUpReceiver.class.getName();
    private static final String PARTNER_CUSTOMIZATION
            = "com.google.android.tvlauncher.action.PARTNER_CUSTOMIZATION";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (PARTNER_CUSTOMIZATION.equals(intent.getAction())) {
            Log.d(TAG, "received partner customization");

            // Register boot complete receiver.
            ComponentName componentName = new ComponentName(context, StartUpReceiver.class);
            PackageManager packageManager = context.getPackageManager();
            packageManager.setComponentEnabledSetting(componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

            // Start eon.
            Intent startIntent = new Intent(context, TvActivity.class);
            context.startActivity(startIntent);
        }
    }
}
