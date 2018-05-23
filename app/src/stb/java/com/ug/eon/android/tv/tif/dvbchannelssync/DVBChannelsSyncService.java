package com.ug.eon.android.tv.tif.dvbchannelssync;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

/**
 * Created by goran.arandjelovic on 3/12/18.
 */

public class DVBChannelsSyncService extends JobService {
    private static final String TAG = DVBChannelsSyncService.class.getName();
    private static Runnable sSyncRunnable;
    private static boolean sFirstStart = true;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        try {
            if(sFirstStart) {
                sFirstStart = false;
            } else {
                sSyncRunnable.run();
            }
        }
        catch(Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

    public static void scheduleDVBChannelsSync(Context context, Runnable syncTaskRunnable, long minutes)
    {
        sSyncRunnable = syncTaskRunnable;
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder jobBuilder =
                new JobInfo.Builder(2,
                        new ComponentName(context, DVBChannelsSyncService.class)).setPeriodic(minutes*60*1000);


        JobInfo info = jobBuilder.build();

        scheduler.schedule(info);
    }

    public static final String getChannelDataUrl() {
        return "javascript:updateChannelsData()";
    }
}
