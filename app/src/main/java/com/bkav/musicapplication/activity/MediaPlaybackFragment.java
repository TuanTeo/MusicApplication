package com.bkav.musicapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bkav.musicapplication.MediaStatus;
import com.bkav.musicapplication.R;
import com.bkav.musicapplication.Song.Song;
import com.bkav.musicapplication.contentprovider.SongProvider;
import com.bkav.musicapplication.service.MediaPlaybackService;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Show playing music content
 */
public class MediaPlaybackFragment extends Fragment
        implements SeekBar.OnSeekBarChangeListener, MainActivity.IBindService {

    //Conponents of Fragment
    private RelativeLayout mMediaFragmentRelativeLayout;
    private ImageView mSongAlbumImageView;
    private TextView mSongNameTextView;
    private TextView mArtistNameTextView;
    private TextView mCurrentTimeTextView;
    private TextView mTotalTimeTextView;
    private ImageButton mShuffleImageButton;
    private ImageButton mRepeatImageButton;
    private ImageButton mBackToAllSongImageButton;
    private ImageButton mUnlikeImageButton;
    private ImageButton mLikeImageButton;
    private ImageButton mPreviousImageButton;
    private ImageButton mNextImageButton;
    private ImageButton mPlayImageButton;
    private SeekBar mSongSeekBar;

    //Get MediaPlayBack activity
    private MediaPlaybackActivity mMediaPlaybackActivity;

    //Get MediaService
    MediaPlaybackService mMediaPlaybackService;

    //Get List all song
    private ArrayList<Song> mListAllSong;

    //Check to repeat
    private boolean mIsRepeat = false;
    //Check to shuffle
    private boolean mIsShuffle = false;

    //Media Status
    MediaStatus mMediaStatus = MediaStatus.NONE;

    //SimpleDateFormat to format song time
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");;

    //TODO:Handler object to do update current play time - is a thread, send message each delay time
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
//            int position = msg.arg1;
//            position += 1000;
//            mSongSeekBar.setProgress(position);

            //Update Current time to TextView
            mCurrentTimeTextView.setText(
                    simpleDateFormat.format(
                            mMediaPlaybackService.getmMediaPlayer().getCurrentPosition()));
            //Update Current Progress to SeekBar
            mSongSeekBar.setProgress(
                    mMediaPlaybackService.getmMediaPlayer().getCurrentPosition());
            //Auto next Song
            if(mMediaPlaybackService.getmMediaPlayer().getCurrentPosition() >=
                    (mMediaPlaybackService.getmMediaPlayer().getDuration())){
                mMediaPlaybackService.autoNextMedia();
                upDateInfoView();
            }

            Message message = new Message();
//            message.arg1 = position;
            sendMessageDelayed(message, 500);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.media_playback_fragment, container, false);
        mListAllSong = SongProvider.getInstanceNotCreate().getmListSong();

        //Get MediaPlaybackActivity object
        mMediaPlaybackActivity = ((MediaPlaybackActivity) getActivity());
        mMediaPlaybackActivity.setBindServiceListener(this);
        mMediaPlaybackService = mMediaPlaybackActivity.getmMediaService();

        //initial components view
        initialView(view);
        //Set onClickListener
        setOnClick();
        //Set info to view
        upDateInfoView();
        //Return Fragment view
        return view;
    }

    private void setOnClick() {
        /*Set onClick for backToAllSongImageButton*/
        mBackToAllSongImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlaybackActivity.finish();
            }
        });

        /*Set onClick for Repeat Button*/
        mRepeatImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaStatus == MediaStatus.NONE) {     /*is NONE => REPEAT_ALL*/
                    mMediaStatus = MediaStatus.REPEAT_ALL;
                    mRepeatImageButton.setImageResource(R.drawable.ic_repeat_dark_selected);
                } else if (mMediaStatus == MediaStatus.SHUFFLE) {  /*is SHUFFLE  => REPEAT_AND_SHUFFLE*/
                    mMediaStatus = MediaStatus.REPEAT_AND_SHUFFLE;
                    mRepeatImageButton.setImageResource(R.drawable.ic_repeat_dark_selected);
                } else if (mMediaStatus == MediaStatus.REPEAT_ALL){  /*is REPEAT_ALL or REPEAT_AND_SHUFFLE => REPEAT_ONE*/
                    mMediaStatus = mMediaStatus.REPEAT_ONE;
                    mRepeatImageButton.setImageResource(R.drawable.ic_repeat_one_song_dark);
                } else if (mMediaStatus == MediaStatus.REPEAT_AND_SHUFFLE) {
                    mMediaStatus = mMediaStatus.REPEAT_ONE_AND_SHUFFLE;
                    mRepeatImageButton.setImageResource(R.drawable.ic_repeat_one_song_dark);
                }else if (mMediaStatus == MediaStatus.REPEAT_ONE) {  /*is REPEAT_ONE => SHUFFLE or NONE*/
                    mMediaStatus = MediaStatus.NONE;
                    mRepeatImageButton.setImageResource(R.drawable.ic_repeat_white);
                } else if (mMediaStatus == MediaStatus.REPEAT_ONE_AND_SHUFFLE) {
                    mMediaStatus = MediaStatus.SHUFFLE;
                    mRepeatImageButton.setImageResource(R.drawable.ic_repeat_white);
                }
                mMediaPlaybackService.setmMediaStatus(mMediaStatus);
                Toast.makeText(mMediaPlaybackActivity, mMediaStatus + "", Toast.LENGTH_SHORT).show();
            }
        });

        /*Set onClick for Shuffle button*/
        mShuffleImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaStatus == MediaStatus.NONE) { /*is NONE => SHUFFLE*/
                    mMediaStatus = MediaStatus.SHUFFLE;
                    mShuffleImageButton.setImageResource(R.drawable.ic_play_shuffle_orange_noshadow);
                    mIsShuffle = true;
                } else if (mMediaStatus == MediaStatus.SHUFFLE) { /*is SHUFFLE => NONE*/
                    mMediaStatus = MediaStatus.NONE;
                    mShuffleImageButton.setImageResource(R.drawable.ic_shuffle_white);
                    mIsShuffle = false;
                } else if (mMediaStatus == MediaStatus.REPEAT_AND_SHUFFLE) { /*is REPEAT_AND_SHUFFLE => REPEAT*/
                    mMediaStatus = MediaStatus.REPEAT_ALL;
                    mShuffleImageButton.setImageResource(R.drawable.ic_shuffle_white);
                    mIsShuffle = false;
                } else if (mMediaStatus == MediaStatus.REPEAT_ALL) { /*is REPEAT_ALL => REPEAT_AND_SHUFFLE*/
                    mMediaStatus = MediaStatus.REPEAT_AND_SHUFFLE;
                    mShuffleImageButton.setImageResource(R.drawable.ic_play_shuffle_orange_noshadow);
                    mIsShuffle = true;
                } else if (mMediaStatus == MediaStatus.REPEAT_ONE) { /*is REPEAT_ONE => REPEAT_REPEAT_ONE*/
                    mMediaStatus = MediaStatus.REPEAT_ONE_AND_SHUFFLE;
                    mShuffleImageButton.setImageResource(R.drawable.ic_play_shuffle_orange_noshadow);
                    mIsShuffle = true;
                } else if (mMediaStatus == MediaStatus.REPEAT_ONE_AND_SHUFFLE) {
                    mMediaStatus = MediaStatus.REPEAT_ONE;
                    mShuffleImageButton.setImageResource(R.drawable.ic_shuffle_white);
                    mIsShuffle = false;
                }
                mMediaPlaybackService.setmMediaStatus(mMediaStatus);
                Toast.makeText(mMediaPlaybackActivity, mMediaStatus + "", Toast.LENGTH_SHORT).show();
            }
        });

        /*Set onClick for PlayImageButton*/
        mPlayImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlaybackService.getmMediaPlayer().isPlaying()) {
                    mMediaPlaybackService.pauseMedia();
                    mPlayImageButton.setImageResource(R.drawable.ic_media_play_light);
                } else {
                    mMediaPlaybackService.resumeMedia();
                    mPlayImageButton.setImageResource(R.drawable.ic_media_pause_light);
                }
            }
        });

        /*Set onClick for NextButton*/
        mNextImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlaybackService.nextMedia();
                mPlayImageButton.setImageResource(R.drawable.ic_media_pause_light);
                upDateInfoView();

            }
        });

        /*Set onClick for Previous Button*/
        mPreviousImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlaybackService.getmMediaPlayer().getCurrentPosition() >= 3000) {
                    mMediaPlaybackService.repeatMedia();
                } else if (mMediaPlaybackService.getmMediaPosition() == 0) {
                    mMediaPlaybackService
                            .playMedia(mMediaPlaybackService.getListSongService().size() - 1);
                } else {
                    mMediaPlaybackService
                            .playMedia((mMediaPlaybackService.getmMediaPosition() - 1));
                }
                mPlayImageButton.setImageResource(R.drawable.ic_media_pause_light);
                upDateInfoView();
            }
        });
    }

    private void upDateInfoView() {
        //upDateTimeSong();
        //// TODO: 10/24/20 thanhnch se xem van de khong goi duoc interface trong onbind sevice

        if (mMediaPlaybackService.getmMediaPlayer().isPlaying()) {
            //TODO: what is Message? and what do it do?
            Message message = new Message();
            message.arg1 = mMediaPlaybackService.getmMediaPlayer().getCurrentPosition();
            mHandler.sendMessage(message);
        }

        if (mMediaPlaybackService != null) {
            if(mMediaPlaybackService.getmMediaPlayer().isPlaying()){
                mPlayImageButton.setImageResource(R.drawable.ic_media_pause_light);
            } else{
                mPlayImageButton.setImageResource(R.drawable.ic_media_play_light);
            }

            //Get song position
            int songPositon = mMediaPlaybackService.getmMediaPosition();
            //Set song name view
            mSongNameTextView.setText(mListAllSong.get(songPositon).getmTitle());
            //Set artist name view
            mArtistNameTextView.setText(mListAllSong.get(songPositon).getmArtistName());
            //Set Album Art view
            mSongAlbumImageView.setImageURI(mListAllSong.get(songPositon)
                    .queryAlbumUri(mListAllSong.get(songPositon).getmAlbumID()));
            if (mSongAlbumImageView.getDrawable() == null) {
                mSongAlbumImageView.setImageResource(R.drawable.ic_reason_album);
                mMediaFragmentRelativeLayout.setBackgroundResource(R.drawable.ic_reason_album);
            } else {
                mMediaFragmentRelativeLayout.setBackground(mSongAlbumImageView.getDrawable());
            }

            //Set total time view
            mTotalTimeTextView.setText(mListAllSong.get(songPositon).getmDurationString());
            mSongSeekBar.setMax(mListAllSong.get(songPositon).getmDuration());
        }
    }

    private void initialView(View view) {
        //Work with SeekBar
        mSongSeekBar = view.findViewById(R.id.seek_bar_play_song);
        //Lang nghe su kien cho SeekBar
        mSongSeekBar.setOnSeekBarChangeListener(this);

        //Find view by id
        mMediaFragmentRelativeLayout = view.findViewById(R.id.seek_bar_media_playback_area);
        mSongAlbumImageView = view.findViewById(R.id.media_album_small_image);
        mArtistNameTextView = view.findViewById(R.id.media_name_singer_current);
        mSongNameTextView = view.findViewById(R.id.media_name_song_current);
        mBackToAllSongImageButton = view.findViewById(R.id.media_allsong_button);
        mCurrentTimeTextView = view.findViewById(R.id.current_time_playing);
        mTotalTimeTextView = view.findViewById(R.id.total_time_playing);
        mShuffleImageButton = view.findViewById(R.id.media_shuffle_button);
        mRepeatImageButton = view.findViewById(R.id.media_repeat_button);
        mUnlikeImageButton = view.findViewById(R.id.media_dislike_button);
        mLikeImageButton = view.findViewById(R.id.media_like_button);
        mPreviousImageButton = view.findViewById(R.id.media_prev_button);
        mNextImageButton = view.findViewById(R.id.media_next_button);
        mPlayImageButton = view.findViewById(R.id.media_play_button);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.d("MediaPlaybackFragment", "onProgressChanged: ");

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d("MediaPlaybackFragment", "onStartTrackingTouch: ");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mMediaPlaybackService.getmMediaPlayer().seekTo(mSongSeekBar.getProgress());
        Log.d("MediaPlaybackFragment", "onStopTrackingTouch: ");
    }

    /**
     * Update CurrentTime for SeekBar
     * (Copy: KhoaPham)
     */
    private void upDateTimeSong(){
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
//                //Update Current time to TextView
//                mCurrentTimeTextView.setText(
//                        simpleDateFormat.format(
//                                mMediaPlaybackService.getmMediaPlayer().getCurrentPosition()));
//
//                //Auto next Song
//                if(mMediaPlaybackService.getmMediaPlayer().getCurrentPosition() >=
//                        (mMediaPlaybackService.getmMediaPlayer().getDuration() - 200)){
//                    mMediaPlaybackService.nextMedia();
//                    upDateInfoView();
//                }
//                //Update Current Progress to SeekBar
//                mSongSeekBar.setProgress(
//                        mMediaPlaybackService.getmMediaPlayer().getCurrentPosition());
//                //CallBack
//                mHandler.postDelayed(this, 200);
//            }
//        }, 100);
    }

    /**
     *
     */
    @Override
    public void onBind() {
//        if (mMediaPlaybackService.getmMediaPlayer().isPlaying()) {
//            //TODO: what is Message? and what do it do?
//            Message message = new Message();
//            message.arg1 = mMediaPlaybackService.getmMediaPlayer().getCurrentPosition();
//            mHandler.sendMessage(message);
//        }
    }
}
