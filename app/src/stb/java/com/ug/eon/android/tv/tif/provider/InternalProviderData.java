package com.ug.eon.android.tv.tif.provider;

/**
 * Created by milan.adamovic on 3/5/18.
 */
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * This is a serialized class used for storing and retrieving serialized data from
 * {@link android.media.tv.TvContract.Channels#COLUMN_INTERNAL_PROVIDER_DATA},
 * {@link android.media.tv.TvContract.Programs#COLUMN_INTERNAL_PROVIDER_DATA}, and
 * {@link android.media.tv.TvContract.RecordedPrograms#COLUMN_INTERNAL_PROVIDER_DATA}.
 *
 * In addition to developers being able to add custom attributes to this data type, there are
 * pre-defined values.
 * /Users/milan.adamovic/Library/Android/sdk
 */
public class InternalProviderData {
    private static final String TAG = "InternalProviderData";
    private static final boolean DEBUG = true;

    private static final String KEY_ROOT_ID = "rootid";
    private static final String KEY_SERVICE_TYPE = "service_type";
    private static final String KEY_SERVICE_LANG = "service_lang";
    private static final String KEY_PROG_NUMBER = "prog_number";
    private static final String KEY_FREQUENCY = "frequency";
    private static final String KEY_MODULATION = "modulation";
    private static final String KEY_NVIDEO = "nvideo";
    private static final String KEY_NAUDIO = "naudio";
    private static final String KEY_NSUBTITLE = "nsubtitle";
    private static final String KEY_NTTX = "nttx";
    private static final String KEY_PLAYER_MODE = "player_mode";

    private JSONObject mJsonObject;

    public InternalProviderData(int root_id, int service_type, @NonNull String service_lang, int prog_number,
                                int frequency, @NonNull String modulation, int nvideo, int naudio,
                                int nsubtitle, int nttx, @NonNull String player_mode) {
        mJsonObject = new JSONObject();
        try {
            mJsonObject.put(KEY_ROOT_ID, root_id);
            mJsonObject.put(KEY_SERVICE_TYPE, service_type);
            mJsonObject.put(KEY_SERVICE_LANG, service_lang);
            mJsonObject.put(KEY_PROG_NUMBER, prog_number);
            mJsonObject.put(KEY_FREQUENCY, frequency);
            mJsonObject.put(KEY_MODULATION, modulation);
            mJsonObject.put(KEY_NVIDEO, nvideo);
            mJsonObject.put(KEY_NAUDIO, naudio);
            mJsonObject.put(KEY_NSUBTITLE, nsubtitle);
            mJsonObject.put(KEY_NTTX, nttx);
            mJsonObject.put(KEY_PLAYER_MODE, player_mode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new object and attempts to populate from the provided String
     *
     * @param data Correctly formatted InternalProviderData
     * @throws ParseException If data is not formatted correctly
     */
    public InternalProviderData(@NonNull String data) throws ParseException {
        try {
            mJsonObject = new JSONObject(data);
        } catch (JSONException e) {
            throw new ParseException(e.getMessage());
        }
    }

    /**
     * Creates a new object and attempts to populate by obtaining the String representation of the
     * provided byte array
     *
     * @param bytes Byte array corresponding to a correctly formatted String representation of
     * InternalProviderData
     * @throws ParseException If data is not formatted correctly
     */
    public InternalProviderData(@NonNull byte[] bytes) throws ParseException {
        try {
            mJsonObject = new JSONObject(new String(bytes));
        } catch (JSONException e) {
            throw new ParseException(e.getMessage());
        }
    }

    private int jsonHash(JSONObject jsonObject) {
        int hashSum = 0;
        Iterator<String> keys = jsonObject.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            try {
                if (jsonObject.get(key) instanceof JSONObject) {
                    // This is a branch, get hash of this object recursively
                    JSONObject branch = jsonObject.getJSONObject(key);
                    hashSum += jsonHash(branch);
                } else {
                    // If this key does not link to a JSONObject, get hash of leaf
                    hashSum += key.hashCode() + jsonObject.get(key).hashCode();
                }
            } catch (JSONException ignored) {
            }
        }
        return hashSum;
    }

    @Override
    public int hashCode() {
        // Recursively get the hashcode from all internal JSON keys and values
        return jsonHash(mJsonObject);
    }

    private boolean jsonEquals(JSONObject json1, JSONObject json2) {
        Iterator<String> keys = json1.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            try {
                if (json1.get(key) instanceof JSONObject) {
                    // This is a branch, check equality of this object recursively
                    JSONObject thisBranch = json1.getJSONObject(key);
                    JSONObject otherBranch = json2.getJSONObject(key);
                    return jsonEquals(thisBranch, otherBranch);
                } else {
                    // If this key does not link to a JSONObject, check equality of leaf
                    if (!json1.get(key).equals(json2.get(key))) {
                        // The VALUE of the KEY does not match
                        return false;
                    }
                }
            } catch (JSONException e) {
                return false;
            }
        }
        // Confirm that no key has been missed in the check
        return json1.length() == json2.length();
    }

    /**
     * Tests that the value of each key is equal. Order does not matter.
     *
     * @param obj The object you are comparing to.
     * @return Whether the value of each key between both objects is equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || ! (obj instanceof InternalProviderData)) {
            return false;
        }
        JSONObject otherJsonObject = ((InternalProviderData) obj).mJsonObject;
        return jsonEquals(mJsonObject, otherJsonObject);
    }

    @Override
    public String toString() {
        return mJsonObject.toString();
    }

    public int getChannelFrequency() {
        if(mJsonObject != null && mJsonObject.has(KEY_FREQUENCY)) {
            try {
                return mJsonObject.getInt(KEY_FREQUENCY);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public boolean isHDChannel() {
        return false;
    }

    public boolean isTvChannel() {
        return false;
    }
    /**
     * This exception is thrown when an error occurs in getting or setting data for the
     * InternalProviderData.
     */
    public class ParseException extends JSONException {
        public ParseException(String s) {
            super(s);
        }
    }
}