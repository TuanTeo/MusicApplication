package com.bkav.musicapplication.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bkav.musicapplication.Enum.MediaStatus;
import com.bkav.musicapplication.R;
import com.bkav.musicapplication.Song.Song;
import com.bkav.musicapplication.activity.MainActivity;
import com.bkav.musicapplication.contentprovider.SongProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static androidx.core.app.NotificationCompat.DEFAULT_ALL;
import static androidx.core.app.NotificationCompat.PRIORITY_MIN;


/**
 * Service de quan ly cac logic choi nhac, cac bai hat dang choi,
 * object MediaPlayer de choi nhac, thong bao dieu khien nhac
 */
public class MediaPlaybackService extends Service {

    private static final String NOTIFI_CHANNEL_ID = "notification_channel";
    private static final int MEDIA_NOTIFICATION_ID = 0;
    private NotificationManager mNotificationManager;

    private int mMediaPosition = -1;
    private MediaStatus mMediaStatus = MediaStatus.NONE;
    private MediaPlayer mMediaPlayer;
    private ArrayList<Song> mListAllSong = SongProvider.getInstanceNotCreate().getmListSong();

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new BoundService();
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
        mNotificationManager.cancel(MEDIA_NOTIFICATION_ID);
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
            sendNotification();
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
        if(position == mMediaPosition){
            position = randomPosition();
        }
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

    /**
     * To create Form of notification
     */
    private void createNotificationChannel(){
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel =
                    new NotificationChannel(NOTIFI_CHANNEL_ID,
                            "Media_Service",
                            NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(false); //Set mau den thong bao (true)
            notificationChannel.setLightColor(Color.RED);   //Mau do
            notificationChannel.enableVibration(false);  //Rung khi thong bao
            mNotificationManager.createNotificationChannel(notificationChannel);    //Tao kenh thong bao
        }
    }

    /**
     * To manager, build and show notification
     * @return
     */
    private NotificationCompat.Builder getNotificationBuilder(){
        //Tao event clicked in notification => back to MainActivity
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,
                MEDIA_NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifyBuilder =
                new NotificationCompat.Builder(this, NOTIFI_CHANNEL_ID)
                        .setContentTitle(mListAllSong.get(mMediaPosition).getmTitle())  //Set title
                        .setContentText(mListAllSong.get(mMediaPosition).getmArtistName())  //Set text detail
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setPriority(PRIORITY_MIN)
                        .setDefaults(DEFAULT_ALL)
                        .setContentIntent(notificationPendingIntent);    //Set smallIcon (bat buoc)

        return notifyBuilder;
    }

    private void sendNotification(){
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        mNotificationManager.notify(MEDIA_NOTIFICATION_ID, notifyBuilder.build());
    }
}
