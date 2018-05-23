package com.ug.eon.android.tv.tif.parser;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.ug.eon.android.tv.util.CryptoUtils.toHex;

/**
 * Dvb channel list parser. It is separated from {@link DvbChannelParserTask} because of easier testing.
 * Created by milan.adamovic on 3/13/18.
 */
public class DvbChannelParser {
    private AsyncTask mTaskInstance;
    private MessageDigest mMessageDigest;
    private String mTvChannelListJson;
    private String mRadioChannelListJson;

    public DvbChannelParser(String tvChannels, String radioChannels) {
        mTvChannelListJson = tvChannels;
        mRadioChannelListJson = radioChannels;
        try {
            mMessageDigest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public JSONObject parse() {
        JSONObject newDvbJson = new JSONObject();
        try {
            newDvbJson.put("doc-type", "SBB-Channels");
            JSONArray newChannelsJson = new JSONArray();

            // List of tv channels.
            if (mTvChannelListJson != null)
                parseChannels(newChannelsJson, new JSONArray(mTvChannelListJson), false);

            if (isCancelled())
                return null;

            // List of radio channels.
            if (mRadioChannelListJson != null)
                parseChannels(newChannelsJson, new JSONArray(mRadioChannelListJson), true);

            newDvbJson.put("hash", getDigest());
            newDvbJson.put("channels", newChannelsJson);
            return newDvbJson;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets instance of AsyncTask which executes parse method. This is needed if we
     * want to cancel task at some point.
     */
    public void setAsyncTask(AsyncTask task) {
        mTaskInstance = task;
    }

    private void parseChannels(JSONArray newChannelsJson, JSONArray channels, boolean radioChannel) throws JSONException {
        if (channels == null)
            return;

        int size = channels.length();
        for (int i = 0; i < size; i++) {
            if (isCancelled()) {
                return;
            }
            JSONObject channel = channels.getJSONObject(i);
            // Create channel object which
            JSONObject newObject = new JSONObject();
            newObject.put("name", channel.getString("name"));
            newObject.put("position", channel.getInt("position"));

            if (!channel.isNull("dvbInfo")) {
                JSONObject dvbInfo = channel.getJSONObject("dvbInfo");
                if (!dvbInfo.isNull("originalNetworkId")) {
                    newObject.put("originalNetworkId", dvbInfo.getInt("originalNetworkId"));
                } else {
                    newObject.put("originalNetworkId", 0);
                }

                if (!dvbInfo.isNull("serviceId")) {
                    newObject.put("serviceId", dvbInfo.getInt("serviceId"));
                } else {
                    newObject.put("originalNetworkId", 0);
                }

                if (!dvbInfo.isNull("transportStreamId")) {
                    newObject.put("transportStreamId", dvbInfo.getInt("transportStreamId"));
                } else {
                    newObject.put("originalNetworkId", 0);
                }

                if (!dvbInfo.isNull("casProtected")) {
                    newObject.put("casProtected" , dvbInfo.getBoolean("casProtected"));
                }

                if (!dvbInfo.isNull("modulation")) {
                    newObject.put("modulation", dvbInfo.getString("modulation"));
                } else {
                    newObject.put("modulation", "256-QAM");
                }

                if (!dvbInfo.isNull("frequency")) {
                    newObject.put("frequency", dvbInfo.getInt("frequency"));
                } else {
                    newObject.put("frequency", 640000);
                }

                if (!dvbInfo.isNull("symbolRate")) {
                    newObject.put("symbolRate", dvbInfo.getInt("symbolRate"));
                } else {
                    newObject.put("symbolRate", 0);
                }
            }
            newObject.put("radioChannel", radioChannel);
            updateDigest(newObject.toString());
            newChannelsJson.put(newObject);
        }
    }

    private boolean isCancelled() {
        return mTaskInstance != null && mTaskInstance.isCancelled();
    }

    private void updateDigest(String value) {
        if (mMessageDigest == null)
            return;
        try {
            byte[] bytesValue = value.getBytes(StandardCharsets.UTF_8.name());
            mMessageDigest.update(bytesValue);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private String getDigest() {
        return toHex(mMessageDigest.digest());
    }
}