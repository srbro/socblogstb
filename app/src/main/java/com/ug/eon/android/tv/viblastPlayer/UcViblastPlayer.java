package com.ug.eon.android.tv.viblastPlayer;

import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;

import com.ug.eon.android.tv.drm.DrmInfoProvider;
import com.ug.eon.android.tv.prefs.PreferenceManager;
import com.ug.eon.android.tv.util.EventListener;
import com.ug.eon.android.tv.web.PlayerInterface;
import com.viblast.android.ViblastConfig;
import com.viblast.android.ViblastPlayer;
import com.viblast.android.ViblastQuality;

import java.util.List;

import static com.ug.eon.android.tv.util.EventListener.*;

public class UcViblastPlayer implements PlayerInterface {
	private static final String TAG = UcViblastPlayer.class.getName();
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

	public UcViblastPlayer(SurfaceView viblastView, EventListener listener
			, DrmInfoProvider drmInfoProvider, PreferenceManager preferenceManager) {
		mEventListener = listener;
		mDrmInfoProvider = drmInfoProvider;
		mViblastDrmListener = new UcViblastDrmListener(preferenceManager);
        mViblastPlayer = new ViblastPlayer(viblastView, getConfig(), mViblastDrmListener);
        startPlayer();
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
	public void startPlayer() {
		mViblastPlayer.addListener(new ViblastPlayer.Listener() {
			@Override
			public void onVideoSizeChanged(int width, int height
					, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
				Log.d(TAG, "video size changed: " + width + " " + height
						+ " " + unappliedRotationDegrees + " " + pixelWidthHeightRatio);
			}

			@Override
			public void onPlayerError(ViblastPlayer.ViblastPlayerException e) {
				Log.d(TAG, "onPlayerError");
				mEventListener.sendEvent(EVENT_ERROR);
			}

			@Override
			public void onPlaybackStateChanged(ViblastPlayer.ViblastPlayerState viblastPlayerState) {
				Log.d(TAG, "player state changed: " + viblastPlayerState.toString());
				int state = getState(viblastPlayerState);
				mEventListener.sendEvent(state); // pass event 2,3 - play success, 1 - play error
			}

			@Override
			public void onQualityChanged(int qualityId) {
				Log.d(TAG, "onQualityChanged");
                mEventListener.onBitrateChange(qualityId);
			}

			@Override
			public void onAvailableQualities(List<ViblastQuality> list) {
				Log.d(TAG, "available changed: " + list.size());
			}

			@Override
			public void onStreamDuration(boolean isVoD, long duration) {
				Log.d(TAG, "available changed " + isVoD + "-" + duration);
			}

			@Override
			public void onTransferFailure(String s, int i) {
				Log.d(TAG, "On TransferFailure " + s + " " + i);
				if (i >= 400) { // 4xx, 5xx errors
                    mEventListener.sendEvent(EVENT_ERROR);
                }
			}

			@Override
			public void onViblastPlayerReleased() {
				Log.d(TAG, "Viblast released");
			}
		});
	}

    @Override
	public void playVideo(String url, boolean drmProtected) {
		Log.d(TAG, "url: " + url + ", drm: " + drmProtected);
		mVodMode = false;
		playStream(url, 0, drmProtected);
	}

	@Override
	public void playVideo(String url, Double ms, boolean drmProtected) {
		Log.d(TAG, "mVodMode url: " + url);
		mVodMode = true;
		playStream(url, ms, drmProtected);
    }

    private void playStream(String url, double ms, boolean drmProtected) {
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
		Log.d(TAG, "seekTo " + ms);
    }

    @Override
    public void resume() {
		Log.d(TAG, "resume");
		if (mViblastPlayer != null) {
			mViblastPlayer.resume();
		}
    }

    @Override
	public void playPause(final boolean playPause) {
		Log.d(TAG, "playPause: " + playPause + ", viblastRunning: " + isRunning());
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
		Log.d(TAG, "pause stream");
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
		Log.d(TAG, "video stop");
		if (mViblastPlayer != null) {
			mViblastPlayer.stop();
		}
    }

	private int getState(ViblastPlayer.ViblastPlayerState state) {
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