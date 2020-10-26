package com.bkav.musicapplication.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bkav.musicapplication.MediaStatus;
import com.bkav.musicapplication.Song.Song;
import com.bkav.musicapplication.broadcast.ServiceBroadcast;
import com.bkav.musicapplication.contentprovider.SongProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


/**
 * Service de quan ly cac logic choi nhac, cac bai hat dang choi,
 * object MediaPlayer de choi nhac, thong bao dieu khien nhac
 */
public class MediaPlaybackService extends Service {

    public static final String PLAY_MEDIA = "PlayMedia"; //Key for Broadcast to play media

    private int mMediaPosition = -1;
    private MediaStatus mMediaStatus = MediaStatus.NONE;
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
        Toast.makeText(this, "Destroy Service", Toast.LENGTH_SHORT).show();
        mMediaPlayer.release();
    }

    //Tra ve 1 doi tuong MediaPlaybackService
    public class BoundService extends Binder {
        public MediaPlaybackService getService() {
            return MediaPlaybackService.this;
        }
    }

    public ArrayList<Song> getListSongService() {
        return SongProvider.getInstanceNotCreate().getmListSong();
    }

    public void autoNextMedia(){
        if (mMediaStatus == MediaStatus.SHUFFLE
                || mMediaStatus == MediaStatus.REPEAT_AND_SHUFFLE) {
            nextByShuffleWithButton();
        } else if(mMediaStatus == MediaStatus.NONE){
            if(mMediaPosition == mListAllSong.size() - 1){
                /*Don't continue play*/
                mMediaPlayer.pause();
            } else {
                playMedia(mMediaPosition + 1);
            }
        } else if(mMediaStatus == MediaStatus.REPEAT_ONE
                || mMediaStatus == MediaStatus.REPEAT_ONE_AND_SHUFFLE){
            repeatMedia();
        } else {
            nextWithButton();
        }
    }

    public void nextMedia() {
        if (mMediaStatus == MediaStatus.SHUFFLE
                || mMediaStatus == MediaStatus.REPEAT_AND_SHUFFLE
                || mMediaStatus == MediaStatus.REPEAT_ONE_AND_SHUFFLE) {
            nextByShuffleWithButton();
        } else {
            nextWithButton();
        }
    }

    public void prevMedia() {
        if (mMediaStatus == MediaStatus.NONE) { //Prev with NONE Status
            if (mMediaPlayer.getCurrentPosition() >= 3000) {
                repeatMedia();
            } else if (getmMediaPosition() == 0) {
                playMedia(getListSongService().size() - 1);
            } else {
                playMedia((getmMediaPosition() - 1));
            }
        } else if (mMediaStatus == MediaStatus.REPEAT_ALL) {

        } else if (mMediaStatus == MediaStatus.SHUFFLE
                || mMediaStatus == MediaStatus.REPEAT_ONE_AND_SHUFFLE) {
            nextByShuffleWithButton();
        } else if (mMediaStatus == MediaStatus.REPEAT_AND_SHUFFLE) {

        } else if (mMediaStatus == MediaStatus.REPEAT_ONE) {

        }
    }

    public void playMedia(int position) {
        try {
            stopMedia();
            mMediaPlayer = new MediaPlayer();
//            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    autoNextMedia();
//                }
//            });
            mMediaPlayer.setDataSource(mListAllSong.get(position).getmPath());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mMediaPosition = position;
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    autoNextMedia();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pauseMedia() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public void resumeMedia() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

    public void stopMedia() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void repeatMedia() {
        playMedia(getmMediaPosition());
    }

    //Play with shuffle Status
    public void nextByShuffleWithButton() {
        int position = randomPosition();
        playMedia(position);
    }

    private void nextWithButton() {
        if (getmMediaPosition() == (mListAllSong.size()-1)) {
            playMedia(0);
        } else {
            playMedia(getmMediaPosition() + 1);
        }
    }

    private int randomPosition() {
        Random random = new Random();
        return random.nextInt(mListAllSong.size());
    }

    public MediaPlayer getmMediaPlayer() {
        return mMediaPlayer;
    }

    public MediaStatus getmMediaStatus() {
        return mMediaStatus;
    }

    public void setmMediaStatus(MediaStatus mMediaStatus) {
        this.mMediaStatus = mMediaStatus;
    }

    public int getmMediaPosition() {
        return mMediaPosition;
    }
}
