package com.bkav.musicapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;

import com.bkav.musicapplication.R;
import com.bkav.musicapplication.Song;
import com.bkav.musicapplication.SongAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Song> mListSong;
    private SongAdapter mSongAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create Music app Toolbar
        setSupportActionBar((androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar_main));

//        //Hien thi FragmentAllSong first
        if (savedInstanceState == null) {
            AllSongFragment allSongFragment = new AllSongFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, allSongFragment).commit();
        }


    }

    /**
     * Push item cua menu len toolbar
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Set event for item clicked
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        if (itemID == R.id.seach_action) {
            /**
             * TODO xu ly tac vu tim kiem bai hat trong list bai hat
             */
        }
        return super.onOptionsItemSelected(item);
    }

    public void switchToDetail() {
        MediaPlaybackFragment mediaPlaybackFragment = new MediaPlaybackFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, mediaPlaybackFragment).commit();
    }
}