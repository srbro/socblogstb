package com.ug.eon.android.tv.channels.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.ug.eon.android.tv.channels.ChannelUtils;
import com.ug.eon.android.tv.channels.EonProgram;

import java.util.List;

public class ProgramManagementService extends JobService {

    public interface ProgramChangedListener {
        void onProgramsAdded(long channelId, List<EonProgram> programs);
    }

    private AddProgramsTask task;
    private ProgramChangedListener programListener;

    public void setProgramChangedListener(ProgramChangedListener listener) {
        programListener = listener;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Bundle data = params.getTransientExtras();
            long channelId = data.getLong("channelId");
            task = new AddProgramsTask(getApplicationContext(), channelId) {
                @Override
                protected void onPostExecute(List<EonProgram> args) {
                    if (args != null && programListener != null)
                        programListener.onProgramsAdded(channelId, args);

                    jobFinished(params, false);
                }
            };
            task.execute();
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    private static class AddProgramsTask extends AsyncTask<Void, Void, List<EonProgram>> {

        private Context context;
        private long channelId;

        public AddProgramsTask(Context context, long channelId) {
            this.context = context;
            this.channelId = channelId;
        }

        @Override
        protected List<EonProgram> doInBackground(Void... args) {
            return ChannelUtils.addEonMainChannelPrograms(context, channelId);
        }
    }
}
