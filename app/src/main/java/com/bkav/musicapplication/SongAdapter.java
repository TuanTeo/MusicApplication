package com.bkav.musicapplication;

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
    private ArrayList<Song> mListSongAdapter;
    private LayoutInflater mInflater;
    private MainActivity mainActivity;

    private int mLastItemPositionInt = -1;  //Vi tri cua phan tu khi clicked

    public SongAdapter(ArrayList<Song> mListSongAdapter, MainActivity context) {
        mainActivity = context;
        this.mListSongAdapter = mListSongAdapter;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.song_list_item, parent, false);
        return new SongViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
//        mDefaultTypeFace = holder.mSongNameItem. ;
        holder.mSerialSongNumberTextView.setText(position + "");
        holder.mSongNameItemTextView.setText(mListSongAdapter.get(position).getSongName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mLastItemPositionInt == position) {
                holder.mSongNameItemTextView.setTextAppearance(R.style.SongTheme_NameSongClickOverLay);

                //TODO ve tim hieu them
            } else {
                holder.mSongNameItemTextView.setTextAppearance(R.style.SongTheme_NameSongOverLay);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mListSongAdapter.size();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder
            implements RecyclerView.OnClickListener {

        private SongAdapter mSongAdapter;
        private TextView mSerialSongNumberTextView;
        private TextView mSongNameItemTextView;
        private TextView mTotalTimeSongItemTextView;
        private ImageButton mSongDetailItemImageButton;

        /**
         * Constructor of Song View Holder
         *
         * @param itemView
         * @param mSongAdapter
         */
        public SongViewHolder(@NonNull View itemView, SongAdapter mSongAdapter) {
            super(itemView);
            this.mSongAdapter = mSongAdapter;
            mSerialSongNumberTextView = itemView.findViewById(R.id.serial_item_textview);
            mSongNameItemTextView = itemView.findViewById(R.id.song_name_item_textview);
            mTotalTimeSongItemTextView = itemView.findViewById(R.id.total_time_song_item_textview);
            mSongDetailItemImageButton = itemView.findViewById(R.id.song_detail_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //Show small playing area
            mainActivity.showSmallPlayingArea();
            mLastItemPositionInt = getAdapterPosition();
            notifyDataSetChanged();
        }
    }
}
