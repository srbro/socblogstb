package com.ug.eon.android.tv.channels.watchnext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.ug.eon.android.tv.TvActivity;
import com.ug.eon.android.tv.channels.ChannelUtils;
import com.ug.eon.android.tv.prefs.PreferenceManager;
import com.ug.eon.android.tv.prefs.PreferenceManagerImpl;
import com.ug.eon.android.tv.prefs.SharedPrefsProviderImpl;

import org.json.JSONObject;

import java.util.List;

public class EonWatchNextActivity extends Activity {

    private static final String TAG = EonWatchNextActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "Watch Next program requested");
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
        String data = createWatchNextProgramData(getWatchNextProgramId(uri));
        if (!data.isEmpty()) {
            mainActivityIntent.putExtra("deepLink", "content");
            mainActivityIntent.putExtra("deepLinkData", data);
        }

        context.startActivity(mainActivityIntent);
        finish();
    }

    public String getWatchNextProgramId(Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (!segments.isEmpty())
            return segments.get(0);

        return "";
    }

    private String createWatchNextProgramData(String watchNextProgramId) {
        if (watchNextProgramId.isEmpty())
            return "";

        List<EonWatchNextProgram> currentWatchNextItems = ChannelUtils.getCurrentWatchNextPrograms(new PreferenceManagerImpl(new SharedPrefsProviderImpl(getApplicationContext())));
        if (currentWatchNextItems == null ) {
            Log.d(TAG, "No programs found.");
            return "";
        }

        for (EonWatchNextProgram eonWatchNextProgram : currentWatchNextItems) {
            if (watchNextProgramId.equals(String.valueOf(eonWatchNextProgram.getId()))) {
                JSONObject json = new JSONObject();
                try {
                    json.put("type", eonWatchNextProgram.getType());
                    json.put("id", eonWatchNextProgram.getId());
                    json.put("channelId", eonWatchNextProgram.getChannelId());
                }
                catch (Exception e) {
                    continue;
                }
                return json.toString();
            }
        }

        return "";
    }
}
