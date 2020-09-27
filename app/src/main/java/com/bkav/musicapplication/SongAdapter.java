package com.bkav.musicapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bkav.musicapplication.activity.MainActivity;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    //Create List of Song
    private ArrayList<Song> mListSong;
    private LayoutInflater mInflater;
    private MainActivity mainActivity;

    public SongAdapter(ArrayList<Song> mListSong, MainActivity context) {
        mainActivity = context;
        this.mListSong = mListSong;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.song_list_item, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        holder.mSerialSongNumber.setText(position + "");
        holder.mSongNameItem.setText(mListSong.get(position).getSongName());
    }

    @Override
    public int getItemCount() {
        return mListSong.size();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder
            implements RecyclerView.OnClickListener{
        private TextView mSerialSongNumber;
        private TextView mSongNameItem;
        private TextView mTotalTimeSongItem;
        private ImageButton mSongDetailItem;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            mSerialSongNumber = itemView.findViewById(R.id.serial_item_textview);
            mSongNameItem = itemView.findViewById(R.id.song_name_item_textview);
            mTotalTimeSongItem = itemView.findViewById(R.id.total_time_song_item_textview);
            mSongDetailItem = itemView.findViewById(R.id.song_detail_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //Lay vi tri cua item clicked
            int position = getLayoutPosition();

            //In dam Ten bai hat
            mSongNameItem = v.findViewById(R.id.song_name_item_textview);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mSongNameItem.setTextAppearance(R.style.TextAppearance_AppCompat_Headline);
            }
            mainActivity.switchToDetail();
        }
    }
}
