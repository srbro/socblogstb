package com.ug.eon.android.tv.channels;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.tv.TvContract;
import android.net.Uri;
import android.support.media.tv.Channel;
import android.support.media.tv.ChannelLogoUtils;
import android.support.media.tv.PreviewProgram;
import android.support.media.tv.TvContractCompat;
import android.support.media.tv.WatchNextProgram;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ug.eon.android.tv.channels.watchnext.EonWatchNextProgram;
import com.ug.eon.android.tv.channels.watchnext.WatchNextProgramAdapter;
import com.ug.eon.android.tv.infoserver.entities.WatchNextItem;
import com.ug.eon.android.tv.prefs.PreferenceManager;
import com.ug.eon.android.tv.util.BitmapUtils;
import com.ug.eon.android.tv.util.Optional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by nemanja.todoric on 1/19/2018.
 */

public class ChannelUtils {

    private static final String TAG = ChannelUtils.class.getName();

    private static final String EON_INPUT_ID = "com.ug.eon.android.tv.channel.main";

    private static final String[] CHANNELS_PROJECTION = {
            TvContractCompat.Channels._ID,
            TvContractCompat.Channels.COLUMN_INPUT_ID,
            TvContract.Channels.COLUMN_DISPLAY_NAME,
            TvContractCompat.Channels.COLUMN_BROWSABLE
    };

    public static int getNumberOfEonChannels(Context context) {
        Cursor cursor =
                context.getContentResolver()
                        .query(
                                TvContractCompat.Channels.CONTENT_URI,
                                CHANNELS_PROJECTION,
                                null,
                                null,
                                null);

        if (cursor == null)
            return 0;

        int result = cursor.getCount();
        cursor.close();

        return result;
    }

    public static List<Channel> getEonChannels(Context context) {
        Cursor cursor =
                context.getContentResolver()
                        .query(
                                TvContractCompat.Channels.CONTENT_URI,
                                CHANNELS_PROJECTION,
                                null,
                                null,
                                null);

        List<Channel> eonChannels = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Channel channel = Channel.fromCursor(cursor);
                if (EON_INPUT_ID.equals(channel.getInputId()))
                    eonChannels.add(channel);
            }
            while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();

        return eonChannels;
    }

    public static boolean channelExists(Context context, String channel) {
        List<Channel> channels = getEonChannels(context);
        for (Channel eonChannel : channels)
            if (eonChannel.getDisplayName().equals(channel))
                return true;

        return false;
    }

    public static Channel getEonMainChannel(Context context) {
        List<Channel> eonChannels = getEonChannels(context);
        for (Channel channel : eonChannels)
            if (ChannelStrings.EON_MAIN_CHANNEL.equals(channel.getDisplayName()))
                return channel;

        return null;
    }

    public static boolean mainEonChannelExists(Context context) {
        return channelExists(context, ChannelStrings.EON_MAIN_CHANNEL);
    }

    public static long createEonMainChannel(Context context) {
        Channel eonMainChannel = getEonMainChannel(context);
        if (eonMainChannel != null)
            return eonMainChannel.getId();

        // Create the channel since it has not been added to the TV Provider.
        EonChannel mainChannel = EonChannelsDetailsProvider.getEonMainChannel();
        Uri appLinkIntentUri = Uri.parse(mainChannel.getAppLinkIntentUri());

        Channel.Builder builder = new Channel.Builder();
        builder.setType(TvContractCompat.Channels.TYPE_PREVIEW)
                .setDisplayName(mainChannel.getName())
                .setDescription(mainChannel.getDescription())
                .setAppLinkIntentUri(appLinkIntentUri)
                .setInputId(EON_INPUT_ID);

        Log.d(TAG, "Creating channel: " + mainChannel.getName());
        Uri channelUrl =
                context.getContentResolver()
                        .insert(
                                TvContractCompat.Channels.CONTENT_URI,
                                builder.build().toContentValues());

        Log.d(TAG, "channel insert at " + channelUrl);
        long channelId = ContentUris.parseId(channelUrl);
        Log.d(TAG, "channel id " + channelId);

        Bitmap bitmap = BitmapUtils.convertToBitmap(context, mainChannel.getChannelLogo());
        ChannelLogoUtils.storeChannelLogo(context, channelId, bitmap);

        return channelId;
    }

    public static List<EonProgram> addEonMainChannelPrograms(Context context, long mainEonChannelId) {
        Log.i("EON_ADD_PROGRAMS", "addEonMainChannelPrograms called");
        List<EonProgram> mainChannelPrograms = null;
        Cursor cursor =
                    context.getContentResolver()
                            .query(
                                    TvContractCompat.buildChannelUri(mainEonChannelId),
                                    null,
                                    null,
                                    null,
                                    null);

            if (cursor != null && cursor.moveToNext()) {
                Channel channel = Channel.fromCursor(cursor);
                if (!channel.isBrowsable()) {
                    Log.d(TAG, "Channel is not browsable: " + mainEonChannelId);
                    //TODO: delete programs
                } else {
                    Log.d(TAG, "Channel is browsable: " + mainEonChannelId);
                    mainChannelPrograms = EonProgramDetailsProvider.getProgramsForMainChannel();
                    createPrograms(context, mainEonChannelId, mainChannelPrograms);
                    // TODO: also update programs
                }
            }

        if (cursor != null)
            cursor.close();

        return mainChannelPrograms;
    }

    public static void syncWatchNextChannel(Context context, List<WatchNextItem> watchNextItems, PreferenceManager preferenceManager) {
        Log.i(TAG, "syncing watch next channels");
        if (watchNextItems != null && preferenceManager != null) {
            deleteCurrentWatchNextItems(context, preferenceManager);
            addWatchNextPrograms(context, watchNextItems, preferenceManager);
        }
    }

    private static void deleteCurrentWatchNextItems(Context context, PreferenceManager preferenceManager) {
        List<EonWatchNextProgram> currentWatchNextItems = getCurrentWatchNextPrograms(preferenceManager);
        if (currentWatchNextItems == null ) {
            Log.d(TAG, "No program to remove from watch next.");
            return;
        }

        for (EonWatchNextProgram eonWatchNextProgram : currentWatchNextItems) {
            removeFromWatchNext(context, eonWatchNextProgram);
        }

        preferenceManager.remove("watchNext");
        Log.i(TAG, "removed watch next items");
    }

    private static void removeFromWatchNext(Context context, EonWatchNextProgram eonWatchNextProgram) {
        if (eonWatchNextProgram.getWatchNextId() < 0) { // not actually in Watch Next database
            Log.d(TAG, "Program has watch next id: " +
                    eonWatchNextProgram.getWatchNextId() + ", and cannot be removed from Watch Next stripe");
            return;
        }

        int rows =
                context.getContentResolver()
                        .delete(
                                TvContractCompat.buildWatchNextProgramUri(eonWatchNextProgram.getWatchNextId()),
                                null,
                                null);
        Log.d(TAG, String.format("Deleted %d programs(s) from watch next", rows));

        eonWatchNextProgram.setWatchNextId(-1);
    }

    public static List<EonWatchNextProgram> getCurrentWatchNextPrograms(PreferenceManager preferenceManager) {
        Optional<String> rawWatchNextPrograms = preferenceManager.getValue("watchNext");

        return rawWatchNextPrograms.map(rawWatchNext -> {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            EonWatchNextProgram[] result = gson.fromJson(rawWatchNext, EonWatchNextProgram[].class);
            return Arrays.asList(result);
        }).orElse(null);
    }

    private static void addWatchNextPrograms(Context context, List<WatchNextItem> items, PreferenceManager preferenceManager) {
        WatchNextProgramAdapter watchNextProgramAdapter = new WatchNextProgramAdapter(preferenceManager);

        List<EonWatchNextProgram> addedWatchNextPrograms = new ArrayList<>();
        Collections.reverse(items);
        for (WatchNextItem watchNextItem : items) {
            EonWatchNextProgram eonWatchNextProgram = watchNextProgramAdapter.createFromWatchNextItem(watchNextItem);
            if (eonWatchNextProgram == null) {
                continue;
            }

            WatchNextProgram program = createWatchNextProgram(eonWatchNextProgram);

            // create program.
            Uri watchNextProgramUri =
                    context.getContentResolver()
                            .insert(
                                    TvContractCompat.WatchNextPrograms.CONTENT_URI,
                                    program.toContentValues());
            long watchNextId = ContentUris.parseId(watchNextProgramUri);
            eonWatchNextProgram.setWatchNextId(watchNextId);

            addedWatchNextPrograms.add(eonWatchNextProgram);
        }

        persistWatchNextPrograms(addedWatchNextPrograms, preferenceManager);
        Log.d(TAG, "Added " + addedWatchNextPrograms.size() + " watch next programs");
    }

    private static void persistWatchNextPrograms(List<EonWatchNextProgram> addedWatchNextPrograms, PreferenceManager preferenceManager) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String watchNextPrefs = gson.toJson(addedWatchNextPrograms);
        Log.d(TAG, "persisting watch next: " + watchNextPrefs);
        preferenceManager.setValue("watchNext", watchNextPrefs);
    }

    private static void createPrograms(Context context, long channelId, List<EonProgram> eonPrograms) {
        for (EonProgram eonProgram : eonPrograms) {
            PreviewProgram program = buildProgram(channelId, eonProgram);

            Uri programUri =
                    context.getContentResolver()
                            .insert(
                                    TvContractCompat.PreviewPrograms.CONTENT_URI,
                                    program.toContentValues());
            long programId = ContentUris.parseId(programUri);
            eonProgram.setProgramId(programId);
        }
    }

    private static PreviewProgram buildProgram(long channelId, EonProgram eonProgram) {
        Uri posterArtUri = Uri.parse("android.resource://com.ug.eon.android.tv/" + eonProgram.getCardImageId());

        PreviewProgram.Builder builder = new PreviewProgram.Builder();
        builder.setChannelId(channelId)
                .setType(TvContractCompat.PreviewPrograms.TYPE_CHANNEL)
                .setTitle(eonProgram.getTitle())
                .setDescription(eonProgram.getDescription())
                .setPosterArtUri(posterArtUri)
                .setIntentUri(Uri.parse(eonProgram.getDeepLink()));
        return builder.build();
    }

    private static WatchNextProgram createWatchNextProgram(EonWatchNextProgram eonWatchNextProgram) {
        Uri intentUri = Uri.parse("eon://watchnext/" + eonWatchNextProgram.getId());

        WatchNextProgram.Builder builder = new WatchNextProgram.Builder();
        builder.setType(TvContractCompat.PreviewProgramColumns.TYPE_TV_EPISODE)
                .setWatchNextType(TvContractCompat.WatchNextPrograms.WATCH_NEXT_TYPE_NEW)
                .setLastEngagementTimeUtcMillis(System.currentTimeMillis())
                .setTitle(eonWatchNextProgram.getTitle())
                .setLive(true)
                .setDescription(eonWatchNextProgram.getShortDescription())
                .setPosterArtUri(Uri.parse(eonWatchNextProgram.getImageURI()))
                .setLogoUri(Uri.parse(eonWatchNextProgram.getChannelLogoURI()))
                .setIntentUri(intentUri);
        return builder.build();
    }
}
