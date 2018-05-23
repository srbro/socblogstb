package com.ug.eon.android.tv.tif.provider;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.media.tv.TvContract;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;

import com.ug.eon.android.tv.web.hal.WebDeviceInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by milan.adamovic on 3/20/18.
 */
public class ChannelDataManager {
    private static final String TAG = ChannelDataManager.class.getName();
    private static final String DVBC_INPUT_ID = "net.quber.tvinput/.service.QuberTvInputService/HW0";
    private static final int MSG_UPDATE_CHANNELS = 1000;
    private static final long EXECUTE_DELAY = 500;
    private ContentObserver mContentObserver;
    private ChannelUpdater mChannelUpdater;
    private Handler mHandler;
    private Context mContext;
    private final List<Channel> mChannelList;

    public ChannelDataManager(Context context) {
        mContext = context;
        mChannelList = Collections.synchronizedList(new ArrayList<>());
    }

    @Nullable
    public Channel getChannel(int networkId, int streamId, int serviceId) {
        if (mChannelList == null)
            return null;

        synchronized (mChannelList) {
            Iterator iterator = mChannelList.iterator();
            while (iterator.hasNext()) {
                Channel channel = (Channel) iterator.next();
                if (channel.getOriginalNetworkId() == networkId
                        && channel.getTransportStreamId() == streamId
                        && channel.getServiceId() == serviceId) {
                    return channel;
                }
            }
        }
        return null;
    }

    /**
     * Starts update channel task which fetches data from TvProvider.
     */
    private void updateChannels() {
        if (mChannelUpdater != null) {
            mChannelUpdater.cancel(true);
            mChannelUpdater = null;
        }
        mChannelUpdater = new ChannelUpdater();
        mChannelUpdater.execute();
    }

    @MainThread
    private static class ChannelHandler extends Handler {
        private ChannelDataManager mChannelDataManager;

        ChannelHandler(ChannelDataManager channelDataManager) {
            mChannelDataManager = channelDataManager;
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_UPDATE_CHANNELS) {
                mChannelDataManager.updateChannels();
            }
        }
    }

    private class ChannelUpdater extends AsyncTask<Void, Void, List<Channel>> {

        @Override
        protected List<Channel> doInBackground(Void... voids) {
            return TvContractUtils.getChannels(mContext.getContentResolver()
                    , TvContract.buildChannelsUriForInput(DVBC_INPUT_ID));
        }

        @Override
        protected void onPostExecute(List<Channel> channels) {
            mContext.sendBroadcast(new Intent(WebDeviceInterface.BROADCAST_CHANNELS_SET));
            mChannelList.clear();
            mChannelList.addAll(channels);
        }
    }

    public void registerObserver() {
        updateChannels();
        mHandler = new ChannelHandler(this);
        mContentObserver = new ContentObserver(mHandler) {
            @Override
            public void onChange(boolean selfChange) {
                if (!mHandler.hasMessages(MSG_UPDATE_CHANNELS)) {
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_CHANNELS, EXECUTE_DELAY);
                }
            }
        };
        mContext.getContentResolver().registerContentObserver(
                TvContract.buildChannelsUriForInput(DVBC_INPUT_ID), true, mContentObserver);
    }

    public void unregisterObserver() {
        mContext.getContentResolver().unregisterContentObserver(mContentObserver);
        mHandler.removeCallbacksAndMessages(null);
        mChannelList.clear();
    }
}