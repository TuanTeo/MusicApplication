package com.bkav.musicapplication.service;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class MediaServiceManager extends Application {
    private static MediaServiceManager sMediaServiceManager;
    private MediaPlaybackService mMediaService;
    public static synchronized MediaServiceManager getInstance(){
        if(sMediaServiceManager == null){
            sMediaServiceManager = new MediaServiceManager();
        }
        return sMediaServiceManager;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMediaService = ((MediaPlaybackService.BoundService) service).getService(); //get Service instance
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public void startService(Context context){
        Intent intent = new Intent(context, MediaPlaybackService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    public void stopService(){
        unbindService(mServiceConnection);
    }

    public MediaPlaybackService getmMediaService(){
        return mMediaService;
    }
}
