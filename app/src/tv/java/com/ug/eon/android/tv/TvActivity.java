package com.ug.eon.android.tv;

import android.app.Activity;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.core.CrashlyticsCore;
import com.ug.eon.android.tv.NetworkCheck.NetworkReceiver;
import com.ug.eon.android.tv.drm.DrmInfoProvider;
import com.ug.eon.android.tv.infoserver.Authentication;
import com.ug.eon.android.tv.infoserver.InfoServerClient;
import com.ug.eon.android.tv.infoserver.InfoServerClientImpl;
import com.ug.eon.android.tv.prefs.PreferenceManager;
import com.ug.eon.android.tv.prefs.PreferenceManagerImpl;
import com.ug.eon.android.tv.prefs.SharedPrefsProvider;
import com.ug.eon.android.tv.prefs.SharedPrefsProviderImpl;
import com.ug.eon.android.tv.util.Dpad;
import com.ug.eon.android.tv.viblastPlayer.UcViblastPlayer;
import com.ug.eon.android.tv.web.hal.WebDeviceInterface;

import io.fabric.sdk.android.Fabric;

/*
 * Main activity which shows WebView EON application.
 */
public class TvActivity extends Activity {
    private static final String TAG = TvActivity.class.getName();
    private static final String EVENT_APP_STARTED = "Application started";

    private NetworkReceiver mNetworkReceiver;
    private WebDeviceInterface mWebDeviceInterface;
    private UcViblastPlayer mUcPlayer;
    private boolean mCreated;
    private Dpad mDPad;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Log all the detected mistakes in the app
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
        // Disable crashlytics for debug.
        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());

        setContentView(R.layout.activity_tv);

        mCreated = false;
        mDPad = new Dpad();
        mNetworkReceiver = new NetworkReceiver();
        initMainActivityOnCreate();
        // keeps the screen turned on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Answers.getInstance().logCustom(new CustomEvent(EVENT_APP_STARTED));
    }

    private void initMainActivityOnCreate() {
        mWebDeviceInterface = new WebDeviceInterface(this);

        SharedPrefsProvider sharedPrefsProvider = new SharedPrefsProviderImpl(getApplicationContext());
        PreferenceManager pm = new PreferenceManagerImpl(sharedPrefsProvider);
        InfoServerClient isClient = new InfoServerClientImpl(pm);
        Authentication authentication = new Authentication(isClient, mWebDeviceInterface);
        DrmInfoProvider drmInfoProvider = new DrmInfoProvider(isClient, pm);

        mUcPlayer = new UcViblastPlayer(findViewById(R.id.main_surface_view), mWebDeviceInterface, drmInfoProvider, pm);
        mWebDeviceInterface.setUcPlayer(mUcPlayer);
        mWebDeviceInterface.setAuthHandler(authentication);
        mNetworkReceiver.setOnConnectionChangeListener(mWebDeviceInterface);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_LEANBACK)) {
            hideSystemUI();
        }
        // TODO: 1/29/18 mCreate is added to secure that webview is not loaded two times, onCreate and onPause.
        Log.d(TAG, "On Resume " + mCreated);
        if(mCreated && mWebDeviceInterface != null) {
            mWebDeviceInterface.reloadState(true);
        }

        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        mCreated = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "On Pause");
        if (mUcPlayer != null && mWebDeviceInterface != null) {
            mWebDeviceInterface.reloadState(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        registerReceiver(mNetworkReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        unregisterReceiver(mNetworkReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: STOP VIDEO");
        if (mUcPlayer != null) {
            mUcPlayer.destroy();
            mUcPlayer = null;
        }
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, "dispatch Event Code: " + event.getKeyCode());
        Log.d(TAG, "dispatch Event Action: " + event.getAction());

        int keyCode = mDPad.getDirectionPressed(event);
        if (keyCode < 0) {
            keyCode = event.getKeyCode();
        }
        Log.d(TAG, "KEY " + event.getAction() + " CODE: " + keyCode);

        // forward volume up/down keys to system
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
            return super.dispatchKeyEvent(event);

        if (mWebDeviceInterface != null) {
            return mWebDeviceInterface.dispatchNativeKey(keyCode, event.getAction());
        }
        return false;
    }
}
