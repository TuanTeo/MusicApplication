package com.bkav.musicapplication.Song;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Song {
    static final Song EMPTY_SONG = new Song("", -1, -1, -1, null, "", -1, "", "");

    public final String mTitle;
    public final int mTrackNumber;
    public final int mDuration;
    public final String mPath;
    public final String mAlbumName;
    public final int mArtistId;
    public final String mArtistName;
    public final int mYear;
    public final String mAlbumID;


    public Song(String title, int trackNumber, int mYear, int mDuration, String mPath, String mAlbumName, int mArtistId, String mArtistName, String mAlbumID) {
        this.mTitle = title;
        this.mTrackNumber = trackNumber;
        this.mYear = mYear;
        this.mDuration = mDuration;
        this.mPath = mPath;
        this.mAlbumName = mAlbumName;
        this.mArtistId = mArtistId;
        this.mArtistName = mArtistName;
        this.mAlbumID = mAlbumID;
    }

    /**
     * Convert Duration to String fommat hh:mm:ss
     */
    public String getmDurationString(){
        int totalTime = mDuration;
        int minutes = 0;
        int seconds = 0;
        String durationString;
        NumberFormat formatter;

        if((totalTime / 1000 / 60) > 0){ //Nhieu hon 1 minutes
            minutes = totalTime / 1000 / 60;
            totalTime -= minutes * (1000 * 60);
        }
        if(totalTime / 1000 > 0){ //Nhieu hon 1s
            seconds = totalTime / 1000;
        }

        //Format time to mm:ss
        formatter = new DecimalFormat("##00");
        durationString = formatter.format(minutes) + ":";
        durationString += formatter.format(seconds);

        return durationString;
    }

    /**
     * Getter & Setter
     */
    public String getmTitle() {
        return mTitle;
    }

    public int getmTrackNumber() {
        return mTrackNumber;
    }

    public int getmDuration() {
        return mDuration;
    }

    public String getmPath() {
        return mPath;
    }

    public String getmAlbumName() {
        return mAlbumName;
    }

    public int getmArtistId() {
        return mArtistId;
    }

    public String getmArtistName() {
        return mArtistName;
    }

    public int getmYear() {
        return mYear;
    }

    public String getmAlbumID() {
        return mAlbumID;
    }

    public static Uri queryAlbumUri(String imgUri) {   //dung album de load anh
        final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(artworkUri, Long.parseLong(imgUri));  //noi them imgUri vao artworkUri
    }

    public static byte[] getAlbumArt(String uri) {  // dung file de load anh
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(uri);
        byte[] albumArt = mediaMetadataRetriever.getEmbeddedPicture();  // chuyển đổi đường dẫn file media thành đường dẫn file Ảnh
        mediaMetadataRetriever.release();
        return albumArt;
    }
}
