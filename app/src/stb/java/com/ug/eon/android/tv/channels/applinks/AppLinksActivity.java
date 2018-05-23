package com.ug.eon.android.tv.channels.applinks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.ug.eon.android.tv.TvActivity;

import java.util.List;

public class AppLinksActivity extends Activity {

    private static final String TAG = "AppLinkActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "app deep link requested");
        startMainActivity();
    }

    private void startMainActivity() {
        Context context = getApplicationContext();
        Intent mainActivityIntent = new Intent(context, TvActivity.class);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mainActivityIntent.setAction(Intent.ACTION_MAIN);
        mainActivityIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        Intent intent = getIntent();
        Uri uri = intent.getData();
        String action = getUserAction(uri);
        if (!action.isEmpty())
            mainActivityIntent.putExtra("deepLink", action);

        context.startActivity(mainActivityIntent);
        finish();
    }

    public String getUserAction(Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (!segments.isEmpty())
            return segments.get(0);

        return "";
    }
}
