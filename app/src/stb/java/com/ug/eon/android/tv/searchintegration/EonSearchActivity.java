package com.ug.eon.android.tv.searchintegration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by goran.arandjelovic on 2/27/18.
 */

public abstract class EonSearchActivity extends Activity {
    private static final String TAG = EonSearchActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "app global search requested");
        startMainActivity();
    }

    private void startMainActivity() {
        Context context = getApplicationContext();
        Intent mainActivityIntent = new Intent(context, com.ug.eon.android.tv.TvActivity.class);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mainActivityIntent.setAction(Intent.ACTION_MAIN);
        mainActivityIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        Intent intent = getIntent();
        String data = intent.getDataString();
        if (data != null && !data.isEmpty()) {
            mainActivityIntent.putExtra("deepLink", "content");
            mainActivityIntent.putExtra("deepLinkData", data);
        }

        context.startActivity(mainActivityIntent);
        finish();
    }
}