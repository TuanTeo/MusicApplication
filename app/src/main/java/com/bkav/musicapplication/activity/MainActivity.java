package com.bkav.musicapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.bkav.musicapplication.R;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout mSmallPlayingAreaRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create Music app Toolbar
        setSupportActionBar(
                (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar_main));

        //Hien thi FragmentAllSong first
        if (savedInstanceState == null) {
            showAllSongFragment();
        }
    }

    /**
     *
     * Show All Song Fragment Wh
     */
    public void showAllSongFragment() {
        AllSongFragment allSongFragment = new AllSongFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, allSongFragment)
                .commit();
    }

    /**
     * Show Small Playing Area
     */
    public void showSmallPlayingArea(){
        mSmallPlayingAreaRelativeLayout = findViewById(R.id.small_playing_area);
        mSmallPlayingAreaRelativeLayout.setVisibility(View.VISIBLE);
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
}