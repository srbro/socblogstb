package com.ug.eon.android.tv.infoserver.entities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by goran.arandjelovic on 4/4/18.
 */

public class LiveTVAsset extends Asset {
    private int channelId;
    private String startTime;
    private String endTime;
    private List<Image> channelLogos;

    public LiveTVAsset() {
        super(AssetType.LIVETV);
    }
    public LiveTVAsset(AssetType assetType) {
        super(assetType);
    }

    public int getChannelId() { return channelId; }
    public long getDuration() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
        try {
            Date start = dateFormat.parse(startTime);
            Date end = dateFormat.parse(endTime);
            return end.getTime() - start.getTime();
        }
        catch (Exception e) {

        }
        return 0;
    }
    public List<Image> getChannelLogos() { return channelLogos; }
}
