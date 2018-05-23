package com.ug.eon.android.tv.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.ug.eon.android.tv.TvActivity;

/**
 * Created by petar.stefanovic on 08/03/2018.
 */


public class SpecialRCUKeysReceiver extends BroadcastReceiver {
    private static final String TAG = SpecialRCUKeysReceiver.class.getName();
    private static final String ACTION = "com.ug.eon.android.tv.specialkeys";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            Log.d(TAG, "received special RCU key");
            if (!isTvUserSetupComplete(context))
                return;

            Intent mainActivityIntent = new Intent(context, TvActivity.class);
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            mainActivityIntent.setAction(Intent.ACTION_MAIN);
            mainActivityIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            String action = getDeepLinkFromAction(intent.getStringExtra("key"));
            if (!action.isEmpty()) {
                mainActivityIntent.putExtra("deepLink", action);
            }
            context.startActivity(mainActivityIntent);
        }
    }

    private String getDeepLinkFromAction(String action) {
        switch(action) {
            case "eon": return "home";
            case "guide": return "guide";
            case "ondemand": return "vod";
            case "radio": return "radio";
            case "livetv": return "livetv";
            default: return "";
        }
    }

    private boolean isTvUserSetupComplete(Context context) {
        boolean isTvSetupComplete = Settings.Secure.getInt(
                context.getContentResolver(), "user_setup_complete", 0) != 0;
        Log.d(TAG, "isTvSetupComplete : " + isTvSetupComplete);
        isTvSetupComplete &= Settings.Secure.getInt(
                context.getContentResolver(), "tv_user_setup_complete", 0) != 0;
        Log.d(TAG, "isTvSetupComplete : " + isTvSetupComplete);
        return isTvSetupComplete;
    }

}
