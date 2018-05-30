package com.ug.eon.android.tv.config;

import android.content.Context;

import java.util.Properties;

public interface PropertyFileProvider {
    public Properties getProperties(Context context, String filePath);
}
