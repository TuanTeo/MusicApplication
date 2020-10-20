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

    private ServiceBroadcast mStartServiceBroadcast;
    private MediaPlayer mMediaPlayer;
    private ArrayList<Song> mListAllSong = SongProvider.getInstanceNotCreate().getmListSong();

    @Override
    public void onCreate() {

        Toast.makeText(this, "onCreate: Service", Toast.LENGTH_SHORT).show();

        //TODO tao IntentFilter de doan nhan Broadcast
        mStartServiceBroadcast = new ServiceBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PLAY_MEDIA);

        //Dang ki Receiver
        registerReceiver(mStartServiceBroadcast, intentFilter);

        Log.d("MediaService", "onCreate: ");
        super.onCreate();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "onBind: Service", Toast.LENGTH_SHORT).show();
        return new BoundService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Get position of media from intent
        int position = intent.getIntExtra(SongAdapter.SONG_POSITION, -1);
        //Play media
        playMedia(position);
        return START_NOT_STICKY; //khong tao lai Service khi app bi tat ngang
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "onDestroy: Service", Toast.LENGTH_SHORT).show();
        //Huy dang ki Receiver
        unregisterReceiver(mStartServiceBroadcast);
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


    public void playMedia(int position){
        try {
            stopMedia();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(mListAllSong.get(position).getmPath());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
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
