package com.ug.eon.android.tv.infoserver.entities;

/**
 * Created by goran.arandjelovic on 4/4/18.
 */

public class VodAsset extends Asset {
    private int year;
    private long duration;

    public VodAsset() {
        super(AssetType.VOD);
    }

    public int getYear() { return year; }
    public long getDuration() { return duration; }
}
