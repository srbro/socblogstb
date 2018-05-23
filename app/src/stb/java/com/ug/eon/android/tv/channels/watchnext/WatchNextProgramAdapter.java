package com.ug.eon.android.tv.channels.watchnext;

import android.support.annotation.Nullable;

import com.ug.eon.android.tv.infoserver.entities.Image;
import com.ug.eon.android.tv.infoserver.entities.WatchNextItem;
import com.ug.eon.android.tv.prefs.PreferenceManager;
import com.ug.eon.android.tv.prefs.ServerPrefs;
import com.ug.eon.android.tv.util.Optional;

import java.util.List;

public class WatchNextProgramAdapter {

    private PreferenceManager preferenceManager;

    public WatchNextProgramAdapter(PreferenceManager preferenceManager) {
        this.preferenceManager = preferenceManager;
    }

    @Nullable
    public EonWatchNextProgram createFromWatchNextItem(WatchNextItem watchNextItem) {
        WatchNextItem.WatchNextItemPayload watchNextItemPayload = watchNextItem.getPayload();

        String imageURI = getImageURI(watchNextItemPayload.getImages());
        if (imageURI == null) // we don't want to put anything in Watch Next that has no image
            return null;

        String channelLogo = getImageURI(watchNextItemPayload.getChannelLogos());

        EonWatchNextProgram watchNextProgram = new EonWatchNextProgram();
        watchNextProgram.setType(watchNextItem.getType());
        watchNextProgram.setId(watchNextItemPayload.getId());
        watchNextProgram.setChannelId(watchNextItemPayload.getChannelId());
        watchNextProgram.setTitle(watchNextItemPayload.getTitle());
        watchNextProgram.setShortDescription(watchNextItemPayload.getShortDescription());
        watchNextProgram.setStartTime(watchNextItemPayload.getStartTime());
        watchNextProgram.setEndTime(watchNextItemPayload.getEndTime());
        watchNextProgram.setImageURI(imageURI);
        watchNextProgram.setChannelLogoURI(channelLogo);

        return watchNextProgram;
    }

    private String getImageURI(List<Image> images) {
        String imagePath = null;
        for (Image image : images) {
            if (image.getSize().equals("XL")) {
                imagePath = image.getPath();
            }
        }

        if (imagePath == null)
            return null;

        final String imageURI = imagePath;
        return preferenceManager.getServerPrefs().map(prefs -> {
            if(prefs.getImageServerUrl() == null || prefs.getImageServerUrl().isEmpty())
                return "";
            return prefs.getImageServerUrl() + imageURI;
        }).orElse("");
    }
}
