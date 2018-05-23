package com.ug.eon.android.tv.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.CursorObjectAdapter;

import com.ug.eon.android.tv.prefs.AuthPrefs;
import com.ug.eon.android.tv.prefs.PreferenceManager;
import com.ug.eon.android.tv.prefs.PreferenceManagerImpl;
import com.ug.eon.android.tv.prefs.SharedPrefsProviderImpl;
import com.ug.eon.android.tv.util.Optional;


/**
 * Created by petar.stefanovic on 19/03/2018.
 */

public class CommunityIdProvider extends ContentProvider {

    private static final String TAG = "CommunityIdProvider";
    private static final String AUTHORITY = "com.ug.eon.android.tv.contentprovider.CommunityIdProvider";

    private static final String DATA = "communityid";

    private static final int MATCH= 1;

    static final UriMatcher sUriMatcher;

    static{
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, DATA, MATCH);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (sUriMatcher.match(uri)) {
            case MATCH:
                Context ctx = getContext();
                PreferenceManager pm = new PreferenceManagerImpl(new SharedPrefsProviderImpl(ctx));

                MatrixCursor cursor = new MatrixCursor(new String[]{"commId"});

                pm.getAuthPrefs().ifPresent((AuthPrefs prefs) -> {
                    int commId = prefs.getComm_id();
                    cursor.addRow(new Object[]{commId});
                });

                return cursor;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException();
    }
}

