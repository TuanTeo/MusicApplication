package com.bkav.musicapplication.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bkav.musicapplication.favoritesongdatabase.FavoriteSongDataBase;
import com.bkav.musicapplication.song.Song;

public class FavoriteSongProvider extends ContentProvider {

    //Authority: Thẩm quyền
    private static final String AUTHORITY = "com.bkav.musicapplication.data.FavoriteSong";
    private static final String FAVORITE_SONG_BASE_PATH = "song_data";
    //Uri of FavoriteSong Database
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + FAVORITE_SONG_BASE_PATH);

    /* ??? ( chac la cai muon lay ra ) */
    public static final int TUTORIALS = 100;
    public static final int TUTORIAL_ID = 110;

//    //Column of Table
//    private static final String COLUMN_TITLE = "Title";
//    private static final String COLUMN_TRACK = "Track";
//    private static final String COLUMN_YEAR = "Year";
//    private static final String COLUMN_DURATION = "Duration";
//    private static final String COLUMN_PATH = "Path";
//    private static final String COLUMN_ALBUM = "Album";
//    private static final String COLUMN_ARTIST_ID = "Artist_ID";
//    private static final String COLUMN_ARTIST = "Artist";
//    private static final String COLUMN_ALBUM_ID = "Album_ID";

    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/mt-tutorial";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/mt-tutorial";

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, FAVORITE_SONG_BASE_PATH, TUTORIALS);
        sUriMatcher.addURI(AUTHORITY, FAVORITE_SONG_BASE_PATH + "/#", TUTORIAL_ID);
    }

    private SQLiteDatabase mObjWriteDB;
    private FavoriteSongDataBase mFavoriteSongDB;

    @Override
    public boolean onCreate() {
        this.mFavoriteSongDB = new FavoriteSongDataBase(getContext());
        mObjWriteDB = mFavoriteSongDB.getWritableDatabase();
        return true;
    }

    public FavoriteSongDataBase getmFavoriteSongDB(){
        return this.mFavoriteSongDB;
    }

    /**
     * Truy van du lieu trong DataBase
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(FavoriteSongDataBase.TABLE_SONG);
        int uriType = sUriMatcher.match(uri);
//        switch (uriType){
//            case TUTORIALS:
//                queryBuilder.appendWhere(FavoriteSongDataBase.COLUMN_PATH + "=" +
//                        uri.getPathSegments().get(0));
//                break;
//            default:
//                throw new IllegalArgumentException("Unknown URI");
//        }
        Cursor cursor = queryBuilder.query(mFavoriteSongDB.getReadableDatabase(),projection
                , selection, selectionArgs,null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * Add data to DataBase
     * @param uri
     * @param values
     * @return
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        //Kiểm tra xem uri cua Song đã có trong Database chưa
        Cursor cursor = query(FavoriteSongProvider.CONTENT_URI,null,
                "Path = ?", new String[]{values.getAsString(FavoriteSongDataBase.COLUMN_PATH)},
                null);
        if(cursor.moveToFirst()){
            return null;
        } else {
            long rowID = mObjWriteDB.insert(FavoriteSongDataBase.TABLE_SONG, null, values);
            if (rowID > 0) {
                Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
                getContext().getContentResolver().notifyChange(_uri, null);
                return _uri;
            }
            values.get(FavoriteSongDataBase.COLUMN_ALBUM_ID);
            throw new SQLException("Fail to add a record into " + uri);
        }
    }

    /**
     * Xoa du lieu tu DataBase
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase sqlDB = mFavoriteSongDB.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case TUTORIALS:
                rowsDeleted = sqlDB.delete(FavoriteSongDataBase.TABLE_SONG, selection,
                        selectionArgs);
                break;
            case TUTORIAL_ID:
                String id = uri.getPathSegments().get(0);
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(FavoriteSongDataBase.TABLE_SONG,
                            mFavoriteSongDB.COLUMN_PATH+ "like" + id,null);
                } else {
                    rowsDeleted = sqlDB.delete(FavoriteSongDataBase.TABLE_SONG,
                            mFavoriteSongDB.COLUMN_PATH + "like" + id + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: Lam gi co " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    /**
     * Thay doi du lieu cua cac hang da co trong DataBase
     * @param uri           //Co so du lieu duoc truy van
     * @param values
     * @param selection     //Cau lenh "Where"
     * @param selectionArgs //Tham so bo sung cho thao tac truy van
     * @return
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase sqlDB = mFavoriteSongDB.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType){
            case TUTORIALS:
                rowsUpdated = sqlDB.update(FavoriteSongDataBase.TABLE_SONG,
                        values, selection, selectionArgs);
                break;
            case TUTORIAL_ID:
                String id = uri.getLastPathSegment();
                if(TextUtils.isEmpty(selection)){
//                    rowsUpdated = sqlDB.update(FavoriteSongDataBase.TABLE_SONG, values,
//                            FavoriteSongDataBase.COLUMN_PATH + "=" +id + "and" + selection,
//                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
