package com.bkav.musicapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bkav.musicapplication.R;
import com.bkav.musicapplication.Song.Song;
import com.bkav.musicapplication.contentprovider.SongProvider;

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

    //Get List all song
    private ArrayList<Song> mListAllSong = SongProvider.getInstanceNotCreate().getmListSong();

    //TODO:Handler object to do update current play time
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int position = msg.arg1;
            position += 1;
            mSongSeekBar.setProgress(position);
            Message message = new Message();
            message.arg1 = position;
            sendMessageDelayed(message, 1000);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.media_playback_fragment, container, false);
        initialView(view);
        upDateInfoView();
        setOnClick();
        return view;
    }

    private void setOnClick() {
        mBackToAllSongImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mMediaPlaybackActivity, MainActivity.class);
                startActivity(intent);
            }
        });
        mPlayImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediaPlaybackActivity.getmMediaService().getmMediaPlayer().isPlaying()){
                    mMediaPlaybackActivity.getmMediaService().pauseMedia();
                    mPlayImageButton.setImageResource(R.drawable.ic_media_play_light);
                } else {
                    mMediaPlaybackActivity.getmMediaService().resumeMedia();
                    mPlayImageButton.setImageResource(R.drawable.ic_media_pause_light);
                }
            }
        });
    }

    private void upDateInfoView() {
        //Get song position
        int songPositon = mMediaPlaybackActivity.getmMediaService().getmMediaPosition();
        //Set song name view
        mSongNameTextView.setText(mListAllSong.get(songPositon).getmTitle());
        //Set artist name view
        mArtistNameTextView.setText(mListAllSong.get(songPositon).getmArtistName());
        //Set Album Art view
       mSongAlbumImageView.setImageURI(mListAllSong.get(songPositon)
                        .queryAlbumUri(mListAllSong.get(songPositon).getmAlbumID()));
        if(mSongAlbumImageView.getDrawable() == null){
            mSongAlbumImageView.setImageResource(R.drawable.ic_reason_album);
        }

        //Set total time view
        mTotalTimeTextView.setText(mListAllSong.get(songPositon).getmDurationString());
    }

    private void initialView(View view) {
        //Work with SeekBar
        mSongSeekBar = view.findViewById(R.id.seek_bar_play_song);
        mSongSeekBar.setMax(100);
        //Lang nghe su kien cho SeekBar
        mSongSeekBar.setOnSeekBarChangeListener(this);

        //Get MediaPlaybackActivity object
        mMediaPlaybackActivity = ((MediaPlaybackActivity) getActivity());
        mMediaPlaybackActivity.setBindServiceListener(this);

        //Find view by id
        mMediaFragmentRelativeLayout = view.findViewById(R.id.media_playback_area);
        mSongAlbumImageView = view.findViewById(R.id.media_album_small_image);
        mArtistNameTextView = view.findViewById(R.id.media_name_singer_current);
        mSongNameTextView = view.findViewById(R.id.media_name_song_current);
        mBackToAllSongImageButton = view.findViewById(R.id.media_allsong_button);
        mCurrentTimeTextView = view.findViewById(R.id.current_time_playing);
        mTotalTimeTextView = view.findViewById(R.id.total_time_song_item_textview);
        mShuffleImageButton = view.findViewById(R.id.media_shuffle_button);
        mRepeatImageButton = view.findViewById(R.id.media_repeat_button);
        mUnlikeImageButton = view.findViewById(R.id.media_dislike_button);
        mLikeImageButton = view.findViewById(R.id.media_like_button);
        mPreviousImageButton = view.findViewById(R.id.media_prev_button);
        mNextImageButton = view.findViewById(R.id.media_next_button);
        mPlayImageButton = view.findViewById(R.id.media_play_button);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        mMainActivity.setBindServiceListener(null);
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
//        mSongSeekBar.getProgress();
//        ((MainActivity) getActivity()).getmMediaService().
        Log.d("MediaPlaybackFragment", "onStopTrackingTouch: ");
    }

    /**
     *
     */
    @Override
    public void onBind() {
        if(mMediaPlaybackActivity.getmMediaService().getmMediaPlayer().isPlaying()){
            //TODO: what is Message? and what do it do?
            Message message = new Message();
            message.arg1 = mMediaPlaybackActivity.getmMediaService().getmMediaPlayer().getCurrentPosition();
            mHandler.sendMessage(message);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
