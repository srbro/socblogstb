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

public final class ChannelsManager {

    private static final String TAG = ChannelsManager.class.getName();
    private static int WATCH_NEXT_SYNC_INTERVAL = 30 * 60 * 1000;

    public static void syncChannels(Context context) {
        syncMainEonChannel(context);
        syncWatchNextChannel(context);
    }

    public static void syncMainEonChannel(Context context) {
        Log.d(TAG, "Scheduling main EON channel sync service");
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.schedule(new JobInfo.Builder(0,
                new ComponentName(context, ChannelManagementService.class))
                .setMinimumLatency(0).build());
    }

    public static void syncWatchNextChannel(Context context) {
        Log.d(TAG, "Scheduling watch next channel sync service");
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.schedule(new JobInfo.Builder(3,
                new ComponentName(context, WatchNextChannelService.class))
                .setPeriodic(WATCH_NEXT_SYNC_INTERVAL)
                .setMinimumLatency(0).build());
    }
}
