package com.bkav.musicapplication.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.bkav.musicapplication.Enum.MediaStatus;
import com.bkav.musicapplication.R;
import com.bkav.musicapplication.song.Song;
import com.bkav.musicapplication.contentprovider.SongProvider;
import com.bkav.musicapplication.service.MediaPlaybackService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Show playing music content
 */
public class MediaPlaybackFragment extends Fragment
        implements SeekBar.OnSeekBarChangeListener, MainActivity.IBindService {

    public static final String REPEAT_STATUS = "Repeat_status";
    public static final String SHUFFLE_STATUS = "Shuffle status";

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
    private static int mIsRepeat = 0;  // 0:NONE    -   1:REPEAT    -   2:REPEAT_ONE
    //Check to shuffle
    private static boolean mIsShuffle = false;

    //Media Status
    MediaStatus mMediaStatus = MediaStatus.NONE;

    //SimpleDateFormat to format song time
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");

    //Variables to check when have to update UI
    private int mSongPosition = -1;
    boolean mIsPlay = false;

    //TODO:Handler object to do update current play time - is a thread, send message each delay time
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
//            int position = msg.arg1;
//            position += 1000;
//            mSongSeekBar.setProgress(position);

//            Log.d("TAG2", "handleMessage: " + msg.what);
//            switch (msg.what) {
//                case 0:
//                    removeMessages(0);
//                    break;
//                default: {
            //Update Current time to TextView
            mCurrentTimeTextView.setText(
                    simpleDateFormat.format(
                            mMediaPlaybackService.getmMediaPlayer().getCurrentPosition()));
            //Update Current Progress to SeekBar
            mSongSeekBar.setProgress(
                    mMediaPlaybackService.getmMediaPlayer().getCurrentPosition());
            //Update View when next Song
            if (mSongPosition != mMediaPlaybackService.getmMediaPosition()
                    || mIsPlay != mMediaPlaybackActivity.getmMediaService().getmMediaPlayer().isPlaying()) {
                upDateInfoView();
                mSongPosition = mMediaPlaybackService.getmMediaPosition();
                mIsPlay = mMediaPlaybackActivity.getmMediaService().getmMediaPlayer().isPlaying();
            }
            Message message = new Message();
//                  message.arg1 = position;
            sendMessageDelayed(message, 500);
//                }
//            }

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
//        mMediaPlaybackActivity.setBindServiceListener(this);
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

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences =
                mMediaPlaybackActivity
                        .getSharedPreferences("Repeat_and_Shuffle_status", Context.MODE_PRIVATE);
        //Get status for repeat and shuffle button
        int repeatStatus = sharedPreferences.getInt(REPEAT_STATUS, -1);
        boolean shuffleStatus = sharedPreferences.getBoolean(SHUFFLE_STATUS, false);
        if (repeatStatus == 1) {
            mRepeatImageButton.setImageResource(R.drawable.ic_repeat_dark_selected);
        } else if (repeatStatus == 2) {
            mRepeatImageButton.setImageResource(R.drawable.ic_repeat_one_song_dark);
        } else {
            mRepeatImageButton.setImageResource(R.drawable.ic_repeat_white);
        }
        if (shuffleStatus) {
            mShuffleImageButton.setImageResource(R.drawable.ic_play_shuffle_orange_noshadow);
        } else {
            mShuffleImageButton.setImageResource(R.drawable.ic_shuffle_white);
        }
        mMediaPlaybackService.setmMediaStatus(getMediaStatus());
    }

    /**
     * Set onClick for ImageButton in MediaFragment
     */
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
//                if (mMediaStatus == MediaStatus.NONE) {     /*is NONE => REPEAT_ALL*/
//                    mMediaStatus = MediaStatus.REPEAT_ALL;
//                    mRepeatImageButton.setImageResource(R.drawable.ic_repeat_dark_selected);
//                    mIsRepeat = 1;
//                } else if (mMediaStatus == MediaStatus.SHUFFLE) {  /*is SHUFFLE  => REPEAT_AND_SHUFFLE*/
//                    mMediaStatus = MediaStatus.REPEAT_AND_SHUFFLE;
//                    mRepeatImageButton.setImageResource(R.drawable.ic_repeat_dark_selected);
//                    mIsRepeat = 1;
//                } else if (mMediaStatus == MediaStatus.REPEAT_ALL) {  /*is REPEAT_ALL or REPEAT_AND_SHUFFLE => REPEAT_ONE*/
//                    mMediaStatus = mMediaStatus.REPEAT_ONE;
//                    mRepeatImageButton.setImageResource(R.drawable.ic_repeat_one_song_dark);
//                    mIsRepeat = 2;
//                } else if (mMediaStatus == MediaStatus.REPEAT_AND_SHUFFLE) {
//                    mMediaStatus = mMediaStatus.REPEAT_ONE_AND_SHUFFLE;
//                    mRepeatImageButton.setImageResource(R.drawable.ic_repeat_one_song_dark);
//                    mIsRepeat = 2;
//                } else if (mMediaStatus == MediaStatus.REPEAT_ONE) {  /*is REPEAT_ONE => SHUFFLE or NONE*/
//                    mMediaStatus = MediaStatus.NONE;
//                    mRepeatImageButton.setImageResource(R.drawable.ic_repeat_white);
//                    mIsRepeat = 0;
//                } else if (mMediaStatus == MediaStatus.REPEAT_ONE_AND_SHUFFLE) {
//                    mMediaStatus = MediaStatus.SHUFFLE;
//                    mRepeatImageButton.setImageResource(R.drawable.ic_repeat_white);
//                    mIsRepeat = 0;
//                }

                //Recode
                if (mIsRepeat == 0) {
                    mIsRepeat = 1;
                    mRepeatImageButton.setImageResource(R.drawable.ic_repeat_dark_selected);
                } else if (mIsRepeat == 1) {
                    mIsRepeat = 2;
                    mRepeatImageButton.setImageResource(R.drawable.ic_repeat_one_song_dark);
                } else {
                    mIsRepeat = 0;
                    mRepeatImageButton.setImageResource(R.drawable.ic_repeat_white);
                }
                mMediaPlaybackService.setmMediaStatus(getMediaStatus());
                Toast.makeText(mMediaPlaybackActivity, mMediaStatus + "", Toast.LENGTH_SHORT).show();
            }
        });

        /*Set onClick for Shuffle button*/
        mShuffleImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mMediaStatus == MediaStatus.NONE) { /*is NONE => SHUFFLE*/
//                    mMediaStatus = MediaStatus.SHUFFLE;
//                    mShuffleImageButton.setImageResource(R.drawable.ic_play_shuffle_orange_noshadow);
//                    mIsShuffle = true;
//                } else if (mMediaStatus == MediaStatus.SHUFFLE) { /*is SHUFFLE => NONE*/
//                    mMediaStatus = MediaStatus.NONE;
//                    mShuffleImageButton.setImageResource(R.drawable.ic_shuffle_white);
//                    mIsShuffle = false;
//                } else if (mMediaStatus == MediaStatus.REPEAT_AND_SHUFFLE) { /*is REPEAT_AND_SHUFFLE => REPEAT*/
//                    mMediaStatus = MediaStatus.REPEAT_ALL;
//                    mShuffleImageButton.setImageResource(R.drawable.ic_shuffle_white);
//                    mIsShuffle = false;
//                } else if (mMediaStatus == MediaStatus.REPEAT_ALL) { /*is REPEAT_ALL => REPEAT_AND_SHUFFLE*/
//                    mMediaStatus = MediaStatus.REPEAT_AND_SHUFFLE;
//                    mShuffleImageButton.setImageResource(R.drawable.ic_play_shuffle_orange_noshadow);
//                    mIsShuffle = true;
//                } else if (mMediaStatus == MediaStatus.REPEAT_ONE) { /*is REPEAT_ONE => REPEAT_REPEAT_ONE*/
//                    mMediaStatus = MediaStatus.REPEAT_ONE_AND_SHUFFLE;
//                    mShuffleImageButton.setImageResource(R.drawable.ic_play_shuffle_orange_noshadow);
//                    mIsShuffle = true;
//                } else if (mMediaStatus == MediaStatus.REPEAT_ONE_AND_SHUFFLE) {
//                    mMediaStatus = MediaStatus.REPEAT_ONE;
//                    mShuffleImageButton.setImageResource(R.drawable.ic_shuffle_white);
//                    mIsShuffle = false;
//                }
                //Recode
                if (!mIsShuffle) {
                    mIsShuffle = true;
                    mShuffleImageButton.setImageResource(R.drawable.ic_play_shuffle_orange_noshadow);
                } else {
                    mIsShuffle = false;
                    mShuffleImageButton.setImageResource(R.drawable.ic_shuffle_white);
                }
                mMediaPlaybackService.setmMediaStatus(getMediaStatus());
                Toast.makeText(mMediaPlaybackActivity, mMediaStatus + "", Toast.LENGTH_SHORT).show();
            }
        });

        /*Set onClick for PlayImageButton*/
        mPlayImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlaybackService.getmMediaPlayer().isPlaying()) {
                    mMediaPlaybackService.pauseMedia();
                    mPlayImageButton.setImageResource(R.mipmap.ic_media_play_light);
                } else {
                    mMediaPlaybackService.resumeMedia();
                    mPlayImageButton.setImageResource(R.mipmap.ic_media_pause_light);
                }
            }
        });

        /*Set onClick for NextButton*/
        mNextImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlaybackService.nextMedia();
                mPlayImageButton.setImageResource(R.mipmap.ic_media_pause_light);
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
                mPlayImageButton.setImageResource(R.mipmap.ic_media_pause_light);
                upDateInfoView();
            }
        });
    }

    /**
     * Update View by SongPosition
     */
    private void upDateInfoView() {
        //upDateTimeSong();
        //// TODO: 10/24/20 thanhnch se xem van de khong goi duoc interface trong onbind sevice

//        if (mMediaPlaybackService.getmMediaPlayer().isPlaying()) {
            //TODO: what is Message? and what do it do?
            Message message = new Message();
//            message.arg1 = mMediaPlaybackService.getmMediaPlayer().getCurrentPosition();
            mHandler.sendMessage(message);
//        }

        if (mMediaPlaybackService != null) {
            if (mMediaPlaybackService.getmMediaPlayer().isPlaying()) {
                mPlayImageButton.setImageResource(R.mipmap.ic_media_pause_light);
            } else {
                mPlayImageButton.setImageResource(R.mipmap.ic_media_play_light);
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

    /**
     * Initial FindViewbyID
     *
     * @param view
     */
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

    /**
     * SeekBar Progress
     *
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    /**
     * Start Change progress of SeekBar
     *
     * @param seekBar
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    /**
     * Stop change Progress of SeekBar
     *
     * @param seekBar
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mMediaPlaybackService.getmMediaPlayer().seekTo(mSongSeekBar.getProgress());
    }

    /**
     * Update CurrentTime for SeekBar
     * (Copy: KhoaPham)
     */
    private void upDateTimeSong() {
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
     * ???
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

    private MediaStatus getMediaStatus() {
        if (!mIsShuffle && mIsRepeat == 0) {
            mMediaStatus = MediaStatus.NONE;
        } else if (!mIsShuffle && mIsRepeat == 1) {
            mMediaStatus = MediaStatus.REPEAT_ALL;
        } else if (!mIsShuffle && mIsRepeat == 2) {
            mMediaStatus = MediaStatus.REPEAT_ONE;
        } else if (mIsShuffle && mIsRepeat == 0) {
            mMediaStatus = MediaStatus.SHUFFLE;
        } else if (mIsShuffle && mIsRepeat == 1) {
            mMediaStatus = MediaStatus.REPEAT_AND_SHUFFLE;
        } else if (mIsShuffle && mIsRepeat == 2) {
            mMediaStatus = MediaStatus.REPEAT_ONE_AND_SHUFFLE;
        }
        return mMediaStatus;
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences =
                mMediaPlaybackActivity.getSharedPreferences(
                        "Repeat_and_Shuffle_status", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHUFFLE_STATUS, mIsShuffle);
        editor.putInt(REPEAT_STATUS, mIsRepeat);
        editor.commit();

    }

}
