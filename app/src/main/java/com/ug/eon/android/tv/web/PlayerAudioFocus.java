package com.ug.eon.android.tv.web;

import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;

public class PlayerAudioFocus {
    private static final String TAG = PlayerAudioFocus.class.getName();
    private AudioManager.OnAudioFocusChangeListener mAudioChangeListener;
    private AudioFocusRequest mAudioFocus;
    private AudioManager mAudioManger;

    public PlayerAudioFocus(AudioManager audioManager, AudioManager.OnAudioFocusChangeListener listener) {
        mAudioManger = audioManager;
        mAudioChangeListener = listener;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mAudioFocus = getAudioFocus();
        }
    }

    public boolean requestAudioFocus() {
        Log.d(TAG, "request audio focus");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return mAudioManger.requestAudioFocus(mAudioFocus)
                    == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        } else {
            return mAudioManger.requestAudioFocus(mAudioChangeListener
                    , AudioManager.STREAM_MUSIC
                    , AudioManager.AUDIOFOCUS_GAIN)
                    == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }
    }

    public void abandonAudioFocus() {
        Log.d(TAG, "abandon audio focus");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mAudioManger.abandonAudioFocusRequest(mAudioFocus);
        } else {
            mAudioManger.abandonAudioFocus(mAudioChangeListener);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private AudioFocusRequest getAudioFocus() {
        AudioAttributes audioAtt = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                .build();
        return new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAtt)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(mAudioChangeListener, new Handler())
                .build();
    }
}
