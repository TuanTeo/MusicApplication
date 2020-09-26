package com.bkav.musicapplication.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;


/**
 * Service de quan ly cac logic choi nhac, cac bai hat dang choi,
 * object MediaPlayer de choi nhac, thong bao dieu khien nhac
 */
public class MediaPlaybackService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
