package com.ug.eon.android.tv.channels;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import com.ug.eon.android.tv.channels.services.ChannelManagementService;
import com.ug.eon.android.tv.channels.services.WatchNextChannelService;
import com.ug.eon.android.tv.channels.watchnext.WatchNextProgramProvider;

/**
 * Created by nemanja.todoric on 1/21/2018.
 */

public interface ChannelsManager {
    /**
     * Sync main EON channel.
     * @param context
     * @return true if request is accepted and processed, false otherwise.
     */
    public boolean syncMainEonChannel(Context context);

    /**
     * Sync Watch Next channel.
     * @param context
     * @return true if request is accepted and processed, false otherwise.
     */
    public boolean syncWatchNextChannel(Context context);
}
