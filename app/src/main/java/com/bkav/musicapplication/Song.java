package com.bkav.musicapplication;

import android.widget.ImageView;

public class Song {
    private int songURI;
    private int songAlbumImage;
    private String songName;
    private String songSingerName;

    public Song() {
    }

    public Song(int songURI, int songAlbumImage,
                String songName, String songSingerName) {
        this.songURI = songURI;
        this.songAlbumImage = songAlbumImage;
        this.songName = songName;
        this.songSingerName = songSingerName;
    }

    public int getSongURI() {
        return songURI;
    }

    public void setSongURI(int songURI) {
        this.songURI = songURI;
    }

    public int getSongAlbumImage() {
        return songAlbumImage;
    }

    public void setSongAlbumImage(int songAlbumImage) {
        this.songAlbumImage = songAlbumImage;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongSingerName() {
        return songSingerName;
    }

    public void setSongSingerName(String songSingerName) {
        this.songSingerName = songSingerName;
    }
}
