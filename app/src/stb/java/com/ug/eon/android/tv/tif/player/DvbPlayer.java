package com.ug.eon.android.tv.tif.player;

import android.app.Activity;
import android.media.tv.TvContract;
import android.media.tv.TvView;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ug.eon.android.tv.tif.provider.Channel;
import com.ug.eon.android.tv.tif.provider.ChannelDataManager;
import com.ug.eon.android.tv.util.EventListener;
import com.ug.eon.android.tv.web.PlayerInterface;

/**
 * Dvb player based on TV Input Framework.
 * Created by milan.adamovic on 3/5/18.
 */
public class DvbPlayer extends TvView.TvInputCallback implements PlayerInterface {
    private static final String TAG = DvbPlayer.class.getName();
    private EventListener mEventListener;
    private TvView mTvView;
    private Activity mActivity;
    private boolean mStarted;

    private ChannelDataManager mChannelDataManager;
    private Channel mCurrentChannel;

    public DvbPlayer(Activity activity, TvView tvView, EventListener eventListener) {
        mActivity = activity;
        mTvView = tvView;
        mEventListener = eventListener;
        mChannelDataManager = new ChannelDataManager(mActivity);
        startPlayer();
    }

    @Override
    public void startPlayer() {
        mTvView.setCallback(this);
        mChannelDataManager.registerObserver();
    }

    @Override
    public void playDvbVideo(int networkId, int streamId, int serviceId) {
        mActivity.runOnUiThread(() -> {
            if (shouldResumeChannel(networkId, streamId, serviceId)) {
                Log.d(TAG, "resume playback");
                tune(mCurrentChannel);
                return;
            }
            long startTime = System.currentTimeMillis();
            Channel channel = getChannel(networkId, streamId, serviceId);
            Log.d(TAG, "channel fetched in " + (System.currentTimeMillis() - startTime) + "ms");
            if (channel != null)
                tune(channel);
        });
    }

    @Override
    public void stop() {
        mActivity.runOnUiThread(() -> {
            if (!mStarted)
                return;
            mStarted = false;
            mTvView.reset();
        });
    }

    private void tune(@NonNull Channel channel) {
        Log.d(TAG, "channel id: " + channel.getId() + ", inputId: " + channel.getInputId());
        mStarted = true;
        mTvView.tune(channel.getInputId(), TvContract.buildChannelUri(channel.getId()));
        mCurrentChannel = channel;
    }

    private Channel getChannel(int networkId, int streamId, int serviceId) {
        return mChannelDataManager.getChannel(networkId, streamId, serviceId);
    }

    private boolean shouldResumeChannel(int networkId, int streamId, int serviceId) {
        return mCurrentChannel != null && mCurrentChannel.getOriginalNetworkId() == networkId
                && mCurrentChannel.getTransportStreamId() == streamId
                && mCurrentChannel.getServiceId() == serviceId;
    }

    @Override
    public void destroy() {
        mTvView.reset();
        mTvView = null;
        mChannelDataManager.unregisterObserver();
    }

    @Override
    public void onConnectionFailed(String inputId) {
        Log.d(TAG, "connection failed");
        mEventListener.sendEvent(EventListener.EVENT_DVB_ERROR);
    }

    @Override
    public void onDisconnected(String inputId) {
        Log.d(TAG, "disconnected");
        mEventListener.sendEvent(EventListener.EVENT_DVB_ERROR);
    }

    @Override
    public void onChannelRetuned(String inputId, Uri channelUri) {
        mEventListener.sendEvent(EventListener.EVENT_PLAYED);
    }

    @Override
    public void onVideoAvailable(String inputId) {
        Log.d(TAG, "video available");
        mEventListener.sendEvent(EventListener.EVENT_PLAYED);
    }

    @Override
    public void onVideoUnavailable(String inputId, int reason) {
        Log.d(TAG, "video unavailable");
        mEventListener.sendEvent(EventListener.EVENT_DVB_ERROR);
    }

    @Override
    public void resume() {
        // do nothing.
    }

    @Override
    public void playPause(boolean playPause) {
        if (!playPause) {
            stop();
        }
    }

    @Override
    public void playVideo(String url, Double ms, boolean drm) {
        throw new UnsupportedOperationException("Dvb player does not implement this method.");
    }

    @Override
    public void playVideo(String data, boolean drm) {
        throw new UnsupportedOperationException("Dvb player does not implement this method.");
    }

    @Override
    public void seekTo(double ms) {
        throw new UnsupportedOperationException("Dvb player does not implement this method.");
    }
}