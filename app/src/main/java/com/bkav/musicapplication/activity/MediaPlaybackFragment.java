package com.bkav.musicapplication.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bkav.musicapplication.R;

/**
 * Show playing music content
 */
public class MediaPlaybackFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, MainActivity.IBindService {

    private SeekBar mSongSeekBar;
    private MediaPlaybackActivity mMediaPlaybackActivity;
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

        //Work with SeekBar
        mSongSeekBar = view.findViewById(R.id.seek_bar_play_song);
        mSongSeekBar.setMax(100);
        //Lang nghe su kien cho SeekBar
        mSongSeekBar.setOnSeekBarChangeListener(this);

        //
        mMediaPlaybackActivity = ((MediaPlaybackActivity) getActivity());
        mMediaPlaybackActivity.setBindServiceListener(this);
        return view;
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

}
