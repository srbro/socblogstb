package com.ug.eon.android.tv.tif.parser;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

import com.ug.eon.android.tv.web.hal.WebDeviceInterface;

import net.quber.eonsettingservice.IEonSettingService;

/**
 * This task parse arrays with channels provided by IS to json format that KAON expects.
 * DVB-C channel list format: SETTOPBOX-317
 *
 * Created by milan.adamovic on 3/8/18.
 */
public class DvbChannelParserTask extends AsyncTask<String, Integer, Boolean> {
    private static final String TAG = DvbChannelParserTask.class.getName();
    private IEonSettingService mService;
    private Context mContext;

    private long mStartTimeMillis;

    public DvbChannelParserTask(IEonSettingService service, Context context) {
        mService = service;
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "Starting channels parser task");
        mStartTimeMillis = System.currentTimeMillis();
    }

    @Override
    protected Boolean doInBackground(String... channelsLists) {
        if (channelsLists == null || channelsLists.length < 1) {
            Log.d(TAG, "No channels to parse");
            cancel(true);
            return false;
        }
        DvbChannelParser parser = new DvbChannelParser(channelsLists[0], channelsLists[1]);
        parser.setAsyncTask(this);

        boolean updateStatus = false;
        try {
            updateStatus = mService.updateDtvChannels(parser.parse().toString());
        } catch (RemoteException e) {
            Log.e(TAG, "error: " + e.getMessage());
        }
        return updateStatus;
    }

    @Override
    protected void onCancelled(Boolean s) {
        super.onCancelled(s);
        Log.d(TAG, "Task has been canceled!");
    }

    @Override
    protected void onPostExecute(Boolean updateStatus) {
        super.onPostExecute(updateStatus);
        Log.d(TAG, "Task ended in: " + (System.currentTimeMillis() - mStartTimeMillis) + " ms");
        Log.d(TAG, "Channel update: " + updateStatus);
        // no need to set channels (no change in list) - report setting is finished
        if (!updateStatus) {
            mContext.sendBroadcast(new Intent(WebDeviceInterface.BROADCAST_CHANNELS_SET));
        }
    }
}