package com.ug.eon.android.tv.drm;

import android.support.annotation.Nullable;

public interface DrmTokenListener {
    void onDrmTokenFetched(@Nullable String token);
}
