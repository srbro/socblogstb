package com.ug.eon.android.tv.channels.services;


import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.media.tv.TvContractCompat;

import com.ug.eon.android.tv.channels.ChannelUtils;

public class ChannelManagementService extends JobService {

    public static interface ChannelChangedListener {
        public void onChannelAdded(long channelId);
    }

    private static class TaskResultData {
        boolean success;
        long channelId;
    }

    private AddChannelsTask task;
    private ChannelChangedListener channelListener;

    public void setChannelChangedListener(ChannelChangedListener listener) {
        channelListener = listener;
    }

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        task = new AddChannelsTask(getApplicationContext()) {
            @Override
            protected void onPostExecute(TaskResultData taskResultData) {
                super.onPostExecute(taskResultData);
                boolean shouldRescheduleJob = !taskResultData.success;
                jobFinished(jobParameters, shouldRescheduleJob);
                if (channelListener != null)
                    channelListener.onChannelAdded(taskResultData.channelId);
                addPrograms(getApplicationContext(), taskResultData.channelId);
            }
        };
        task.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (task != null)
            task.cancel(true);

        return true;
    }

    private static class AddChannelsTask extends AsyncTask<Void, Void, TaskResultData> {

        private Context context;

        public AddChannelsTask(Context context) {
            this.context = context;
        }

        @Override
        protected TaskResultData doInBackground(Void... args) {
            TaskResultData result = new TaskResultData();
            if (!ChannelUtils.mainEonChannelExists(context)) {
                long mainChannelId = ChannelUtils.createEonMainChannel(context);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    TvContractCompat.requestChannelBrowsable(context, mainChannelId);
                }
                result.channelId = mainChannelId;
            }

            result.success = true;
            return result;
        }
    }

    private void addPrograms(Context context, long channelId) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder jobBuilder =
                new JobInfo.Builder(1,
                        new ComponentName(context, ProgramManagementService.class))
                        .setMinimumLatency(0L);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            JobInfo.TriggerContentUri triggerContentUri = new JobInfo.TriggerContentUri(
                    TvContractCompat.buildChannelUri(channelId),
                    0);
            jobBuilder.addTriggerContentUri(triggerContentUri);
            jobBuilder.setTriggerContentMaxDelay(0L);
            jobBuilder.setTriggerContentUpdateDelay(0L);
        }

        Bundle data = new Bundle();
        data.putLong("channelId", channelId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            jobBuilder.setTransientExtras(data);
        }

        scheduler.schedule(jobBuilder.build());
    }
}
