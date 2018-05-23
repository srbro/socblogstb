package com.ug.eon.android.tv.web.hal;

import android.app.Activity;

import com.ug.eon.android.tv.web.UcWebInterface;

/**
 * This web interface should contain interfaces for android-tv device.
 * Created by milan.adamovic on 3/12/18.
 */
public class WebDeviceInterface extends UcWebInterface {
    private static final String TAG = WebDeviceInterface.class.getName();

    public WebDeviceInterface(Activity activity) {
        super(activity, null);
    }
}