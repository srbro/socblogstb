package com.ug.eon.android.tv.searchintegration;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.ug.eon.android.tv.infoserver.InfoServerClient;
import com.ug.eon.android.tv.infoserver.InfoServerClientImpl;
import com.ug.eon.android.tv.infoserver.entities.Asset;
import com.ug.eon.android.tv.infoserver.entities.AssetType;
import com.ug.eon.android.tv.infoserver.entities.Assets;
import com.ug.eon.android.tv.infoserver.entities.Image;
import com.ug.eon.android.tv.prefs.PreferenceManager;
import com.ug.eon.android.tv.prefs.PreferenceManagerImpl;
import com.ug.eon.android.tv.prefs.ServerPrefs;
import com.ug.eon.android.tv.prefs.SharedPrefsProvider;
import com.ug.eon.android.tv.prefs.SharedPrefsProviderImpl;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by goran.arandjelovic on 2/27/18.
 */

public class EonSearchProvider extends ContentProvider {
    private static final String TAG = EonSearchProvider.class.getName();
    private static final String PACKAGE_NAME = EonSearchProvider.class.getPackage().getName();
    private static InfoServerClient infoServerClient = null;
    private static PreferenceManager preferenceManager = null;

    private static final List<String> searchDatabaseColumns = Arrays.asList(
            "_id",
            "suggest_text_1",
            "suggest_text_2",
            "suggest_result_card_image",
            "suggest_production_year",
            "suggest_duration",
            "suggest_content_type",
            "suggest_intent_data"
    );

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(PACKAGE_NAME, "livetv/*", AssetType.LIVETV.getOrdinal());
        uriMatcher.addURI(PACKAGE_NAME, "cutv/*", AssetType.CUTV.getOrdinal());
        uriMatcher.addURI(PACKAGE_NAME, "vod/*", AssetType.VOD.getOrdinal());
    }

    private static String remapAssetType(AssetType assetType) {
        // This method is used to map asset type appropriately to reflect implementation of
        // JS method 'linkToContent'. It should be removed at some point.
        if(assetType.equals(AssetType.LIVETV) || assetType.equals(AssetType.CUTV)) {
            return "EVENT";
        }
        return "VOD";
    }

    private static String prepareImagePath(AssetType assetType, List<Image> images) {
        String imageType = null;
        if(assetType.equals(AssetType.LIVETV) || assetType.equals(AssetType.CUTV)) {
            imageType = "EVENT_16_9";
        } else if(assetType.equals(AssetType.VOD)) {
            imageType = "VOD_POSTER_21_31";
        }
        for(Image image : images) {
            if(image.getType().equals(imageType)) {
                return preferenceManager.getServerPrefs().map(prefs -> {
                    if(prefs.getImageServerUrl() == null || prefs.getImageServerUrl().isEmpty())
                        return null;
                    return prefs.getImageServerUrl() + image.getPath();
                }).orElse(null);
            }
        }
        return null;
    }

    private void populateCursor(MatrixCursor mc, AssetType searchAsset, List<? extends Asset> assets)  {
        for(int i = 0; i < assets.size(); i++) {
            Asset asset = assets.get(i);

            JSONObject globalSearchData = new JSONObject();
            try {
                globalSearchData.put("type", remapAssetType(asset.getAssetType()));
                globalSearchData.put("id", asset.getId());
                globalSearchData.put("channelId", asset.getChannelId());
            }
            catch (Exception e) {
                continue;
            }
            String imageUrl = prepareImagePath(searchAsset, asset.getImages());
            String intentData = globalSearchData.toString();
            mc.addRow(new Object[]{ i, asset.getTitle(), asset.getShortDescription(), imageUrl, asset.getYear(), asset.getDuration(), "video", intentData });
        }
    }

    @Override
    public boolean onCreate() {
        SharedPrefsProvider sharedPrefsProvider = new SharedPrefsProviderImpl(getContext());
        preferenceManager = new PreferenceManagerImpl(sharedPrefsProvider);
        infoServerClient = new InfoServerClientImpl(preferenceManager);
        return infoServerClient != null && preferenceManager != null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        AssetType searchAsset = AssetType.valueOf(uriMatcher.match(uri));

        if(searchAsset == null) {
            return null;
        }

        MatrixCursor mc = new MatrixCursor(searchDatabaseColumns.toArray(new String[searchDatabaseColumns.size()]));

        Assets assets = infoServerClient.searchAssets(strings1 != null ? strings1[0] : null, searchAsset);

        if(assets == null) {
            return null;
        }
        if(searchAsset.equals(AssetType.LIVETV)) {
            populateCursor(mc, searchAsset, assets.getLiveTv());
        } else if(searchAsset.equals(AssetType.CUTV)) {
            populateCursor(mc, searchAsset, assets.getCuTv());
        } else if(searchAsset.equals(AssetType.VOD)) {
            populateCursor(mc, searchAsset, assets.getVod());
        } else {
            return null;
        }

        return mc;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) { return 0; }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) { return 0; }
}
