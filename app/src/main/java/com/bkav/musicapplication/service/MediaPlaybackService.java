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
import com.bkav.musicapplication.broadcast.StartServiceBroadcast;
import com.bkav.musicapplication.contentprovider.SongProvider;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Service de quan ly cac logic choi nhac, cac bai hat dang choi,
 * object MediaPlayer de choi nhac, thong bao dieu khien nhac
 */
public class MediaPlaybackService extends Service {

    public static final String PLAY_MEDIA = "PlayMedia"; //Key for Broadcast to play media

    private StartServiceBroadcast mStartServiceBroadcast;
    private MediaPlayer mMediaPlayer;
    private ArrayList<Song> mListAllSong = SongProvider.getInstanceNotCreate().getmListSong();
    private Binder binder;
    private Callbacks activity;

    @Override
    public void onCreate() {

        Toast.makeText(this, "onCreate: Service", Toast.LENGTH_SHORT).show();

        //TODO tao IntentFilter de doan nhan Broadcast
        mStartServiceBroadcast = new StartServiceBroadcast();
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
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int position = intent.getIntExtra("SongPosition", -1);
        Toast.makeText(this, "onStartCommand: SongPosition " + position, Toast.LENGTH_SHORT).show();
        Log.d("StartCommand", "List Size: " + getListSongService().size());

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

    //Here Activity register to the service as Callbacks client
    public void registerClient(Activity activity){
        this.activity = (Callbacks) activity;
    }

    //callbacks interface for communication with service clients!
    public interface Callbacks{
        public void updateClient(long data);
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

}
