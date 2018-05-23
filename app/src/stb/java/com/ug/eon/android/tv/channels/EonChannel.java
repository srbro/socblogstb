package com.ug.eon.android.tv.channels;

/**
 * Created by nemanja.todoric on 1/19/2018.
 */

public class EonChannel {

    private long channelId;
    private String name;
    private String description;
    private String appLinkIntentUri;
    private int channelLogo;

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAppLinkIntentUri() {
        return appLinkIntentUri;
    }

    public void setAppLinkIntentUri(String appLinkIntentUri) {
        this.appLinkIntentUri = appLinkIntentUri;
    }

    public int getChannelLogo() {
        return channelLogo;
    }

    public void setChannelLogo(int channelLogo) {
        this.channelLogo = channelLogo;
    }
}

