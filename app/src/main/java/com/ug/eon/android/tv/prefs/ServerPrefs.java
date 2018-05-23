package com.ug.eon.android.tv.prefs;

/**
 * Created by nemanja.todoric on 3/27/2018.
 * Intended to contain server urls from shared preferences.
 * Also used for directly parsing auth object JSON format in shared preferences
 */

public class ServerPrefs {
    private String name;
    private String infoServerBaseUrl;
    private String apiVersion;
    private String imageServerUrl;
    private String staticServer;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfoServerBaseUrl() {
        return infoServerBaseUrl;
    }

    public void setInfoServerBaseUrl(String infoServerBaseUrl) {
        this.infoServerBaseUrl = infoServerBaseUrl;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getImageServerUrl() {
        return imageServerUrl;
    }

    public void setImageServerUrl(String imageServerUrl) {
        this.imageServerUrl = imageServerUrl;
    }

    public String getStaticServer() {
        return staticServer;
    }

    public void setStaticServer(String staticServer) {
        this.staticServer = staticServer;
    }
}
