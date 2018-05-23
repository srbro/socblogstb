package com.ug.eon.android.tv.channels.watchnext;

import android.content.Context;

import com.ug.eon.android.tv.infoserver.InfoServerClient;
import com.ug.eon.android.tv.infoserver.InfoServerClientImpl;
import com.ug.eon.android.tv.infoserver.entities.WatchNextItem;
import com.ug.eon.android.tv.prefs.PreferenceManager;
import com.ug.eon.android.tv.prefs.PreferenceManagerImpl;
import com.ug.eon.android.tv.prefs.SharedPrefsProvider;
import com.ug.eon.android.tv.prefs.SharedPrefsProviderImpl;

import java.util.List;

public class WatchNextProgramProvider {

    public static List<WatchNextItem> getWatchNextPrograms(Context context) {
        SharedPrefsProvider sharedPrefsProvider = new SharedPrefsProviderImpl(context);
        PreferenceManager pm = new PreferenceManagerImpl(sharedPrefsProvider);
        InfoServerClient isClient = new InfoServerClientImpl(pm);

        return isClient.getWatchNextItems();
    }
}
