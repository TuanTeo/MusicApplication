package com.bkav.musicapplication.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bkav.musicapplication.R;
import com.bkav.musicapplication.contentprovider.SongProvider;
import com.bkav.musicapplication.service.MediaPlaybackService;

public class MediaPlaybackActivity extends AppCompatActivity {

    private boolean mIsBindService = false;

    private MediaPlaybackService mMediaService;
    private MainActivity.IBindService mBindServiceListener;

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            //Tao doi tuong service
            MediaPlaybackService.BoundService bind = (MediaPlaybackService.BoundService) service;
            mMediaService = bind.getService(); //Get instance of servive
            mIsBindService = true;
            showMediaPlaybackFragment();
//            mBindServiceListener.onBind();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_playback_activity);
        bindMediaService();
//        showMediaPlaybackFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Bind to Service
     */
    public void bindMediaService() {
        //Tao moi 1 Service tu MediaActivity => null
        //Giai phap => lay Service hien co
        Intent intent = new Intent(getApplicationContext(), MediaPlaybackService.class);
        getApplicationContext().bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mIsBindService){
            getApplicationContext().unbindService(mServiceConnection);
            mIsBindService = false;
        }
    }

    private void showMediaPlaybackFragment() {
        MediaPlaybackFragment mediaPlaybackFragment = new MediaPlaybackFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.media_playback_view, mediaPlaybackFragment)
                .commit();
    }


    public MediaPlaybackService getmMediaService() {
        return mMediaService;
    }



    /**
     * Tuong tac giua Activity va Fragment
     * (MainActivity voi MediaPlaybackFragment)??
     *
     * @param bindServiceListener
     */
    public void setBindServiceListener(MainActivity.IBindService bindServiceListener) {
        mBindServiceListener = bindServiceListener;
    }
}
