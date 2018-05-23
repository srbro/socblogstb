package com.ug.eon.android.tv.util;

import android.support.annotation.Nullable;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Util class for crypto related stuff like hash functions.
 *
 * Created by milan.adamovic on 3/13/18.
 */
public class CryptoUtils {

    /**
     * Calculates SHA-1 for provided string value.
     *
     * @param value String value for which SHA-1 will be calculated.
     * @return Hex value of sha1.
     */
    @Nullable
    public static String calculateSha1(String value) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
            byte[] textBytes = value.getBytes("UTF-8");
            md.update(textBytes, 0, textBytes.length);
            byte[] sha1hash = md.digest();
            return toHex(sha1hash);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toHex(byte[] data) {
        StringBuilder hexString = new StringBuilder();
        for (byte aData : data) hexString.append(Integer.toHexString(0xFF & aData));
        return hexString.toString();
    }
}