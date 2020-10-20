package com.bkav.musicapplication.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.bkav.musicapplication.broadcast.ServiceBroadcast;
import com.bkav.musicapplication.contentprovider.SongProvider;

import java.util.ArrayList;


/**
 * Display Songs List
 */
public class AllSongFragment extends Fragment {

    private ServiceBroadcast mServiceBroadcast;
    private RelativeLayout mSmallPlayRelativeLayout;   //Relative to display Playing area
    private ImageView mSongImageView;
    private TextView mCurrentSongNameTextView;
    private TextView mCurrentArtistNameTextView;
    private ImageButton mPlayMediaImageButton;
    private ArrayList<Song> mListSongAdapter = new ArrayList<>();  //song List object
    private SongAdapter mSongAdapter;   //song Adapter object
    private RecyclerView mRecyclerView; //Recycleview object


    private static AllSongFragment instance;
    private AllSongFragment(){}

    public static AllSongFragment getInstance(){
        if(instance == null){
            synchronized(AllSongFragment.class){
                if(instance == null){
                    instance = new AllSongFragment();
                }
            }
        }
        return instance;
    }


    /**
     * Create all Items RecycleView
     *
     * @param view
     */
    public void createRecycleView(View view) {
        ArrayList<Song> listAllSong = SongProvider.getInstance(getActivity().getApplicationContext()).getmListSong();
        if(listAllSong.size() != 0){
            mListSongAdapter.addAll(listAllSong);
        }

        mSongAdapter = new SongAdapter(mListSongAdapter, (MainActivity) getActivity());
        mRecyclerView = view.findViewById(R.id.list_song_recycleview);
        mRecyclerView.setAdapter(mSongAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.all_list_song_fragment, container, false);
        createRecycleView(view);
        initView(view);

        setOnClick();
        return view;
    }

    private void registerServiceBroadcast(){
        Intent intent = new Intent("PLAY");
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
    }

    /**
     * Set onlick for all element of all song fragment
     */
    public void setOnClick() {
        //Set onClick for SmallPlayRelativeLayout
        mSmallPlayRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), MediaPlaybackActivity.class);
                startActivity(intent);
            }
        });

        //Set onClick for Play/Pause ImageButton
        mPlayMediaImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * Update UI for SmallPlayingRelativeLayout on AllSongFragment
     * @param position
     */
    public void upDateSmallPlayingRelativeLayout(int position){
        mCurrentSongNameTextView.setText(mListSongAdapter.get(position).getmTitle());
        mCurrentArtistNameTextView.setText(mListSongAdapter.get(position).getmArtistName());
        mPlayMediaImageButton.setImageResource(R.drawable.ic_media_pause_light);
        mSongImageView.setImageBitmap(BitmapFactory.decodeFile(mListSongAdapter.get(position).getmAlbumName()));
    }

    public ImageView getmSongImageView() {
        return mSongImageView;
    }

    public void setmSongImageView(ImageView mSongImageView) {
        this.mSongImageView = mSongImageView;
    }
}
