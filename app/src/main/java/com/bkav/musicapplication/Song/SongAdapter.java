package com.bkav.musicapplication.Song;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bkav.musicapplication.R;
import com.bkav.musicapplication.activity.AllSongFragment;
import com.bkav.musicapplication.activity.MainActivity;
import com.bkav.musicapplication.service.MediaPlaybackService;

import java.security.AllPermission;
import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {


    //Create List of Song
    private ArrayList<Song> mListSongAdapter;
    private LayoutInflater mInflater;
    private MainActivity mainActivity;

    private int mLastItemPositionInt = -1;  //Vi tri cua phan tu khi clicked

    public SongAdapter(ArrayList<Song> mListSongAdapter, MainActivity context) {
        this.mainActivity = context;
        this.mListSongAdapter = mListSongAdapter;
        this.mInflater = LayoutInflater.from(context);
        this.mainActivity.setListSongMainActivity(mListSongAdapter);
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.song_list_item, parent, false);
        return new SongViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        //Set Name song
        holder.mSongNameItemTextView.setText(mListSongAdapter.get(position).getmTitle());
        holder.mTotalTimeSongItemTextView.setText(mListSongAdapter.get(position).getmDurationString());

        //Set font
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mLastItemPositionInt == position) {
                holder.mSongNameItemTextView.setTextAppearance(R.style.SongTheme_NameSongClickOverLay);
                //TODO ve tim hieu them
            } else {
                holder.mSongNameItemTextView.setTextAppearance(R.style.SongTheme_NameSongOverLay);
            }
        }

        //Set Serial
        if (mLastItemPositionInt == position) {
            holder.mSerialSongNumberTextView.setVisibility(View.INVISIBLE);
            holder.mPlayingSongImageLinearLayout.setVisibility(View.VISIBLE);
        } else {
            holder.mSerialSongNumberTextView.setText((position + 1) + "");
            holder.mSerialSongNumberTextView.setVisibility(View.VISIBLE);
            holder.mPlayingSongImageLinearLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mListSongAdapter.size();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder
            implements RecyclerView.OnClickListener {

        private AllSongFragment mAllSongFragment;

        private SongAdapter mSongAdapter;
        private TextView mSerialSongNumberTextView;
        private TextView mSongNameItemTextView;
        private TextView mTotalTimeSongItemTextView;
        private ImageButton mSongDetailItemImageButton;
        private LinearLayout mPlayingSongImageLinearLayout;

        /**
         * Constructor of Song View Holder
         *
         * @param itemView
         * @param mSongAdapter
         */
        public SongViewHolder(@NonNull View itemView, SongAdapter mSongAdapter) {
            super(itemView);
            this.mSongAdapter = mSongAdapter;
          //  Log.d("123", "SongViewHolder: "+mSongAdapter);
            mSerialSongNumberTextView = itemView.findViewById(R.id.serial_item_textview);
            mSongNameItemTextView = itemView.findViewById(R.id.song_name_item_textview);
            mTotalTimeSongItemTextView = itemView.findViewById(R.id.total_time_song_item_textview);
            mSongDetailItemImageButton = itemView.findViewById(R.id.song_detail_item);
            mPlayingSongImageLinearLayout = itemView.findViewById(R.id.playing_icon_layout);
            itemView.setOnClickListener(this);
            mAllSongFragment = AllSongFragment.getInstance();
        }

        @Override
        public void onClick(View v) {

            if (v.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                //Show small playing area
                mainActivity.showSmallPlayingArea();
                mLastItemPositionInt = getAdapterPosition();
                notifyDataSetChanged();
                startMediaService();
                mAllSongFragment.upDateSmallPlayingRelativeLayout(mLastItemPositionInt);
            } else {
                mLastItemPositionInt = getAdapterPosition();
                notifyDataSetChanged();
            }
        }

        public void startMediaService() {
            Intent intent = new Intent(mSongAdapter.mainActivity.getApplicationContext(), MediaPlaybackService.class);
            intent.putExtra("SongPosition", mLastItemPositionInt);
            mSongAdapter.mainActivity.startService(intent);
            mSongAdapter.mainActivity.bindService(intent, mainActivity.getmServiceConnection(), Context.BIND_AUTO_CREATE);
        }

    }
}
