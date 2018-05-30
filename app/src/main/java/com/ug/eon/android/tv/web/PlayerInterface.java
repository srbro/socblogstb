package com.ug.eon.android.tv.web;

/**
 * Created by talic on 12/3/17.
 */

public interface PlayerInterface {
    void initPlayer();
    void playVideo(String data, boolean drmProtected);

    /**
     * Should be implemented only by players which can play DVB-C live content.
     *
     * @param networkId Original Network Id.
     * @param streamId Transport Stream Id.
     * @param serviceId Service id.
     */
    default void playDvbVideo(int networkId, int streamId, int serviceId) {
        throw new UnsupportedOperationException("Method playDvbVideo not implemented in this player.");
    }

    void playVideo(String url, Double ms, boolean drmProtected);
    void seekTo(double ms);
    void resume();
    void playPause(final boolean playPause);
    void stop();
    void destroy();
}