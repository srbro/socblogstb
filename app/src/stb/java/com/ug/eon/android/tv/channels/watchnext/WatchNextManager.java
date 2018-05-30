package com.ug.eon.android.tv.channels.watchnext;

import android.content.Context;

public interface WatchNextManager {

    /**
     * Start synchronizing Watch Next channel.
     * @param context
     * @return returns true if request is accepted and forwarded further, false otherwise.
     */
    public boolean syncWatchNext(Context context);
}
