package com.ug.eon.android.tv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.os.ConfigurationCompat;
import android.util.Log;

import com.ug.eon.android.tv.prefs.PreferenceManager;
import com.ug.eon.android.tv.prefs.PreferenceManagerImpl;
import com.ug.eon.android.tv.prefs.SharedPrefsProviderImpl;

import static com.ug.eon.android.tv.web.UcWebInterface.STB_PROVISIONING_MODE;

/**
 * Created by petar.stefanovic on 20/03/2018.
 */

public class ProvisioningActivity extends Activity {
    private static final String TAG = "ProvisioningActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Provisioning requested");

        String lang = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0).getISO3Language();

        // for macedonian language EON is using code for bibliographic applications, while Android is using code for terminology applications
        if (lang.equals("mkd")) {
            lang = "mac";
        }

        Log.i(TAG, "Setting system language to app: " + lang);

        Context context = getApplicationContext();

        PreferenceManager pm = new PreferenceManagerImpl(new SharedPrefsProviderImpl(context));
        pm.setValue("lang", lang);

        Intent mainActivityIntent = new Intent(context, TvActivity.class);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        mainActivityIntent.putExtra("mode", STB_PROVISIONING_MODE);

        Log.i(TAG, "Starting TvActivity in provisioning mode");
        startActivity(mainActivityIntent);

        finish();
    }
}
