package com.ug.eon.android.tv.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

/**
 * Generates unique device id and stores id into private share preferences.
 */
public class InstanceUniqueID {
	private static final String TAG = InstanceUniqueID.class.getName();
	private static final String PREFERENCE_STORAGE = "com.ug.eon.preferences";
	private static final String KEY_UUID = "uuid.key";
	private static final String INSTALLATION = "INSTALLATION";
	private Context mContext;
	private String mUUID;

	public InstanceUniqueID(Context context) {
		super();
		this.mContext = context.getApplicationContext();
	}

	/**
	 * Gets unique uuid.
	 * @return UUID.
	 */
	public String getId() {
		if (mUUID == null) {
			mUUID = readUUID();
		}
		Log.d(TAG, "getId: " + mUUID);
		return mUUID;
	}

	private String readUUID() {
		String uuid = getUUIDValue();
		if (uuid != null) {
			return uuid.replace("-", "");
		}

		File installation = new File(mContext.getFilesDir(), INSTALLATION);
		if (installation.exists()) {
			// if file exists try to use it to get serial
			try {
				uuid = readInstallationFile(installation);
				Log.d(TAG, "[uuid migration] read from file: " + uuid);
				// Save into share preferences.
				setUUIDValue(uuid);
			} catch (IOException e) {
				uuid = null;
			}
		}

		//if it doesn't exist, generate new uuid and save it.
		if (uuid == null) {
			uuid = UUID.randomUUID().toString();
			Log.d(TAG, "generated new uuid: " + uuid);
			setUUIDValue(uuid);
		}

		return uuid.replace("-", "");
	}

	/**
	 * Reads from installation file. This method is deprecated.
	 * Use {@link #getUUIDValue()} method instead.
	 */
	@Deprecated
	private String readInstallationFile(File installation) throws IOException {
		RandomAccessFile f = new RandomAccessFile(installation, "r");
		byte[] bytes = new byte[(int) f.length()];
		f.readFully(bytes);
		f.close();
		return new String(bytes);
	}

	/**
	 * Gets uuid value from private share preferences.
	 */
	private String getUUIDValue() {
		SharedPreferences preferences = mContext.getSharedPreferences(PREFERENCE_STORAGE,
				Context.MODE_PRIVATE);
		return preferences.getString(KEY_UUID, null);
	}

	/**
	 * Sets uuid value into private shared preferences storage.
	 */
	private void setUUIDValue(String uuid) {
		SharedPreferences preferences = mContext.getSharedPreferences(PREFERENCE_STORAGE,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(KEY_UUID, uuid);
		editor.apply();
	}
}