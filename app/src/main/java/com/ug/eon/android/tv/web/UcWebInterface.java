package com.ug.eon.android.tv.web;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.ug.eon.android.tv.BuildConfig;
import com.ug.eon.android.tv.NetworkCheck.OnNetworkChange;
import com.ug.eon.android.tv.R;
import com.ug.eon.android.tv.common.InstanceUniqueID;
import com.ug.eon.android.tv.infoserver.AuthHandler;
import com.ug.eon.android.tv.storage.PrefKey;
import com.ug.eon.android.tv.storage.PreferenceUtils;
import com.ug.eon.android.tv.ui.EonIntroView;
import com.ug.eon.android.tv.util.EventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

// Singleton implementation is okey if there is only one activity, if this situation changes
// please change the singleton pattern - sending Activity mActivity is bad in that situation
public class UcWebInterface extends PlayerControls implements EventListener, OnNetworkChange, Serializable, AuthFailedHandler {
    private static final String TAG = UcWebInterface.class.getName();
    private static final String JS_NAMESPACE = "ANDROMAN";
    private static final String EVENT_ID_READ = "ID read";
    private static final String PREFS_NAME = "EData";

    private InstanceUniqueID mInstanceId;
    private JSONArray mCallbackFunctions;
    private WebView mWebView;
    protected Activity mActivity;
    private String drmConaxJson;
    private StartupParameters startupParams;

    private AuthHandler authHandler;

    public void setAuthHandler(AuthHandler ah) {
        authHandler = ah;
    }

    public UcWebInterface(final Activity context, StartupParameters params) {
        Log.d(TAG, "UcWebInterface created!");
        mActivity = context;
        mCallbackFunctions = new JSONArray();
        mInstanceId = new InstanceUniqueID(mActivity);
        startupParams = params != null ? params : new StartupParameters();

        mWebView = mActivity.findViewById(R.id.main_web_view);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setMinimumFontSize(1);
        webSettings.setDomStorageEnabled(true);

        if (startupParams.getStartupMode().equals("stbprovisioning")) {
            mWebView.setBackgroundColor(Color.BLACK);
        }
        else {
            playEonSplash();
        }

        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.addJavascriptInterface(this, JS_NAMESPACE);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mWebView.setBackgroundResource(0);
                mWebView.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        if (width >= 1920) {
            mWebView.setInitialScale(100);
        } else {
            mWebView.getSettings().setUseWideViewPort(true);
            mWebView.setInitialScale(66);
        }

        // Set User Agent.
        String userAgent = mWebView.getSettings().getUserAgentString();
        mWebView.getSettings().setUserAgentString(userAgent + " "
                + mActivity.getString(R.string.user_agent));

        try {
            Log.d(TAG, "-------Komanda---: " + execCmd("getprop net.dns1"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadUrl();
    }

    protected WebView getWebView() {
        return mWebView;
    }

    private static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
    }

    @JavascriptInterface
    private static String execCmd(String cmd) throws IOException {
        java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @JavascriptInterface
    public void initHal(String callbacks) {//, String servers) {
        Log.d(TAG, "initHal " + callbacks);
        analyzeClbks(callbacks);
        try {
            mCallbackFunctions = new JSONArray();
            JSONArray array = new JSONArray(callbacks);
            for (int i = 0; i < array.length(); i++) {
                Log.d(TAG, "INIT HAL ITEM: " + array.get(i));
                mCallbackFunctions.put(array.get(i));
            }
            mCallbackFunctions.put("onPlayerStateChanged");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //store("servers", servers);
    }

    private boolean onResumePauseActivity = false;
    private static final String strOnResumePauseActivity = "onResumePauseActivity";

    private boolean onNativeKeyUp = false;
    private static final String strOnNativeKeyUp = "onNativeKeyUp";

    private boolean onNativeKeyDown = false;
    private static final String strOnNativeKeyDown = "onNativeKeyDown";

    private boolean onNetworkCheck = false;
    private static final String strOnNetworkCheck = "onNetworkCheck";

    private boolean onPlayerStateChanged = false;
    private static final String strOnPlayerStateChanged = "onPlayerStateChanged";

    private boolean onBitrateChanged = false;
    private static final String strOnBitrateChanged = "onBitrateChanged";

    private void analyzeClbks(String callbacks) {
        try {
            JSONArray array = new JSONArray(callbacks);
            for (int i = 0; i < array.length(); i++) {
                Log.d("Ananlyzing", array.get(i).toString());
                switch (array.get(i).toString()) {
                    case strOnResumePauseActivity:
                        onResumePauseActivity = true;
                        break;
                    case strOnNativeKeyUp:
                        onNativeKeyUp = true;
                        break;
                    case strOnNativeKeyDown:
                        onNativeKeyDown = true;
                        break;
                    case strOnNetworkCheck:
                        onNetworkCheck = true;
                        break;
                    case strOnPlayerStateChanged:
                        onPlayerStateChanged = true;
                        break;
                    case strOnBitrateChanged:
                        onBitrateChanged = true;
                        break;
                    default:
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("onNativeKeyUp", String.valueOf(onNativeKeyUp));
        Log.d("onNativeKeyDown", String.valueOf(onNativeKeyDown));
    }

    public void reloadState(boolean value) {
        Log.d(TAG, "ANDROMAN refresh " + value);

        if (onResumePauseActivity) {
            mWebView.loadUrl("javascript:" + strOnResumePauseActivity + "(" + value + ")");
        }
    }

    public boolean dispatchNativeKey(int keyCode, int action) {
        if (action == KeyEvent.ACTION_UP) {
            if (onNativeKeyUp) {
                mWebView.loadUrl("javascript:" + strOnNativeKeyUp + "(" + keyCode + ")");
            }
            return onNativeKeyUp;
        }
        else if (action == KeyEvent.ACTION_DOWN) {
            if (onNativeKeyDown) {
                mWebView.loadUrl("javascript:" + strOnNativeKeyDown + "(" + keyCode + ")");
            }
            return onNativeKeyDown;
        }
        return false;
    }

    public void doDeepLink(String action, String data) {
        Log.i("WebWeb", "calling deep link: " + action);

        switch (action) {
            case "nowontv":
                mWebView.loadUrl("javascript:window.changeRoute('NowTv')");
                break;
            case "guide":
                mWebView.loadUrl("javascript:window.changeRoute('Guide')");
                break;
            case "livetv":
                mWebView.loadUrl("javascript:window.changeRoute('PlayerTv')");
                break;
            case "vod":
                mWebView.loadUrl("javascript:window.changeRoute('VodLanding')");
                break;
            case "radio":
                mWebView.loadUrl("javascript:window.changeRoute('Radio')");
                break;
            case "home":
                mWebView.loadUrl("javascript:window.changeRoute('Home')");
                break;
            case "content": {
                try {
                    JSONObject deepLinkContent = new JSONObject(data);
                    String type = deepLinkContent.getString("type");
                    Integer id = deepLinkContent.getInt("id");
                    Integer channelId = 0;
                    try {
                        channelId = deepLinkContent.getInt("channelId");
                    }
                    catch (Exception e) {
                        Log.e(TAG, "ChannelId doesn't exist in JSON content intent data");
                    }
                    mWebView.loadUrl("javascript:linkToContent('" + type + "',{\"id\": " + id.toString() + ", \"channelId\": " + channelId.toString() + "})");
                }
                catch(Exception e) {
                    Log.e(TAG, "Error in parsing JSON content intent data");
                }
            }

            default:
                break;
        }
    }

    public void dispatchNetworkCheck(boolean value) {
        if (onNetworkCheck) {
            mWebView.loadUrl("javascript:" + strOnNetworkCheck + "(" + value + ")");
        }
    }

    @Override
    public void onConnectionChange(final boolean result) {
        Log.d(TAG, "onConnectionChange " + result);
        if (onNetworkCheck)
            mWebView.loadUrl("javascript:" + strOnNetworkCheck + "(" + result + ")");
    }

    @Override
    public void onBitrateChange(int bitrate) {
        Log.d("onBitrateChange ", bitrate + "");
        final int finalEvent = bitrate;
        if (onBitrateChanged) mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mWebView.loadUrl("javascript:" + strOnBitrateChanged + "(" + finalEvent + ")");
            }
        });
    }

    @JavascriptInterface
    public String getWrapperVersion() {
        PackageManager manager = mActivity.getPackageManager();
        PackageInfo info = null;
        String version = "00";
        try {
            info = manager.getPackageInfo(mActivity.getPackageName(), 0);
            version = info.versionName;
            Log.d(TAG, "WRAPPER VERSION " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "Version Error " + e.toString());
        }
        return version;
    }

    @JavascriptInterface
    public void setAppStatus(String currentStatus) {
        if(!startupParams.getStartupAction().isEmpty() && currentStatus.equals("loaded")) {
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Log.i(TAG, "calling doDeepLink(), startup action: " + startupParams.getStartupAction());
                    doDeepLink(startupParams.getStartupAction(), startupParams.getStartupActionData());
                }
            });
        }
    }

    @JavascriptInterface
    public void onEnterExitZap(final boolean value) {
        Log.d(TAG, "Web Interface surfice " + value);
//        ucPlayer.hideSurfaceView(value);
    }

    @JavascriptInterface
    public void appLoaded(final boolean value) {
        Log.d(TAG, "App loaded event " + value);
//        ucPlayer.hideSurfaceView(value);
    }

    @JavascriptInterface
    public void test() {
    }

    @JavascriptInterface
    public String getMac() {
        //String uuid = getUniquePseudoID();
        String uuid = mInstanceId.getId();
        Answers.getInstance().logCustom(new CustomEvent(EVENT_ID_READ).putCustomAttribute("ID", uuid));
        return uuid;
    }

    @JavascriptInterface
    public String getMACNew() {
        String mac;
        mac = getMACAddress("eth0");
        return mac;
    }

    @JavascriptInterface
    public String getMACNewWIFI() {
        String mac;
        mac = getMACAddress("wlan0");
        return mac;
    }

    @JavascriptInterface
    public String getSerial() {
        if (getDeviceType().equals("stb")) {
            return getDeviceSerial();
        } else {
            return mInstanceId.getId();
        }
    }

    @JavascriptInterface
    public String getNetworkInfo() {
        String s_dns1;
        String s_dns2;
        String s_gateway;
        String s_ipAddress;
        String s_leaseDuration;
        String s_netmask;
        String s_serverAddress;

        DhcpInfo d;
        WifiManager wifii;
        wifii = (WifiManager) mActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        d = wifii.getDhcpInfo();

        s_dns1 = "DNS 1: " + String.valueOf(d.dns1);
        s_dns2 = "DNS 2: " + String.valueOf(d.dns2);
        s_gateway = "Default Gateway: " + String.valueOf(d.gateway);
        s_ipAddress = "IP Address: " + String.valueOf(d.ipAddress);
        s_leaseDuration = "Lease Time: " + String.valueOf(d.leaseDuration);
        s_netmask = "Subnet Mask: " + String.valueOf(d.netmask);
        s_serverAddress = "Server IP: " + String.valueOf(d.serverAddress);

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("DNS1", s_dns1);
            jsonObject.put("DNS2", s_dns2);
            jsonObject.put("Default Gateway", s_gateway);
            jsonObject.put("IP Address:", s_ipAddress);
            jsonObject.put("Lease Time:", s_leaseDuration);
            jsonObject.put("Subnet Mask:", s_netmask);
            jsonObject.put("Server IP:", s_serverAddress);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    @JavascriptInterface
    public String getLocalIpAddress() {
        String localIP = "";
        try {
            localIP = execCmd("getprop dhcp.eth0.ipaddress");
            return localIP;
        } catch (Exception err) {
            Log.e(TAG, "error getting localIP " + err.toString());
        }
        return localIP;
    }

    @JavascriptInterface
    public String getSubnetMask() {
        String mask = "";
        try {
            mask = execCmd("getprop dhcp.eth0.mask");
            return mask;
        } catch (Exception err) {
            Log.e(TAG, "error getting mask " + err.toString());
        }
        return mask;
    }

    @JavascriptInterface
    public String getPrimaryDNS() {
        String primaryDNS = "";
        try {
            primaryDNS = execCmd("getprop dhcp.eth0.dns1");
            return primaryDNS;
        } catch (Exception err) {
            Log.e(TAG, "error primaryDNS " + err.toString());
        }
        return primaryDNS;
    }

    @JavascriptInterface
    public String getSecondaryDns() {
        String secondaryDNS = "";
        try {
            secondaryDNS = execCmd("getprop dhcp.eth0.dns2");
            return secondaryDNS;
        } catch (Exception err) {
            Log.e(TAG, "error secondaryDNS " + err.toString());
        }
        return secondaryDNS;
    }

    @JavascriptInterface
    public String getGateway() {
        String gateway = "";
        try {
            gateway = execCmd("getprop dhcp.eth0.gateway");
            return gateway;
        } catch (Exception err) {
            Log.e(TAG, "error getting gateway " + err.toString());
        }
        return gateway;
    }

    @JavascriptInterface
    public String isItOnLAN() {
        String lanOn = "";
        try {
            lanOn = execCmd("getprop dhcp.eth0.result").toString();
            lanOn = String.valueOf(lanOn);
            return lanOn;
        } catch (Exception err) {
            Log.e("error LAN", err.toString());
        }
        Log.d(TAG, "final lanOn = " + lanOn);
        return lanOn;
    }

    @JavascriptInterface
    public String isItOnWiFi() {
        String wifiOn = "";
        try {
            wifiOn = execCmd("getprop dhcp.wlan0.result").toString();
            wifiOn = String.valueOf(wifiOn);

            return wifiOn;
        } catch (Exception err) {
            Log.e(TAG, "error LAN" + err.toString());
        }
        Log.d(TAG, "final wifiOn = " + wifiOn);
        return wifiOn;
    }

    @JavascriptInterface
    public String getLocalIpAddressWiFi() {
        String localIPWiFi = "";
        try {
            localIPWiFi = execCmd("getprop dhcp.wlan0.ipaddress");
            return localIPWiFi;
        } catch (Exception err) {
            Log.e(TAG, "error localIPWiFi " + err.toString());
        }
        return localIPWiFi;
    }

    @JavascriptInterface
    public String getSubnetMaskWiFi() {
        String subnetWiFi = "";
        try {
            subnetWiFi = execCmd("getprop dhcp.wlan0.mask");
            return subnetWiFi;
        } catch (Exception err) {
            Log.e(TAG, "error localIPWiFi " + err.toString());
        }
        return subnetWiFi;
    }

    @JavascriptInterface
    public String getPrimaryDNSWiFi() {
        String PrimaryDNSWiFi = "";
        try {
            PrimaryDNSWiFi = execCmd("getprop dhcp.wlan0.dns1");
            return PrimaryDNSWiFi;
        } catch (Exception err) {
            Log.e(TAG, "error PrimaryDNSWiFi " + err.toString());
        }
        return PrimaryDNSWiFi;
    }

    @JavascriptInterface
    public String getSecondaryDnsWiFi() {
        String SecondaryDnsWiFi = "";
        try {
            SecondaryDnsWiFi = execCmd("getprop dhcp.wlan0.dns2");
            return SecondaryDnsWiFi;
        } catch (Exception err) {
            Log.e(TAG, "error SecondaryDnsWiFi " + err.toString());
        }
        return SecondaryDnsWiFi;
    }

    @JavascriptInterface
    public String getGatewayWiFi() {
        String GatewayWiFi = "";
        try {
            GatewayWiFi = execCmd("getprop dhcp.wlan0.gateway");
            return GatewayWiFi;
        } catch (Exception err) {
            Log.e(TAG, "error GatewayWiFi" + err.toString());
        }
        return GatewayWiFi;
    }

    @JavascriptInterface
    public String getSDKVersion() {
        String sdkver = "";
        try {
            sdkver = execCmd("getprop ro.build.version.sdk");
            return sdkver;
        } catch (Exception err) {
            Log.e(TAG, "error sdkver " + err.toString());
        }
        return sdkver;
    }

    @JavascriptInterface
    public String getReleaseVersion() {
        String releasever = "";
        try {
            releasever = execCmd("getprop ro.build.version.release");
            return releasever;
        } catch (Exception err) {
            Log.e(TAG, "error releasever " + err.toString());
        }
        return releasever;
    }

    @JavascriptInterface
    public String getDeviceModel() {
        String deviceModel = "";
        try {
            deviceModel = execCmd("getprop ro.product.model");
            Log.d(TAG, "deviceModel" + deviceModel);
            return deviceModel;
        } catch (Exception err) {
            Log.e(TAG, "error deviceModel " + err.toString());
        }
        return deviceModel;
    }

    @JavascriptInterface
    public String getDeviceManufacturer() {
        String deviceManufacturer = "";
        try {
            deviceManufacturer = execCmd("getprop ro.product.manufacturer");
            return deviceManufacturer;
        } catch (Exception err) {
            Log.e(TAG, "err deviceManufacturer " + err.toString());
        }
        return deviceManufacturer;
    }

    @JavascriptInterface
    public String getDeviceModelGroup() {
        String deviceModelGroup = "";
        try {
            deviceModelGroup = execCmd("getprop ro.nrdp.modelgroup");
            return deviceModelGroup;
        } catch (Exception err) {
            Log.e(TAG, "err deviceModelGroup " + err.toString());
        }
        return deviceModelGroup;
    }

    @JavascriptInterface
    public String getWrapperCommitHash() {
        return BuildConfig.COMMIT_HASH;
    }

    @JavascriptInterface
    public void exitApp() {
        mActivity.setResult(Activity.RESULT_OK);
        mActivity.finish();
    }

    @JavascriptInterface
    public String load(String name) {
        Log.d(TAG, JS_NAMESPACE + " load " + name);
        SharedPreferences settings = mActivity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String res = settings.getString(name, "");
        Log.d(TAG, JS_NAMESPACE + " loaded " + res);

//        int duration = Toast.LENGTH_SHORT;
//        Toast toast = Toast.makeText(mActivity, JS_NAMESPACE + " load: " + var_name + " : " + res, duration);
//        toast.show();

        return res;
    }

    @JavascriptInterface
    public void store(String name, String val) {
        Log.d(TAG, JS_NAMESPACE + " store " + name + " " + val);
        SharedPreferences settings = mActivity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name, val);
        editor.apply();
        Log.d(TAG, JS_NAMESPACE + " stored " + name);
    }

    @JavascriptInterface
    public void remove(String name) {
        Log.d(TAG, JS_NAMESPACE + " remove " + name);
        SharedPreferences settings = mActivity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(name);
        editor.apply();
        Log.d(TAG, JS_NAMESPACE + " removed " + name);
    }

    @JavascriptInterface
    public String hasInternet() {
        ConnectivityManager cm = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        // connected to the internet
        try {
            if (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return "true";
            } else if (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_ETHERNET) {
                return "true";
            } else {
                // not connected to the internet
                return "false";
            }
        } catch (Exception err) {
            Log.e(TAG, "err deviceManufacturer " + err.toString());
            return "false";
        }
    }

    @JavascriptInterface
    public boolean clearCache() {
        try {
            mWebView.clearCache(true);
            return true;
        } catch (Exception err) {
            Log.e(TAG, "error clearing cache " + err.toString());
            return false;
        }
    }

    /**
     * Method returns string device type. Device type can be 'stb' or 'android-tv'.
     * @return Stb or Android-tv device type depends on application build.
     */
    @JavascriptInterface
    public String getDeviceType() {
        return mActivity.getString(R.string.device_type);
    }

    /**
     * Gets the hardware serial. On android 8.0 > READ_PHONE_STATE permission is required
     * otherwise this method will return 'unknown'.
     *
     * @return Device hardware serial id.
     */
    @JavascriptInterface
    public String getDeviceSerial() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Build.getSerial();
        } else {
            return Build.SERIAL;
        }
    }

    @Override
    public void sendEvent(int event) {
        Log.d("CUSTOM EVENT ", event + "");
        final int finalEvent = event;
        if (mCallbackFunctions != null) {
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d(TAG, "UI thread, i am the UI thread sendEvent");
                    for (int i = 0; i < mCallbackFunctions.length(); i++) {
                        try {
                            String callback = mCallbackFunctions.get(i).toString(); // should be onPlayerStateChanged
                            if (callback.equals("onPlayerStateChanged")) {
                                Log.d(TAG, "sentevent: " + event);
                                mWebView.loadUrl("javascript:" + callback + "(" + finalEvent + ")");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    @JavascriptInterface
    public void drmHeaderRecived(String conaxJson) {
        drmConaxJson = conaxJson;
    }

    public void getDrmHeader() {
        drmConaxJson = null;
        mWebView.loadUrl("javascript:getDrmHeader()");
    }

    private void loadUrl() {
        String simpleUrl = mActivity.getResources().getString(R.string.webview_address);
        String clientId = mActivity.getResources().getString(R.string.clientId);
        String clientSecret = mActivity.getResources().getString(R.string.clientSecret);
        String productId = "eon";
        String mode = startupParams.getStartupMode().isEmpty() ? "default" : startupParams.getStartupMode();
        final String url = simpleUrl.concat("?clientId="+ clientId + "&clientSecret=" + clientSecret + "&productId=" + productId + "&mode=" + mode + "&t=" + SystemClock.currentThreadTimeMillis());
        mWebView.loadUrl(url);
    }

    @JavascriptInterface
    public void onAuthenticated() {
        Log.i("UC_AUTH/" + TAG, "client authenticated. notifying..");
        authHandler.onAuthenticated();
    }

    @Override
    public void onAuthFailed() {
        Log.i("UC_AUTH/" + TAG, "auth token expired. asking for refresh..");
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl("javascript:onRefreshToken()");
            }
        });
    }

    /**
     * Shows video splash screen only first time.
     */
    private void playEonSplash() {
        if (!PreferenceUtils.getBoolean(mActivity, PrefKey.EON_SPLASH_SCREEN)) {
            EonIntroView eonIntroView = new EonIntroView(mActivity);
            mActivity.<RelativeLayout>findViewById(R.id.parent_of_all).addView(eonIntroView);
            eonIntroView.play();
            PreferenceUtils.setBoolean(mActivity, PrefKey.EON_SPLASH_SCREEN, true);
        }
    }
}
