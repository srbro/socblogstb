package com.ug.eon.android.tv.prefs;

/**
 * Created by nemanja.todoric on 3/27/2018.
 */

public interface SharedPrefsProvider {
    public String getString(String key, String defaultValue);
    public void setString(String key, String value);
    public void remove(String key);
}
