package com.ug.eon.android.tv.infoserver.entities;

import java.util.List;

/**
 * Created by goran.arandjelovic on 4/4/18.
 */

public class Assets {
    private List<LiveTVAsset> TV;
    private List<CuTVAsset> CUTV;
    private List<VodAsset> VOD;

    public List<LiveTVAsset> getLiveTv() { return TV; }
    public List<CuTVAsset> getCuTv() { return CUTV; }
    public List<VodAsset> getVod() { return VOD; }
}
