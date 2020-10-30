package com.bkav.musicapplication.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bkav.musicapplication.favoritesongdatabase.FavoriteSongDataBase;

public class FavoriteSongProvider extends ContentProvider {

    //Thẩm quyền
    private static final String AUTHORITY = "com.bkav.musicapplication.data.FavoriteSong";

    /* ??? ( chac la cai muon lay ra ) */
    public static final int TUTORIALS = 100;
    public static final int TUTORIAL_ID = 110;


    private static final String FAVORITE_SONG_BASE_PATH = "song_data";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + FAVORITE_SONG_BASE_PATH);

    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/mt-tutorial";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/mt-tutorial";

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, FAVORITE_SONG_BASE_PATH, TUTORIALS);
        sUriMatcher.addURI(AUTHORITY, FAVORITE_SONG_BASE_PATH + "/#", TUTORIAL_ID);
    }
    private FavoriteSongDataBase mFavoriteSongDB;

    @Override
    public boolean onCreate() {
        this.mFavoriteSongDB = new FavoriteSongDataBase(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }
}
