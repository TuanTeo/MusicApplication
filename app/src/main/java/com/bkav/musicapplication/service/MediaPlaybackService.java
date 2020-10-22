package com.bkav.musicapplication.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bkav.musicapplication.Song.Song;
import com.bkav.musicapplication.Song.SongAdapter;
import com.bkav.musicapplication.broadcast.ServiceBroadcast;
import com.bkav.musicapplication.contentprovider.SongProvider;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Service de quan ly cac logic choi nhac, cac bai hat dang choi,
 * object MediaPlayer de choi nhac, thong bao dieu khien nhac
 */
public class MediaPlaybackService extends Service {

    public static final String PLAY_MEDIA = "PlayMedia"; //Key for Broadcast to play media

    private int mMediaPosition = -1;

    private IBinder mIBinder = new BoundService();
    private ServiceBroadcast mStartServiceBroadcast;
    private MediaPlayer mMediaPlayer;
    private ArrayList<Song> mListAllSong = SongProvider.getInstanceNotCreate().getmListSong();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY; //khong tao lai Service khi app bi tat ngang
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //Tra ve 1 doi tuong MediaPlaybackService
    public class BoundService extends Binder{
        public MediaPlaybackService getService(){
            return MediaPlaybackService.this;
        }
    }


    public ArrayList<Song> getListSongService(){
        Log.d("AllSongService", "setListSongService: ");
        return SongProvider.getInstanceNotCreate().getmListSong();
    }

    public int getmMediaPosition() {
        return mMediaPosition;
    }

    public void playMedia(int position){
        try {
            stopMedia();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(mListAllSong.get(position).getmPath());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mMediaPosition = position;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pauseMedia(){
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
        }
    }

    public void resumeMedia(){
        if(!mMediaPlayer.isPlaying()){
            mMediaPlayer.start();
        }
    }

    public void stopMedia(){
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public MediaPlayer getmMediaPlayer() {
        return mMediaPlayer;
    }

    public void setmMediaPlayer(MediaPlayer mMediaPlayer) {
        this.mMediaPlayer = mMediaPlayer;
    }
}
