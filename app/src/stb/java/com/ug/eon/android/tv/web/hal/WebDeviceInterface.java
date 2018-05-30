package com.ug.eon.android.tv.web.hal;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.ug.eon.android.tv.AppUpdater;
import com.ug.eon.android.tv.UcServiceConnection;
import com.ug.eon.android.tv.channels.ChannelsManager;
import com.ug.eon.android.tv.channels.DefaultChannelsManager;
import com.ug.eon.android.tv.channels.watchnext.WatchNextManager;
import com.ug.eon.android.tv.config.ConfigurationManager;
import com.ug.eon.android.tv.config.DefaultPropertyFileProvider;
import com.ug.eon.android.tv.config.PropertyFileConfigurationManager;
import com.ug.eon.android.tv.config.FeatureFactory;
import com.ug.eon.android.tv.config.PropertyFileProvider;
import com.ug.eon.android.tv.tif.dvbchannelssync.DVBChannelsSyncService;
import com.ug.eon.android.tv.tif.parser.DvbChannelParserTask;
import com.ug.eon.android.tv.web.StartupParameters;
import com.ug.eon.android.tv.web.UcWebInterface;

import net.quber.eonsettingservice.IEonSettingService;

/**
 * This web interface should contain interfaces for stb device.
 * Created by milan.adamovic on 3/12/18.
 */
public class WebDeviceInterface extends UcWebInterface {
    private static final String TAG = WebDeviceInterface.class.getName();
    private static final String ACTION_SEARCH_ACCESSORIES = "com.google.android.intent.action.CONNECT_INPUT";
    private static final String ACTION_SYSTEM_UPDATE = "android.settings.SYSTEM_UPDATE_SETTINGS";
    private static final String HYBRID_MODE = "HYBRID";
    public static final String BROADCAST_CHANNELS_SET = "com.ug.eon.android.tv.web.hal.CHANNELS_SET";
    private IEonSettingService mService;
    private AppUpdater mAppUpdater;
    private Context mAppContext;

    public WebDeviceInterface(Activity activity, StartupParameters params) {
        super(activity, params);
        mAppContext = activity.getApplicationContext();
        mAppUpdater = new AppUpdater(mActivity);

        UcServiceConnection ucService = new UcServiceConnection();
        ucService.connect(mAppContext, (connected, service) -> {
            if (connected) {
                mService = service;
            } else {
                ucService.reconnect(mAppContext);
            }
        });

        Runnable syncRunnable = () -> {
            WebView wv = getWebView();
            wv.loadUrl(DVBChannelsSyncService.getChannelDataUrl());
        };

        DVBChannelsSyncService.scheduleDVBChannelsSync(mAppContext, syncRunnable, 60);
    }

    public void start() {
        mAppUpdater.register();
        mAppUpdater.checkForUpdates();
    }

    public void stop() {
        mAppUpdater.unregister();
    }

    /**
     * JS exposed method to set channel lists.
     *
     * @param tvChannelList Tv channel list.
     * @param radioChannelList Radio channel list.
     */
    @JavascriptInterface
    public void setChannelData(String tvChannelList, String radioChannelList) {
        Log.d(TAG, "set dvb channel data");
        if (tvChannelList == null && radioChannelList == null) {
            Log.d(TAG, "list channels are empty");
            return;
        }
        if (mService == null) {
            Log.e(TAG, "Service is null!");
            return;
        }
        UcServiceConnection ucService = new UcServiceConnection();
        ucService.connect(mAppContext, (connected, service) -> {
            if (connected) {
                DvbChannelParserTask dvbChannelParserTask
                        = new DvbChannelParserTask(mService, mAppContext);
                dvbChannelParserTask.execute(tvChannelList, radioChannelList);
            } else {
                ucService.reconnect(mAppContext);
            }
        });
    }

    /**
     * JS exposed method to finish provisioning.
     *
     * @param mode provision mode.
     */
    @JavascriptInterface
    public void provisioningDone(String mode) {
        Log.d(TAG, "provisioned, mode: " + mode);
        // in hybrid mode we have to wait for channels to be set
        if (mode.equals(HYBRID_MODE)) {
            mAppContext.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    mAppContext.unregisterReceiver(this);
                    exitWithResult(mode);
                }
            },  new IntentFilter(BROADCAST_CHANNELS_SET));
        }
        else {
            exitWithResult(mode);
        }
    }

    private void exitWithResult(String mode) {
        Intent result = new Intent();
        // report provision mode so we know whether RF check is needed or not (during FTU)
        result.putExtra("mode", mode);
        mActivity.setResult(Activity.RESULT_OK, result);
        mActivity.finish();
    }

    @JavascriptInterface
    public String getDeviceOperationMode() {
        // TODO: 23/02/18 Add implementation once KAON provides library.
        return "Online";
    }

    /**
     * @see IEonSettingService#getVideoScalingMode()
     */
    @JavascriptInterface
    public int getVideoSize() throws RemoteException {
        return mService != null ? mService.getVideoScalingMode() : -1;
    }

    /**
     * @see IEonSettingService#setVideoScalingMode(int)
     */
    @JavascriptInterface
    public boolean setVideoSize(int videoSize) throws RemoteException {
        return mService != null && mService.setVideoScalingMode(videoSize);
    }

    /**
     * @see IEonSettingService#getAudioOutputMode()
     */
    @JavascriptInterface
    public int getAudioOutput() throws RemoteException {
        return mService != null ? mService.getAudioOutputMode() : -1;
    }

    /**
     * @see IEonSettingService#setAudioOutputMode(int)
     */
    @JavascriptInterface
    public boolean setAudioOutput(int audioOutput) throws RemoteException {
        return mService != null && mService.setAudioOutputMode(audioOutput);
    }

    /**
     * @see IEonSettingService#getHDMIAudioMode()
     */
    @JavascriptInterface
    public int getHDMIAudioOutput() throws RemoteException {
        return mService != null ? mService.getHDMIAudioMode() : -1;
    }

    /**
     * @see IEonSettingService#setHDMIAudioMode(int)
     */
    @JavascriptInterface
    public boolean setHDMIAudioOutput(int hdmiVideoOutput) throws RemoteException {
        return mService != null && mService.setHDMIAudioMode(hdmiVideoOutput);
    }

    @JavascriptInterface
    public String getSubtitleLanguage() {
        // TODO: 23/02/18 Add implementation once KAON provides library.
        return "English";
    }

    @JavascriptInterface
    public void setSubtitleLanguage(String subtitleLanguage) {
        // TODO: 23/02/18 Add implementation once KAON provides library.
    }

    @JavascriptInterface
    public String getSubtitleLanguage2(){
        // TODO: 23/02/18 Add implementation once KAON provides library.
        return "English";
    }

    @JavascriptInterface
    public void setSubtitleLanguage2(String subtitleLanguage) {
        // TODO: 23/02/18 Add implementation once KAON provides library.
    }

    @JavascriptInterface
    public String getAudio() {
        // TODO: 23/02/18 Add implementation once KAON provides library.
        return "English";
    }

    @JavascriptInterface
    public void setAudio(String audio) {
        // TODO: 23/02/18 Add implementation once KAON provides library.
    }

    @JavascriptInterface
    public String getAudio2() {
        // TODO: 23/02/18 Add implementation once KAON provides library.
        return "English";
    }

    @JavascriptInterface
    public void setAudio2(String audio) {
        // TODO: 23/02/18 Add implementation once KAON provides library.
    }

    /**
     * @see IEonSettingService#getDisplayResolution()
     */
    @JavascriptInterface
    public int getDisplayResolution() throws RemoteException {
        return mService != null ? mService.getDisplayResolution() : -1;
    }

    /**
     * @see IEonSettingService#setDisplayResolution(int)
     */
    @JavascriptInterface
    public boolean setDisplayResolution(int displayResolution) throws RemoteException {
        return mService != null && mService.setDisplayResolution(displayResolution);
    }

    @JavascriptInterface
    public int getDisplaySize() throws RemoteException {
        return -1;
    }

    @JavascriptInterface
    public boolean setDisplaySize(int displaySize) throws RemoteException {
        return false;
    }

    /**
     * Opens Remote & Accessories from System settings.
     */
    @JavascriptInterface
    public void pairRCU() {
        Intent pairIntent = new Intent(ACTION_SEARCH_ACCESSORIES);
        if (pairIntent.resolveActivity(mAppContext.getPackageManager()) != null) {
            mAppContext.startActivity(pairIntent);
        }
    }

    /**
     * Opens System update from System settings.
     */
    @JavascriptInterface
    public void checkForUpdate() {
        Intent checkUpdate = new Intent(ACTION_SYSTEM_UPDATE);
        if (checkUpdate.resolveActivity(mAppContext.getPackageManager()) != null) {
            mAppContext.startActivity(checkUpdate);
        }
    }

    /**
     * @see IEonSettingService#unpairDevice(String)
     */
    @JavascriptInterface
    public boolean unpairRCU(String deviceName) throws RemoteException {
        return mService != null && mService.unpairDevice(deviceName);
    }

    /**
     * @see IEonSettingService#getPairDevice()
     */
    @JavascriptInterface
    public String getRCUList() throws RemoteException {
        if (mService == null) {
            return null;
        }
        return new Gson().toJson(mService.getPairDevice());
    }

    /**
     * @see IEonSettingService#getBatteryDevice(String)
     */
    @JavascriptInterface
    public int getRCUBatteryStatus(String deviceName) throws RemoteException {
        return mService != null ? mService.getBatteryDevice(deviceName) : -1;
    }

    @JavascriptInterface
    public void startRCUDAS(int deviceID, String rcuDAS) {
        // TODO: 23/02/18 Add implementation once KAON provides library.
    }

    @JavascriptInterface
    public void starttRCUOAD(int deviceID, String rcuOAD) {
        // TODO: 23/02/18 Add implementation once KAON provides library.
    }

    @JavascriptInterface
    public String getRCUBacklightDuration(int deviceID) {
        // TODO: 23/02/18 Add implementation once KAON provides library.
        return "15s";
    }

    @JavascriptInterface
    public void setRCUBacklightDuration(int deviceID, String rcuBacklightDuration) {
        // TODO: 23/02/18 Add implementation once KAON provides library.
    }

    @JavascriptInterface
    public String getRCUPowerSave(int deviceID) {
        // TODO: 23/02/18 Add implementation once KAON provides library.
        return "On";
    }

    @JavascriptInterface
    public void setRCUPowerSave(int deviceID, String rcuPowerSave) {
        // TODO: 23/02/18 Add implementation once KAON provides library.
    }

    @JavascriptInterface
    public String getAllowedAge() {
        // TODO: 23/02/18 Add implementation once KAON provides library.
        return "18";
    }

    @JavascriptInterface
    public void setAllowedAge(String allowedAge) {
        // TODO: 23/02/18 Add implementation once KAON provides library.
    }

    /**
     * @see IEonSettingService#readTunerQuality()
     */
    @JavascriptInterface
    public float getDvbQuality() throws RemoteException {
        return mService != null ? mService.readTunerQuality() : -1;
    }

    /**
     * @see IEonSettingService#readTunerStrength()
     */
    @JavascriptInterface
    public float getDvbStrength() throws RemoteException {
        return mService != null ? mService.readTunerStrength() : -1;
    }

    /**
     * @see IEonSettingService#readTunerLockStatus()
     */
    @JavascriptInterface
    public int getDvbLock() throws RemoteException {
        return mService != null ? mService.readTunerLockStatus() : -1;
    }

    @JavascriptInterface
    public String getConaxInfo() {
        // TODO: 23/02/18 Add implementation once KAON provides library.
        return "ConaxInfo";
    }

    @JavascriptInterface
    public void checkAppUpdate() {
        mAppUpdater.checkForUpdatesSettings();
    }

    @JavascriptInterface
    @Override
    public void onAuthenticated() {
        super.onAuthenticated();
        syncWatchNext();
    }

    private void syncWatchNext() {
        PropertyFileProvider propertyFileProvider = new DefaultPropertyFileProvider();
        ConfigurationManager configurationManager = new PropertyFileConfigurationManager(mAppContext, propertyFileProvider);
        ChannelsManager channelsManager = new DefaultChannelsManager();
        WatchNextManager watchNextManager = FeatureFactory.getWatchNextManager(channelsManager, configurationManager);
        watchNextManager.syncWatchNext(mAppContext);
    }
}