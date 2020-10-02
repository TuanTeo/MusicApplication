package com.bkav.musicapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
        createMainView();
    }


    /**
     * Show All Song Fragment
     */
    public void showAllSongFragment(int intRes) {
        AllSongFragment allSongFragment = new AllSongFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(intRes, allSongFragment)
                .commit();
    }

    /**
     * Show Small Playing Area
     */
    public void showSmallPlayingArea() {
            mSmallPlayingAreaRelativeLayout = findViewById(R.id.small_playing_area);
        if (mSmallPlayingAreaRelativeLayout.getVisibility() == View.GONE) {
            mSmallPlayingAreaRelativeLayout.setVisibility(View.VISIBLE);
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
//        getMenuInflater().inflate(R.menu.menu_main, menu);
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
//        int itemID = item.getItemId();
//        if (itemID == R.id.seach_action_imagebutton) {
//            /**
//             * TODO xu ly tac vu tim kiem bai hat trong list bai hat
//             */
//        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Create view on the First time
     */
    public void createMainView() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            showAllSongFragment(R.id.container);
        } else {
            showAllSongFragment(R.id.container_left);
            showMediaFragment(R.id.container_right);
        }
    }

    /**
     * Show Detail Media Fragment
     */
    public void showMediaFragment(int intRes) {
        MediaPlaybackFragment mediaPlaybackFragment = new MediaPlaybackFragment();
        getSupportFragmentManager().beginTransaction()
                .add(intRes, mediaPlaybackFragment)
                .commit();
    }

}