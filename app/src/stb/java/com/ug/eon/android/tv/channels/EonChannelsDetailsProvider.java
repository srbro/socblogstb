package com.ug.eon.android.tv.channels;

import android.util.Log;

import com.ug.eon.android.tv.R;
import com.ug.eon.android.tv.channels.applinks.AppLinkUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nemanja.todoric on 1/19/2018.
 */

public class EonChannelsDetailsProvider {

    private static final String TAG = "EonChannelDetialsProv";

    public enum EonChannelType {
        MAIN,
        FAVORITES
    }

    private static List<EonChannel> eonChannelList;

    public static List<EonChannel> getEonChannelList() {
        if (eonChannelList == null) {
            eonChannelList = createEonChannelList();
        }

        return eonChannelList;
    }

    public static EonChannel getEonMainChannel() {
        List<EonChannel> eonChannels = getEonChannelList();
        for (EonChannel channel : eonChannels) {
            Log.i(TAG, "channel: " + channel.getName());
            if (ChannelStrings.EON_MAIN_CHANNEL.equals(channel.getName()))
                return channel;
        }

        // really should not happen
        Log.w(TAG, "Could not find Eon main channel.");
        return null;
    }

    private static List<EonChannel> createEonChannelList() {
        List<EonChannel> eonChannels = new ArrayList<>();

        EonChannel mainChannel = new EonChannel();
        mainChannel.setChannelId(100);
        mainChannel.setName(ChannelStrings.EON_MAIN_CHANNEL);
        mainChannel.setDescription("Eon Main Stripe");
        mainChannel.setAppLinkIntentUri(AppLinkUtils.URI_CHANNEL);
        mainChannel.setChannelLogo(R.drawable.eon_tv_icon);

        eonChannels.add(mainChannel);

        return eonChannels;
    }
}
