package com.ug.eon.android.tv.infoserver.entities;

import android.util.SparseArray;

/**
 * Created by goran.arandjelovic on 4/3/18.
 */

public enum AssetType {
    LIVETV(1),
    CUTV(2),
    VOD(3);
    private int mOrdinal;
    private static SparseArray<AssetType> map = new SparseArray<>();
    AssetType(int ordinal) {
        mOrdinal = ordinal;
    }
    static {
        for(AssetType asset : AssetType.values()) {
            map.put(asset.getOrdinal(), asset);
        }
    }
    public static AssetType valueOf(int v) {
        return map.get(v, null);
    }
    public int getOrdinal() {
        return mOrdinal;
    }
}
