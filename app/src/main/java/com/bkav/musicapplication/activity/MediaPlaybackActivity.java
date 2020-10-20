package com.bkav.musicapplication.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bkav.musicapplication.R;
import com.bkav.musicapplication.service.MediaPlaybackService;

public class MediaPlaybackActivity extends AppCompatActivity {

    private MediaPlaybackService mMediaService;
    private MainActivity.IBindService mBindServiceListener;
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            //Tao doi tuong service
            MediaPlaybackService.BoundService bind = (MediaPlaybackService.BoundService) service;
            mMediaService = bind.getService(); //Get instance of service
//              if(mMediaService != null){
            mBindServiceListener.onBind();
//              }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceConnection = null;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_playback_activity);

        showMediaPlaybackFragment();
        Intent intent = new Intent(MediaPlaybackActivity.this.getApplicationContext(), MediaPlaybackService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
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

    public void setmMediaService(MediaPlaybackService mMediaService) {
        this.mMediaService = mMediaService;
    }

    /**
     * Tuong tac giua Activity va Fragment
     * (MainActivity voi MediaPlaybackFragment)??
     * @param bindServiceListener
     */
    public void setBindServiceListener(MainActivity.IBindService bindServiceListener) {
        mBindServiceListener = bindServiceListener;
    }
}
