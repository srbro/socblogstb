package com.ug.eon.android.tv.channels.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ug.eon.android.tv.channels.ChannelUtils;
import com.ug.eon.android.tv.channels.watchnext.WatchNextProgramProvider;
import com.ug.eon.android.tv.infoserver.entities.WatchNextItem;
import com.ug.eon.android.tv.prefs.PreferenceManagerImpl;
import com.ug.eon.android.tv.prefs.SharedPrefsProviderImpl;

import java.util.List;

public class WatchNextChannelService extends JobService {

    private static String TAG = WatchNextChannelService.class.getName();

    AddWatchNextProgramsTask task;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        Log.i(TAG, "WatchNext channel sync service started");
        task = new WatchNextChannelService.AddWatchNextProgramsTask(getApplicationContext());
        task.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (task != null)
            task.cancel(true);

        return true;
    }

    private static class AddWatchNextProgramsTask extends AsyncTask<Void, Void, Boolean> {

        private Context context;

        public AddWatchNextProgramsTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... args) {
            List<WatchNextItem> watchNextItems = WatchNextProgramProvider.getWatchNextPrograms(context);
            ChannelUtils.syncWatchNextChannel(context, watchNextItems, new PreferenceManagerImpl(new SharedPrefsProviderImpl(context)));
            Log.i(TAG, "watch next channel sync completed");
            return true;
        }
    }
}
