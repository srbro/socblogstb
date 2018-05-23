package com.ug.eon.android.tv.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.VideoView;

import com.ug.eon.android.tv.R;

/**
 * Eon animation intro video view component.
 * Created by milan.adamovic on 1/25/18.
 */
public class EonIntroView extends VideoView {

    public EonIntroView(Context context) {
        super(context);
        setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public EonIntroView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Starts EON intro video. This method loads video every time.
     * This method releases {@link android.media.MediaPlayer} when video playback is complete.
     */
    public void play() {
        loadVideo(getContext());
    }

    private void loadVideo(Context context) {
        String path = "android.resource://" + context.getPackageName()
                + "/" + R.raw.eon_logo_animation;
        setVideoPath(path);
        // When video is loaded play the video.
        setOnPreparedListener(mp -> start());
        setOnCompletionListener(mp -> {
            setVisibility(View.GONE);
            stopPlayback();
        });
    }
}