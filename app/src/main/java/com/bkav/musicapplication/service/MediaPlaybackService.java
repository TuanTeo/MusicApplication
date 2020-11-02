package com.bkav.musicapplication.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bkav.musicapplication.Enum.MediaStatus;
import com.bkav.musicapplication.Playable;
import com.bkav.musicapplication.R;
import com.bkav.musicapplication.broadcast.NotificationActionService;
import com.bkav.musicapplication.song.Song;
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
public class MediaPlaybackService extends Service implements Playable {

    private static final String NOTIFI_CHANNEL_ID = "notification_channel";
    private static final int MEDIA_NOTIFICATION_ID = 0;
    private static final String ACTION_PREVIOUS = "action_previous";
    private static final String ACTION_PLAY = "action_play";
    private static final String ACTION_NEXT = "action_next";

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString(NotificationActionService.NOTIFICATION_ACTION_NAME);
            switch (action){
                case ACTION_PREVIOUS:
                    onMediaPrevious();
                    break;
                case ACTION_NEXT:
                    onMediaNext();
                    break;
                case ACTION_PLAY:
                    if(mMediaPlayer.isPlaying()){
                        onMediaPause();
                    } else {
                        onMediaPlay();
                    }
                    break;
            }
        }
    };

    private NotificationManager mNotificationManager;

    private int mMediaPosition = -1;

    private MediaStatus mMediaStatus = MediaStatus.NONE;
    private MediaPlayer mMediaPlayer;
    private ArrayList<Song> mListAllSong = SongProvider.getInstanceNotCreate().getmListSong();

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        registerReceiver(mBroadcastReceiver, new IntentFilter(NotificationActionService.BROADCAST_ACTION));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new BoundService();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mNotificationManager.cancel(MEDIA_NOTIFICATION_ID);
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
        if(mMediaPlayer != null){
            mMediaPlayer.release();
        }
        mNotificationManager.cancelAll();
        stopSelf();
    }

    @Override
    public void onMediaPrevious() {
        prevMedia();
    }

    @Override
    public void onMediaPlay() {
        resumeMedia();
    }

    @Override
    public void onMediaPause() {
        pauseMedia();
    }

    @Override
    public void onMediaNext() {
        nextMedia();
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

    public void setListSongService(ArrayList<Song> listSong){
        this.mListAllSong = listSong;
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
        mMediaPosition = position;
        try {
            stopMedia();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(mListAllSong.get(position).getmPath());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            sendNotification();
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
        sendNotification();
    }

    public void resumeMedia() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
        sendNotification();
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
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,
                MEDIA_NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /*Add Media control button and set action*/
        //Previous button
        Intent intentPrev = new Intent(getApplicationContext(), NotificationActionService.class)
                .setAction(ACTION_PREVIOUS);
        PendingIntent pendingIntentPrev = PendingIntent.getBroadcast(getApplicationContext(),
                MEDIA_NOTIFICATION_ID, intentPrev, PendingIntent.FLAG_UPDATE_CURRENT);
        int prevImage = R.drawable.ic_rew_dark;

        //Next button
        Intent intentNext = new Intent(getApplicationContext(), NotificationActionService.class)
                .setAction(ACTION_NEXT);
        PendingIntent pendingIntentNext = PendingIntent.getBroadcast(getApplicationContext(),
                MEDIA_NOTIFICATION_ID, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
        int nextImage = R.drawable.ic_fwd_dark;

        //Play button
        int playImage;
        Intent intentPlay = new Intent(getApplicationContext(), NotificationActionService.class)
                .setAction(ACTION_PLAY);
        PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(getApplicationContext(),
                MEDIA_NOTIFICATION_ID, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);
        if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
            playImage = R.mipmap.ic_media_pause_light;
        } else {
            playImage = R.mipmap.ic_media_play_light;
        }



        //Bitmap to set to Drawer Notification
        Bitmap albumArt = BitmapFactory.decodeResource(getResources(), R.drawable.ic_reason_album);

        NotificationCompat.Builder notifyBuilder =
                new NotificationCompat.Builder(this, NOTIFI_CHANNEL_ID)
                        .setContentTitle(mListAllSong.get(mMediaPosition).getmTitle())  //Set title
                        .setContentText(mListAllSong.get(mMediaPosition).getmArtistName())  //Set text detail
                        .setSmallIcon(R.mipmap.ic_launcher) //Set smallIcon (bat buoc)
                        .setLargeIcon(albumArt)
                        .setDefaults(DEFAULT_ALL)
                        .setOnlyAlertOnce(true)     //Show notification for only first time
                        .setContentIntent(notificationPendingIntent)
                        .setAutoCancel(false)       //Can't remove notification
                        .addAction(prevImage, "Previous", pendingIntentPrev)
                        .addAction(playImage, "Play", pendingIntentPlay)
                        .addAction(nextImage, "Next", pendingIntentNext)
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2))
                        .setOngoing(true);          //Need to don't remove notification

        return notifyBuilder;
    }

    private void sendNotification(){
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        mNotificationManager.notify(MEDIA_NOTIFICATION_ID, notifyBuilder.build());
    }
}
