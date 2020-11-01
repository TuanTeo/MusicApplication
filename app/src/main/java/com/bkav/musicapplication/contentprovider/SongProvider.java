package com.bkav.musicapplication.contentprovider;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import com.bkav.musicapplication.song.Song;

import java.util.ArrayList;

/**
 * Chua tat ca bai hat
 */
public class SongProvider {

    private static SongProvider instance;
    private final ArrayList<Song> mListSong;

    private SongProvider(Context context){
        mListSong = getSongs(makeSongCursor(context));
    }

    public static synchronized SongProvider getInstance(Context context){
        if(instance == null){
            instance = new SongProvider(context);
        }
        return instance;
    }

    public static synchronized SongProvider getInstanceNotCreate(){
        return instance;
    }

    //Item position
    private static final int TITLE = 0;
    private static final int TRACK = 1;
    private static final int YEAR = 2;
    private static final int DURATION = 3;
    private static final int PATH = 4;
    private static final int ALBUM = 5;
    private static final int ARTIST_ID = 6;
    private static final int ARTIST = 7;
    private static final int ALBUM_ID = 8;

    private static final String[] BASE_PROJECTION = new String[]{
            MediaStore.Audio.AudioColumns.TITLE,// 0
            MediaStore.Audio.AudioColumns.TRACK,// 1
            MediaStore.Audio.AudioColumns.YEAR,// 2
            MediaStore.Audio.AudioColumns.DURATION,// 3
            MediaStore.Audio.AudioColumns.DATA,// 4
            MediaStore.Audio.AudioColumns.ALBUM,// 5
            MediaStore.Audio.AudioColumns.ARTIST_ID,// 6
            MediaStore.Audio.AudioColumns.ARTIST,// 7
            MediaStore.Audio.Albums.ALBUM_ID //8
    };


    /**
     * Get all song from Memory
     * @param cursor
     * @return
     */
    public static ArrayList<Song> getSongs(@Nullable final Cursor cursor) {
        ArrayList<Song> songs = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                if (getSongFromCursorImpl(cursor).getmDuration() >= 5000) {
                    songs.add(getSongFromCursorImpl(cursor));
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        return songs;
    }

    /**
     * Read data for each Song
     * @param cursor
     * @return
     */
    private static Song getSongFromCursorImpl(Cursor cursor) {
        final String title = cursor.getString(TITLE);
        final int trackNumber = cursor.getInt(TRACK);
        final int year = cursor.getInt(YEAR);
        final int duration = cursor.getInt(DURATION);
        final String uri = cursor.getString(PATH);
        final String albumName = cursor.getString(ALBUM);
        final int artistId = cursor.getInt(ARTIST_ID);
        final String artistName = cursor.getString(ARTIST);
        final String albumID = cursor.getString(ALBUM_ID);

        return new Song(title, trackNumber, year, duration, uri, albumName, artistId, artistName, albumID);
    }

    /**
     * Create Cursor to read data from Memory
     * @return
     * @param context
     */
    public static Cursor makeSongCursor(Context context) {
            try {
                return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        BASE_PROJECTION, null, null, null);
            } catch (SecurityException e) {
                return null;
            }
    }

    public ArrayList<Song> getmListSong() {
        return mListSong;
    }
}
