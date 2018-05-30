package com.ug.eon.android.tv.config;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DefaultPropertyFileProvider implements PropertyFileProvider {

    private static final String TAG = DefaultPropertyFileProvider.class.getName();

    @Override
    public Properties getProperties(Context context, String filePath) {
        Properties properties = new Properties();
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(filePath);
            properties.load(inputStream);

        } catch (IOException e) {
            Log.d(TAG, "Cannot find/read property file (features.properties)");
        }

        return properties;
    }
}
