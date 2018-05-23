package com.ug.eon.android.tv;

import android.app.Activity;
import android.content.Intent;
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
import com.ug.eon.android.tv.channels.ChannelUtils;
import com.ug.eon.android.tv.channels.ChannelsManager;
import com.ug.eon.android.tv.drm.DrmInfoProvider;
import com.ug.eon.android.tv.infoserver.Authentication;
import com.ug.eon.android.tv.infoserver.InfoServerClient;
import com.ug.eon.android.tv.infoserver.InfoServerClientImpl;
import com.ug.eon.android.tv.infoserver.entities.WatchNextItem;
import com.ug.eon.android.tv.prefs.PreferenceManager;
import com.ug.eon.android.tv.prefs.PreferenceManagerImpl;
import com.ug.eon.android.tv.prefs.SharedPrefsProvider;
import com.ug.eon.android.tv.prefs.SharedPrefsProviderImpl;
import com.ug.eon.android.tv.tif.player.DvbPlayer;
import com.ug.eon.android.tv.util.Dpad;
import com.ug.eon.android.tv.viblastPlayer.UcViblastPlayer;
import com.ug.eon.android.tv.web.PlayerInterface;
import com.ug.eon.android.tv.web.StartupParameters;
import com.ug.eon.android.tv.web.hal.WebDeviceInterface;

import java.util.List;

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
    private PlayerInterface mUcLivePlayer;
    private boolean mCreated;
    private Dpad mDPad;
    private boolean mProvisioningMode;

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
        mProvisioningMode = false;
        mDPad = new Dpad();
        mNetworkReceiver = new NetworkReceiver();
        initMainActivityOnCreate();
        // keeps the screen turned on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Answers.getInstance().logCustom(new CustomEvent(EVENT_APP_STARTED));

        if (!mProvisioningMode)
            ChannelsManager.syncMainEonChannel(this);
    }

    private void initMainActivityOnCreate() {
        Intent intent = getIntent();
        StartupParameters params = new StartupParameters();
        params.setStartupAction(intent.getStringExtra("deepLink"));
        params.setStartupMode(intent.getStringExtra("mode"));
        params.setStartupActionData(intent.getStringExtra("deepLinkData"));

        mWebDeviceInterface = new WebDeviceInterface(this, params);

        mProvisioningMode = params.getStartupMode().equals("stbprovisioning");

        SharedPrefsProvider sharedPrefsProvider = new SharedPrefsProviderImpl(getApplicationContext());
        PreferenceManager pm = new PreferenceManagerImpl(sharedPrefsProvider);
        InfoServerClient isClient = new InfoServerClientImpl(pm);
        Authentication authentication = new Authentication(isClient, mWebDeviceInterface);
        DrmInfoProvider drmInfoProvider = new DrmInfoProvider(isClient, pm);

        // Initialize players for OTT and Live.
        mUcPlayer = new UcViblastPlayer(findViewById(R.id.main_surface_view), mWebDeviceInterface, drmInfoProvider, pm);
        mUcLivePlayer = new DvbPlayer(this, findViewById(R.id.tv_view), mWebDeviceInterface);

        mWebDeviceInterface.setUcPlayer(mUcPlayer);
        mWebDeviceInterface.setAuthHandler(authentication);
        mWebDeviceInterface.setLiveUcPlayer(mUcLivePlayer);
        mNetworkReceiver.setOnConnectionChangeListener(mWebDeviceInterface);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String deepLinkAction = intent.getStringExtra("deepLink");
        Log.i(TAG, "Received deep link request: " + deepLinkAction);
        if (deepLinkAction != null) {
            mWebDeviceInterface.doDeepLink(deepLinkAction, intent.getStringExtra("deepLinkData"));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_LEANBACK)) {
            hideSystemUI();
        }
        // TODO: 1/29/18 mCreate is added to secure that webview is not loaded two times, onCreate and onPause.
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        mCreated = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mCreated && mWebDeviceInterface != null) {
            mWebDeviceInterface.reloadState(true);
        }
        mWebDeviceInterface.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mUcPlayer != null && mWebDeviceInterface != null) {
            mWebDeviceInterface.reloadState(false);
        }
        unregisterReceiver(mNetworkReceiver);
        mWebDeviceInterface.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUcPlayer != null) {
            mUcPlayer.destroy();
            mUcPlayer = null;
        }
        if (mUcLivePlayer != null) {
            mUcLivePlayer.destroy();
            mUcLivePlayer = null;
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
        int keyCode = mDPad.getDirectionPressed(event);
        if (keyCode < 0) {
            keyCode = event.getKeyCode();
        }
        Log.d(TAG, "KEY " + event.getAction() + " CODE: " + keyCode);

        // forward volume up/down keys to system
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
            return super.dispatchKeyEvent(event);

        // in provisioning mode we only support navigation keys (no back or exit)
        if (mProvisioningMode && !isNavigationKey(keyCode)) {
            return false;
        }
        if (mWebDeviceInterface != null) {
            return mWebDeviceInterface.dispatchNativeKey(keyCode, event.getAction());
        }
        return false;
    }

    private boolean isNavigationKey(int keyCode) {
        return keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                || keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_UP
                || keyCode == KeyEvent.KEYCODE_DPAD_CENTER;
    }
}
