package com.bkav.musicapplication.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bkav.musicapplication.R;

public class MediaPlaybackActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_playback_activity);

        MediaPlaybackFragment mediaPlaybackFragment = new MediaPlaybackFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.media_playback_view, mediaPlaybackFragment)
                .commit();
    }


}
