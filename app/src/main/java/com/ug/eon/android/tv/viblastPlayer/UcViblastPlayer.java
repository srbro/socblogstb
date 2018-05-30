package com.ug.eon.android.tv.viblastPlayer;

import android.text.TextUtils;
import android.view.SurfaceView;

import com.ug.eon.android.tv.BuildConfig;
import com.ug.eon.android.tv.drm.DrmInfoProvider;
import com.ug.eon.android.tv.prefs.PreferenceManager;
import com.ug.eon.android.tv.util.EventListener;
import com.ug.eon.android.tv.util.LogUC;
import com.ug.eon.android.tv.web.PlayerInterface;
import com.viblast.android.ViblastConfig;
import com.viblast.android.ViblastPlayer;
import com.viblast.android.ViblastQuality;

import java.util.List;

import static com.ug.eon.android.tv.util.EventListener.*;

public class UcViblastPlayer implements PlayerInterface {
	private static final String TAG = UcViblastPlayer.class.getSimpleName();
	// viblast config settings
	private static final String VIBLAST_KEY = "41bf9c71b80860e63d0ba7912e5075e4c8697c7f459eb86c2151" +
			"73c6a660c88907920bd7d614c5e109a9b0dbc5da51278aa0dcc84f651f31cddab1d8abc564f60471ac3bc08eb922";
	private static final float PLAYLIST_STARTING_OFFSET = 0.1f;
	private static final int INITIAL_ABR_INDEX = 999;
	private static final boolean SET_TUNNELING = false;

	private UcViblastDrmListener mViblastDrmListener;
	private DrmInfoProvider mDrmInfoProvider;
	private ViblastPlayer mViblastPlayer;
	private EventListener mEventListener;
	private boolean mVodMode;

	private long mStartZapTime;

	public UcViblastPlayer(SurfaceView viblastView, EventListener listener
			, DrmInfoProvider drmInfoProvider, PreferenceManager preferenceManager) {
		mEventListener = listener;
		mDrmInfoProvider = drmInfoProvider;
		mViblastDrmListener = new UcViblastDrmListener(preferenceManager);
        mViblastPlayer = new ViblastPlayer(viblastView, getConfig(), mViblastDrmListener);
        initPlayer();
	}

	private ViblastConfig getConfig() {
        final ViblastConfig config = new ViblastConfig();
        config.advancedConfig.put("key", VIBLAST_KEY);
        config.advancedConfig.put("playlist-starting-offset", PLAYLIST_STARTING_OFFSET);
        config.advancedConfig.put("initial-abr-index", INITIAL_ABR_INDEX); // Buffer size for continues playback
        config.setTunneledPlaybackEnabled(SET_TUNNELING);
//        config.setCdnStream(url);
//        config.advancedConfig.put("log", "error"); // Only for development
        return config;
    }

	@Override
	public void initPlayer() {
		mViblastPlayer.addListener(new ViblastPlayer.Listener() {
			@Override
			public void onVideoSizeChanged(int width, int height
					, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
				LogUC.d(TAG, "video size changed: " + width + " " + height
						+ " " + unappliedRotationDegrees + " " + pixelWidthHeightRatio);
			}

			@Override
			public void onPlayerError(ViblastPlayer.ViblastPlayerException e) {
				LogUC.d(TAG, "onPlayerError");
				mEventListener.sendEvent(EVENT_ERROR);
			}

			@Override
			public void onPlaybackStateChanged(ViblastPlayer.ViblastPlayerState viblastPlayerState) {
				LogUC.d(TAG, "player state changed: " + viblastPlayerState.toString());
				int state = getState(viblastPlayerState);
				mEventListener.sendEvent(state); // pass event 2,3 - play success, 1 - play error
			}

			@Override
			public void onQualityChanged(int qualityId) {
				LogUC.d(TAG, "onQualityChanged");
                mEventListener.onBitrateChange(qualityId);
			}

			@Override
			public void onAvailableQualities(List<ViblastQuality> list) {
				LogUC.d(TAG, "available changed: " + list.size());
			}

			@Override
			public void onStreamDuration(boolean isVoD, long duration) {
				LogUC.d(TAG, "available changed " + isVoD + "-" + duration);
			}

			@Override
			public void onTransferFailure(String s, int i) {
				LogUC.d(TAG, "On TransferFailure " + s + " " + i);
				if (i >= 400) { // 4xx, 5xx errors
                    mEventListener.sendEvent(EVENT_ERROR);
                }
			}

			@Override
			public void onViblastPlayerReleased() {
				LogUC.d(TAG, "Viblast released");
			}
		});
	}

    @Override
	public void playVideo(String url, boolean drmProtected) {
		mVodMode = false;
		playStream(url, 0, drmProtected);
	}

	@Override
	public void playVideo(String url, Double ms, boolean drmProtected) {
		mVodMode = true;
		playStream(url, ms, drmProtected);
    }

    private void playStream(String url, double ms, boolean drmProtected) {
		if (BuildConfig.DEBUG) {
			LogUC.d(TAG, "url: " + url + ", drm: " + drmProtected + ", is vod: " + mVodMode);
			mStartZapTime = System.currentTimeMillis();
		}

		if (mViblastPlayer != null) {
			if (drmProtected) {
				playDrmStream(url, ms);
			} else {
				mViblastPlayer.playNewStream(url, true, ms, getConfig());
			}
		}
	}

	private void playDrmStream(String url, double ms) {
		mDrmInfoProvider.requestDrmInfo(token -> {
			if (TextUtils.isEmpty(token)) {
				return;
			}
			mViblastDrmListener.setDrmToken(token);
			mViblastPlayer.playNewStream(url, true, ms, getConfig());
		});
	}

    @Override
    public void seekTo(double ms) {
		if (mViblastPlayer != null) {
			mViblastPlayer.seek(ms);
		}
		LogUC.d(TAG, "seekTo " + ms);
    }

    @Override
    public void resume() {
		if (mViblastPlayer != null) {
			mViblastPlayer.resume();
		}
		LogUC.d(TAG, "resume");
    }

    @Override
	public void playPause(final boolean playPause) {
		LogUC.d(TAG, "playPause: " + playPause + ", viblastRunning: " + isRunning());
		if (!isRunning()) {
			stop();
		}

		if (playPause) {
			start();
		} else {
			if (mVodMode) {
				pause();
			} else {
				stop();
			}
		}
	}

	public void pause() {
		LogUC.d(TAG, "pause stream");
		if (mViblastPlayer != null) {
			mViblastPlayer.pause();
		}
	}

	private void start() {
		if (mViblastPlayer != null) {
			mViblastPlayer.start();
		}
	}

    @Override
    public void stop() {
		LogUC.d(TAG, "video stop");
		if (mViblastPlayer != null) {
			mViblastPlayer.stop();
		}
    }

	private int getState(ViblastPlayer.ViblastPlayerState state) {
		if ((state == ViblastPlayer.ViblastPlayerState.PLAYING
				|| state == ViblastPlayer.ViblastPlayerState.BUFFERING)
				&& BuildConfig.DEBUG) {
			LogUC.d(TAG, "Total zapping time: "
					+ (System.currentTimeMillis() - mStartZapTime)
					+ "ms, " + state.name());
		}

		switch (state) {
			case IDLE:
				return EVENT_STOPPED;
			case BUFFERING:
			case PLAYING:
				return EVENT_PLAYED;
			default:
				return EVENT_PLAYED;
		}
	}

	private boolean isRunning() {
		return mViblastPlayer != null && !mViblastPlayer.isPaused();
	}

	@Override
	public void destroy() {
		if (mViblastPlayer != null) {
			mViblastPlayer.stop();
			mViblastPlayer.release();
			mViblastPlayer = null;
		}
	}
}