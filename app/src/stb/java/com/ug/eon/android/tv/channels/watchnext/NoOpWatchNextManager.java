package com.ug.eon.android.tv.channels.watchnext;

import android.content.Context;

public class NoOpWatchNextManager implements WatchNextManager {
    @Override
    public boolean syncWatchNext(Context context) {
        // as it name stands: this is no op.
        return false; // request not accepted
    }
}
