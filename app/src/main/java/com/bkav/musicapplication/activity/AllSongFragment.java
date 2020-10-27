package com.bkav.musicapplication.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bkav.musicapplication.R;
import com.bkav.musicapplication.Song.Song;
import com.bkav.musicapplication.Song.SongAdapter;
import com.bkav.musicapplication.contentprovider.SongProvider;

import java.util.ArrayList;


/**
 * Display Songs List
 */
public class AllSongFragment extends Fragment {

    private MainActivity mMainActivity;
    private RelativeLayout mSmallPlayingAreaRelativeLayout;
    private RelativeLayout mSmallPlayRelativeLayout;   //Relative to display Playing area
    private ImageView mSongImageView;
    private TextView mCurrentSongNameTextView;
    private TextView mCurrentArtistNameTextView;
    private ImageButton mPlayMediaImageButton;
    private ArrayList<Song> mListSongAdapter = new ArrayList<>();  //song List object
    private SongAdapter mSongAdapter;   //song Adapter object
    private RecyclerView mRecyclerView; //Recycleview object

    private Handler mHandler = new Handler(){   //Handle object as a Thread
        private int songPosition = -1;
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(songPosition != mMainActivity.getmMediaService().getmMediaPosition()){
                upDateSmallPlayingRelativeLayout();
                mSongAdapter.notifyDataSetChanged();
            }
            songPosition = mMainActivity.getmMediaService().getmMediaPosition();

            Message message = new Message();
            sendMessageDelayed(message, 1000);
        }
    };

    /**
     * Create all Items RecycleView
     *
     * @param view
     */
    public void createRecycleView(View view) {
        mListSongAdapter = SongProvider.getInstance(getActivity().getApplicationContext()).getmListSong();
        mSongAdapter = new SongAdapter(mListSongAdapter, mMainActivity);
        mRecyclerView = view.findViewById(R.id.list_song_recycleview);
        mRecyclerView.setAdapter(mSongAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mMainActivity.getApplicationContext()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.all_list_song_fragment, container, false);
        mMainActivity = (MainActivity) getActivity();
        createRecycleView(view);
        initView(view);
        if(savedInstanceState != null){
            mSmallPlayRelativeLayout.setVisibility(savedInstanceState.getInt("IS_PLAYING_VISIBLE"));
        }
        setOnClick();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mMainActivity.getmMediaService() != null){
            upDateSmallPlayingRelativeLayout();
        }
    }

    /**
     * Initial view byViewFindId
     * *@param view
     */
    private void initView(View view){
        mSmallPlayRelativeLayout = view.findViewById(R.id.small_playing_area);
        mSongImageView = view.findViewById(R.id.small_song_imageview);
        mCurrentSongNameTextView = view.findViewById(R.id.small_name_current_song);
        mCurrentArtistNameTextView = view.findViewById(R.id.small_singer_name_current_song);
        mPlayMediaImageButton = view.findViewById(R.id.small_play_imagebutton);
        mSmallPlayingAreaRelativeLayout = view.findViewById(R.id.small_playing_area);
    }

    /**
     * Set onlick for all element of all song fragment
     */
    public void setOnClick() {
        //Set onClick for SmallPlayRelativeLayout
        mSmallPlayRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Dung 2 Activity
                Intent intent = new Intent(getActivity().getApplicationContext(), MediaPlaybackActivity.class);
                startActivity(intent);
            }
        });

        //Set onClick for Play/Pause ImageButton
        mPlayMediaImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMainActivity.getmMediaService().getmMediaPlayer().isPlaying()){
                    mMainActivity.getmMediaService().pauseMedia();
                    mPlayMediaImageButton.setImageResource(R.drawable.ic_media_play_light);
                } else {
                    mMainActivity.getmMediaService().resumeMedia();
                    mPlayMediaImageButton.setImageResource(R.drawable.ic_media_pause_light);
                }

            }
        });
    }

    /**
     * Show Small Playing Area
     */
    public void showSmallPlayingArea() {
        if (mSmallPlayingAreaRelativeLayout.getVisibility() == View.GONE) {
            mSmallPlayingAreaRelativeLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Update UI for SmallPlayingRelativeLayout on AllSongFragment
     */
    public void upDateSmallPlayingRelativeLayout(){
        Message message = new Message();
        mHandler.sendMessage(message);

        int position = mMainActivity.getmMediaService().getmMediaPosition();
        //Update Song Name, Artist Name, Play Button
        mCurrentSongNameTextView.setText(mListSongAdapter.get(position).getmTitle());
        mCurrentArtistNameTextView.setText(mListSongAdapter.get(position).getmArtistName());
        if(mMainActivity.getmMediaService().getmMediaPlayer().isPlaying()){
            mPlayMediaImageButton.setImageResource(R.drawable.ic_media_pause_light);
        } else {
            mPlayMediaImageButton.setImageResource(R.drawable.ic_media_play_light);
        }

        //Update AlbumArt
        mSongImageView.setImageURI(mListSongAdapter.get(position)
                        .queryAlbumUri(mListSongAdapter.get(position).getmAlbumID()));
        if(mSongImageView.getDrawable() == null){
            mSongImageView.setImageResource(R.drawable.ic_reason_album);
        }
        //Update UI for Adapter
        mSongAdapter.notifyDataSetChanged();
        //Scroll to playing position
        mRecyclerView.smoothScrollToPosition(position);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("IS_PLAYING_VISIBLE", mSmallPlayRelativeLayout.getVisibility());
    }
}
