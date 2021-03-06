package com.ug.eon.android.tv.channels;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import com.ug.eon.android.tv.channels.services.ChannelManagementService;
import com.ug.eon.android.tv.channels.services.WatchNextChannelService;

public class DefaultChannelsManager implements ChannelsManager {

    private static final String TAG = ChannelsManager.class.getName();
    private static int WATCH_NEXT_SYNC_INTERVAL = 30 * 60 * 1000;

    public boolean syncMainEonChannel(Context context) {
        Log.d(TAG, "Scheduling main EON channel sync service");
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.schedule(new JobInfo.Builder(0,
                new ComponentName(context, ChannelManagementService.class))
                .setMinimumLatency(0).build());

        return true;
    }

    public boolean syncWatchNextChannel(Context context) {
        Log.d(TAG, "Scheduling watch next channel sync service");
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.schedule(new JobInfo.Builder(3,
                new ComponentName(context, WatchNextChannelService.class))
                .setPeriodic(WATCH_NEXT_SYNC_INTERVAL)
                .build());

        return true;
    }
}
