package com.bkav.musicapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bkav.musicapplication.R;
import com.bkav.musicapplication.Song;
import com.bkav.musicapplication.SongAdapter;

import java.util.ArrayList;

/**
 * Display Songs List
 */
public class AllSongFragment extends Fragment {

    private RelativeLayout mSmallPlayingArea;   //Relative to display Playing area
    private ArrayList<Song> mListSong;  //song List object
    private SongAdapter mSongAdapter;   //song Adapter object
    private RecyclerView mRecyclerView; //Recycleview object

    /**
     * Create all Items RecycleView
     * @param view
     */
    public void createRecycleView(View view) {
        mListSong = new ArrayList<>();
        initListSong(mListSong);    //init listSong
        mRecyclerView = view.findViewById(R.id.list_song_recycleview);
        mSongAdapter = new SongAdapter(mListSong, (MainActivity) getActivity());
        mRecyclerView.setAdapter(mSongAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
    }

    /**
     * Create init List Song
     * @param listSong
     */
    public void initListSong(ArrayList<Song> listSong) {
        listSong.add(new Song(R.raw.bangkhuang,
                R.drawable.ic_reason_album, "Bang Khuang", "JusterT"));
        listSong.add(new Song(R.raw.bewithyou,
                R.drawable.ic_reason_album, "Be With You", "Han Quoc"));
        listSong.add(new Song(R.raw.bangkhuang,
                R.drawable.ic_reason_album, "Bang Khuang", "JusterT"));
        listSong.add(new Song(R.raw.bangkhuang,
                R.drawable.ic_reason_album, "Bang Khuang", "JusterT"));
        listSong.add(new Song(R.raw.bangkhuang,
                R.drawable.ic_reason_album, "Bang Khuang", "JusterT"));
        listSong.add(new Song(R.raw.bangkhuang,
                R.drawable.ic_reason_album, "Bang Khuang", "JusterT"));
        listSong.add(new Song(R.raw.bangkhuang,
                R.drawable.ic_reason_album, "Bang Khuang", "JusterT"));
        listSong.add(new Song(R.raw.bangkhuang,
                R.drawable.ic_reason_album, "Bang Khuang", "JusterT"));
        listSong.add(new Song(R.raw.bangkhuang,
                R.drawable.ic_reason_album, "Bang Khuang", "JusterT"));
        listSong.add(new Song(R.raw.bangkhuang,
                R.drawable.ic_reason_album, "Bang Khuang", "JusterT"));
    }

    public void setOnClickListenerRecycleView(View view){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.all_list_song_fragment, container, false);
        createRecycleView(view);

        //TODO: sau khi set OnClick cho recycleView => set VISIBLE cho mSmallPlayingArea
        return view;
    }
}
