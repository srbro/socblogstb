package com.ug.eon.android.tv.infoserver.entities;

import java.util.List;

/**
 * Created by goran.arandjelovic on 4/3/18.
 */

public class Asset {
    private long id;
    private transient AssetType assetType;
    private String title;
    private String shortDescription;
    private List<Image> images;

    public Asset(AssetType assetType) {
        this.assetType = assetType;
    }

    public long getId() { return id; }
    public AssetType getAssetType() { return assetType; }
    public String getTitle() { return title; }
    public String getShortDescription() { return shortDescription; }
    public List<Image> getImages() { return images; }

    public int getChannelId() { return 0; }
    public int getYear() { return 0; }
    public long getDuration() { return 0; }
    public List<Image> getChannelLogos() { return null; }
}
