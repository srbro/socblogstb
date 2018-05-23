package com.ug.eon.android.tv;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.ug.eon.android.tv.storage.PrefKey;
import com.ug.eon.android.tv.storage.PreferenceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AppUpdater {
    private static final String TAG = AppUpdater.class.getName();
    private static final String ACTION_RESPONSE = "com.ug.eon.android.tv.ota.ACTION_RESPONSE";
    private static final String ACTION_REQUEST = "net.quber.otaupdate.ACTION_REQUEST";
    private static final String EXTRA_REQUEST_TYPE = "otaRequestType";
    private static final String EXTRA_CHECK_UPDATE = "checkForUpdates";
    private static final String EXTRA_START_UPDATE = "startUpdates";
    private static final String EXTRA_SET_PERIOD = "setPeriod";
    private static final String EXTRA_RESPONSE = "response";
    private static final long POSTPONE_TIME = 24 * 60 * 1000;

    private AlertDialog mDialog;
    private String mCurrentAppVersion;
    private String mNewVersion;
    private String mPackageName;
    private boolean mSettingsUpdate;
    private Context mContext;

    public AppUpdater(Context context) {
        mContext = context;
        mPackageName = mContext.getPackageName();
    }

    public void setPeriod(long interval) {
        Intent intent = getUpdateIntent(EXTRA_SET_PERIOD);
        intent.putExtra("interval", interval);
        mContext.sendBroadcast(intent);
    }

    public void checkForUpdates() {
        if (isPostponed()) {
            return;
        }
        mSettingsUpdate = false;
        mContext.sendBroadcast(getUpdateIntent(EXTRA_CHECK_UPDATE));
    }

    public void checkForUpdatesSettings() {
        mSettingsUpdate = true;
        mContext.sendBroadcast(getUpdateIntent(EXTRA_CHECK_UPDATE));
    }

    private void startUpdate(String appId) {
        Intent updateIntent = getUpdateIntent(EXTRA_START_UPDATE);
        updateIntent.putExtra("type", "apps");
        updateIntent.putExtra("updateId", appId);
        updateIntent.putExtra("restart", true);
        mContext.sendBroadcast(updateIntent);
    }

    private Intent getUpdateIntent(String value) {
        Intent intent = new Intent ();
        intent.setAction(ACTION_REQUEST);
        intent.putExtra(EXTRA_REQUEST_TYPE, value);
        return intent;
    }

    private void parseCheckUpdate(String response) {
        try {
            JSONArray array = new JSONObject(response)
                    .getJSONArray("updates");
            if (array == null) {
                return;
            }

            String msg = null;
            for (int i = 0; i < array.length(); i++) {
                JSONObject app = array.getJSONObject(i);
                if (app.getString("updateId").equals(mPackageName)) {
                    mCurrentAppVersion = app.getString("currentVersion");
                    mNewVersion = app.getString("newVersion");
                    msg = app.getString("message");
                    break;
                }
            }

            if (!TextUtils.isEmpty(mNewVersion) && !mCurrentAppVersion.equals(mNewVersion)) {
                if (mNewVersion.compareTo(mCurrentAppVersion) <= 0) {
                    return;
                }

                if (mSettingsUpdate) {
                    showUpdateInfoDialog(msg);
                } else {
                    if (isPostponed())
                        return;

                    showUpdateDialog();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Postpone app update for 24h.
     */
    private void postpone() {
        PreferenceUtils.setValue(mContext, PrefKey.UPDATE_POSTPONE_TIME
                , System.currentTimeMillis());
    }

    private boolean isPostponed() {
        long currentTime = System.currentTimeMillis();
        long postponedTime = PreferenceUtils.getValue(mContext, PrefKey.UPDATE_POSTPONE_TIME);
        return postponedTime != -1 && (currentTime - postponedTime) < POSTPONE_TIME;
    }

    public void unregister() {
        mContext.unregisterReceiver(mUpdateReceiver);
    }

    public void register() {
        mContext.registerReceiver(mUpdateReceiver, new IntentFilter(ACTION_RESPONSE));
    }

    private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(ACTION_RESPONSE)) {
                String response = intent.getStringExtra(EXTRA_RESPONSE);
                parseCheckUpdate(response);
            }
        }
    };

    private void showUpdateDialog() {
        showDialog(mContext.getString(R.string.app_update_dialog_title)
                , mContext.getString(R.string.app_update_dialog_msg));
    }

    private void showUpdateInfoDialog(String desc) {
        String msg = String.format(mContext.getString(R.string.app_update_dialog_info_msg), mNewVersion, desc);
        showDialog(mContext.getString(R.string.app_update_dialog_title), msg);
    }

    private void showDialog(String title, String msg) {
        if (mDialog != null && mDialog.isShowing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.app_update_dialog_positive_btn, (dialog, which) -> {
            startUpdate(mPackageName);
        });
        builder.setNegativeButton(R.string.app_update_dialog_negative_btn, (dialog, which) -> {
            postpone();
            mDialog.cancel();
            mDialog = null;
        });

        mDialog = builder.create();
        mDialog.show();
    }
}