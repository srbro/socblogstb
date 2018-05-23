package com.ug.eon.android.tv.tif.provider;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.tv.TvContract;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by milan.adamovic on 3/13/18.
 */
public class TvContractUtils {
    private static final String TAG = TvContractUtils.class.getName();
    private static final String DVBC_INPUT_ID = "net.quber.tvinput/.service.QuberTvInputService/HW0";
    private static final String CHANNEL_SELECTION = "original_network_id=? AND transport_stream_id=? AND service_id=?";

    /**
     * Gets channel from TvProvider for provided args.
     *
     * @param resolver ContentResolver.
     * @param networkId Origin Network Id.
     * @param streamId Transport Stream Id.
     * @param serviceId Service Id.
     */
    @Nullable
    public static Channel getChannel(ContentResolver resolver, int networkId, int streamId, int serviceId) {
        Uri uri = TvContract.buildChannelsUriForInput(DVBC_INPUT_ID);
        String[] selectionArgs = new String[]{ String.valueOf(networkId), String.valueOf(streamId)
                , String.valueOf(serviceId) };

        try (Cursor cursor = resolver.query(uri, Channel.SIMPLE_PROJECTION, CHANNEL_SELECTION, selectionArgs
                , null)) {
            if (cursor == null || cursor.getCount() == 0) {
                Log.d(TAG, "cursor == 0 or null");
                return null;
            }
            cursor.moveToFirst();
            return Channel.fromSimpleCursor(cursor);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    public static List<Channel> getChannels(ContentResolver resolver, Uri uri) {
        List<Channel> channels = new ArrayList<>();
        try (Cursor cursor = resolver.query(uri, Channel.PROJECTION, null,
                null, null)) {
            if (cursor == null || cursor.getCount() == 0) {
                Log.d(TAG, "cursor == null");
                return channels;
            }

            while (cursor.moveToNext()) {
                Channel channel = Channel.fromCursor(cursor);
                // Preview channel.
                if(channel != null && channel.getType().equals(TvContract.Channels.TYPE_PREVIEW)) {
                    Log.i(TAG,channel.getDisplayName() + " is preview Channel");
                    continue;
                }
                channels.add(channel);
            }
        } catch (Exception e) {
            Log.w(TAG, "Unable to get channels", e);
        }
        return channels;
    }
}