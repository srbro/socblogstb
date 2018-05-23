package com.ug.eon.android.tv.channels.watchnext;

import com.ug.eon.android.tv.infoserver.entities.Image;
import com.ug.eon.android.tv.infoserver.entities.WatchNextItem;

public class EonWatchNextProgram {
    private int id;
    private int channelId;
    private String type;
    private String title;
    private String shortDescription;
    private String startTime;
    private String endTime;
    private String imageURI;
    private String channelLogoURI;

    private long watchNextId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getChannelLogoURI() {
        return channelLogoURI;
    }

    public void setChannelLogoURI(String channelLogoURI) {
        this.channelLogoURI = channelLogoURI;
    }

    public long getWatchNextId() {
        return watchNextId;
    }

    public void setWatchNextId(long watchNextId) {
        this.watchNextId = watchNextId;
    }
}
