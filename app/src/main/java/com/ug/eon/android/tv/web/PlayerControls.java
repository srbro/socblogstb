package com.ug.eon.android.tv.web;

import android.util.Log;
import android.webkit.JavascriptInterface;

public class PlayerControls {
    private static final String TAG = PlayerControls.class.getName();
    private static final String STREAM_VOD = "stream_vod";
    private static final String STREAM_DVB = "stream_dvb";
    private PlayerInterface mUcLivePlayer;
    private PlayerInterface mUcPlayer;
    private PlayerMode mPlayerMode;

    /**
     * Plays OTT or DVB-C stream. This method is called from javascript.
     *
     * @param url Stream url for OTT.
     * @param stream Stream type.
     * @param startTime Ott stream start time.
     * @param networkId Original Network Id - only for DVB.
     * @param streamId Transport Stream Id - only for DVB.
     * @param serviceId Service Id - only for DVB.
     * @param drmProtected Stream drm protected.
     */
    @JavascriptInterface
    public void playStream(String url, String stream, double startTime, int networkId
            , int streamId, int serviceId, boolean drmProtected) {
        switch (stream) {
            case STREAM_DVB:
                Log.d(TAG, "dvb, network id: " + networkId + ", stream id: "
                        + streamId + ", service id: " + serviceId);

                if (mUcPlayer != null && PlayerMode.isOtt(mPlayerMode))
                    mUcPlayer.stop();

                mPlayerMode = PlayerMode.DVB_C;
                mUcLivePlayer.playDvbVideo(networkId, streamId, serviceId);
                break;
            case STREAM_VOD:
                if (mUcLivePlayer != null && PlayerMode.isDvb(mPlayerMode))
                    mUcLivePlayer.stop();

                mPlayerMode = PlayerMode.OTT;
                mUcPlayer.playVideo(url, startTime, drmProtected);
                break;
            default:
                Log.d(TAG, "play hls/dash, default");

                if (mUcLivePlayer != null && PlayerMode.isDvb(mPlayerMode))
                    mUcLivePlayer.stop();

                mPlayerMode = PlayerMode.OTT;
                mUcPlayer.playVideo(url, drmProtected);
                break;
        }
    }

    @JavascriptInterface
    public void playStream(String url, String stream, double startTime, int networkId
            , int streamId, int serviceId) {
        playStream(url, stream, startTime, networkId, streamId, serviceId, false);
    }

    @JavascriptInterface
    public void seekTo(double ms) {
        mUcPlayer.seekTo(ms);
    }

    @JavascriptInterface
    public void resume() {
        mUcPlayer.resume();
    }

    @JavascriptInterface
    public void pause() {
        if (mUcLivePlayer != null && PlayerMode.isDvb(mPlayerMode)) {
            mUcLivePlayer.playPause(false);
        } else {
            mUcPlayer.playPause(false);
        }
    }

    @JavascriptInterface
    public void play() {
        mUcPlayer.playPause(true);
    }

    @JavascriptInterface
    public void stop() {
        mUcPlayer.stop();
    }

    public void setUcPlayer(PlayerInterface ucPlayer) {
        mUcPlayer = ucPlayer;
    }

    public void setLiveUcPlayer(PlayerInterface ucPlayer) {
        mUcLivePlayer = ucPlayer;
    }
}