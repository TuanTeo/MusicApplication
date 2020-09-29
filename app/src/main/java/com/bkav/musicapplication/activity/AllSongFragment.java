package com.bkav.musicapplication.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private RelativeLayout mSmallPlayRelativeLayout;   //Relative to display Playing area
    private ArrayList<Song> mListSongAdapter;  //song List object
    private SongAdapter mSongAdapter;   //song Adapter object
    private RecyclerView mRecyclerView; //Recycleview object
    private MainActivity mainActivity;

    /**
     * Create all Items RecycleView
     *
     * @param view
     */
    public void createRecycleView(View view) {
        mListSongAdapter = new ArrayList<>();
        initListSong(mListSongAdapter);    //init listSong
        mRecyclerView = view.findViewById(R.id.list_song_recycleview);
        mSongAdapter = new SongAdapter(mListSongAdapter, (MainActivity) getActivity());
        mRecyclerView.setAdapter(mSongAdapter);
        mRecyclerView.setLayoutManager
                (new LinearLayoutManager(getActivity().getApplicationContext()));
    }

    /**
     * Create init List Song
     *
     * @param listSong
     */
    public void initListSong(ArrayList<Song> listSong) {
        listSong.add(new Song(R.raw.bangkhuang,
                R.drawable.ic_reason_album, "Bang Khuang", "JusterT"));
        listSong.add(new Song(R.raw.bewithyou,
                R.drawable.ic_reason_album, "Be With You", "Han Quoc"));
        listSong.add(new Song(R.raw.bangkhuang,
                R.drawable.ic_reason_album, "La ai mang nang di xa La ai mang nang di xaLa ai mang nang di xaLa ai mang nang di xa", "JusterT"));
        listSong.add(new Song(R.raw.bangkhuang,
                R.drawable.ic_reason_album, "Noi ta dung chan", "JusterT"));
        listSong.add(new Song(R.raw.bangkhuang,
                R.drawable.ic_reason_album, "Anh da sai", "JusterT"));
        listSong.add(new Song(R.raw.bangkhuang,
                R.drawable.ic_reason_album, "Yeu", "JusterT"));
        listSong.add(new Song(R.raw.bangkhuang,
                R.drawable.ic_reason_album, "Noi nay co anh", "JusterT"));
        listSong.add(new Song(R.raw.bangkhuang,
                R.drawable.ic_reason_album, "Yeu mot nguoi co le", "JusterT"));
        listSong.add(new Song(R.raw.bangkhuang,
                R.drawable.ic_reason_album, "Anh nang cua anh", "JusterT"));
        listSong.add(new Song(R.raw.bangkhuang,
                R.drawable.ic_reason_album, "Big City boy", "JusterT"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.all_list_song_fragment, container, false);
        createRecycleView(view);
        mSmallPlayRelativeLayout = view.findViewById(R.id.small_playing_area);
        setOnClick();
        return view;
    }

    /**
     * Set onlick for all element of all song fragment
     */
    public void setOnClick(){
        mSmallPlayRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlaybackFragment mediaPlaybackFragment = new MediaPlaybackFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction().addToBackStack(null)
                        .replace(R.id.main_view, mediaPlaybackFragment)
                        .commit();

            }
        });
    }
}
