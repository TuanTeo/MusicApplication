package com.bkav.musicapplication.song;

import android.content.ContentValues;
import android.content.res.Configuration;
import android.database.SQLException;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bkav.musicapplication.R;
import com.bkav.musicapplication.activity.MainActivity;
import com.bkav.musicapplication.contentprovider.FavoriteSongProvider;
import com.bkav.musicapplication.favoritesongdatabase.FavoriteSongDataBase;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private ArrayList<Song> mListSongAdapter; //Create List of Song
    private LayoutInflater mInflater;
    private MainActivity mainActivity;

    private int mCurrentSongId = -1;
    private int mLastItemPositionInt = -1;  //Vi tri cua phan tu khi clicked

    public SongAdapter(ArrayList<Song> mListSongAdapter, MainActivity context) {
        this.mainActivity = context;
        this.mListSongAdapter = mListSongAdapter;
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
        if (mainActivity.getmMediaService() != null) {
            mLastItemPositionInt = mainActivity.getmMediaService().getmMediaPosition();
            //Set Name song
            holder.mSongNameItemTextView.setText(mListSongAdapter.get(position).getmTitle());
            holder.mTotalTimeSongItemTextView.setText(mListSongAdapter.get(position).getmDurationString());

            //Set font
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (position == mLastItemPositionInt) {
                    holder.mSongNameItemTextView.setTextAppearance(R.style.SongTheme_NameSongClickOverLay);
                } else {
                    holder.mSongNameItemTextView.setTextAppearance(R.style.SongTheme_NameSongOverLay);
                }
            }

            //Set Serial
            if (position == mLastItemPositionInt) {
                holder.mSerialSongNumberTextView.setVisibility(View.INVISIBLE);
                holder.mPlayingSongImageLinearLayout.setVisibility(View.VISIBLE);
            } else {
                holder.mSerialSongNumberTextView.setText((position + 1) + "");
                holder.mSerialSongNumberTextView.setVisibility(View.VISIBLE);
                holder.mPlayingSongImageLinearLayout.setVisibility(View.GONE);
            }
        } else {
            //Set Name song
            holder.mSongNameItemTextView.setText(mListSongAdapter.get(position).getmTitle());
            holder.mTotalTimeSongItemTextView.setText(mListSongAdapter.get(position).getmDurationString());

            //Set font
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (position == mLastItemPositionInt) {
                    holder.mSongNameItemTextView.setTextAppearance(R.style.SongTheme_NameSongClickOverLay);
                } else {
                    holder.mSongNameItemTextView.setTextAppearance(R.style.SongTheme_NameSongOverLay);
                }
            }

            //Set Serial
            if (position == mLastItemPositionInt) {
                holder.mSerialSongNumberTextView.setVisibility(View.INVISIBLE);
                holder.mPlayingSongImageLinearLayout.setVisibility(View.VISIBLE);
            } else {
                holder.mSerialSongNumberTextView.setText((position + 1) + "");
                holder.mSerialSongNumberTextView.setVisibility(View.VISIBLE);
                holder.mPlayingSongImageLinearLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mListSongAdapter.size();
    }

    public void setFilter(ArrayList<Song> newList) {
        mListSongAdapter = new ArrayList<>();
        mListSongAdapter.addAll(newList);
        notifyDataSetChanged();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder
            implements RecyclerView.OnClickListener {

        private TextView mSerialSongNumberTextView;
        private TextView mSongNameItemTextView;
        private TextView mTotalTimeSongItemTextView;
        private ImageButton mSongDetailItemImageButton;
        private LinearLayout mPlayingSongImageLinearLayout;

        /**
         * Constructor of Song View Holder
         *
         * @param itemView
         */
        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            mSerialSongNumberTextView = itemView.findViewById(R.id.serial_item_textview);
            mSongNameItemTextView = itemView.findViewById(R.id.song_name_item_textview);
            mTotalTimeSongItemTextView = itemView.findViewById(R.id.total_time_song_item_textview);
            mSongDetailItemImageButton = itemView.findViewById(R.id.song_detail_item);

            //Set onClick for Detail image button
            mSongDetailItemImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    PopupMenu popupMenu = new PopupMenu(mainActivity.getApplicationContext(), v);
                    popupMenu.inflate(R.menu.menu_song_item);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(mListSongAdapter.get(position).isFavoriteSong()){
                                if(item.getItemId() == R.id.add_to_favorite_song_item){
                                    item.setEnabled(false);
                                }
                            }
                            switch (item.getItemId()) {
                                case R.id.add_to_favorite_song_item:
                                    addSongToDataBase(position);
                                    notifyDataSetChanged();
                                    return true;
                                case R.id.delete_song_item:
                                    deleteSongFromDataBase(position);
                                    return true;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });

            mPlayingSongImageLinearLayout = itemView.findViewById(R.id.playing_icon_layout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            mLastItemPositionInt = getAdapterPosition();
//            mCurrentSongId = mListSongAdapter.get(mLastItemPositionInt).getmID();

            if (v.getResources().getConfiguration().orientation
                    != Configuration.ORIENTATION_LANDSCAPE) {
//                //Get position of item
//                mLastItemPositionInt = getAdapterPosition();

                //Update List Song for Service
                mainActivity.getmMediaService().setListSongService(mListSongAdapter);

                //play Media
                mainActivity.getmMediaService().playMedia(mLastItemPositionInt);

                //Add Current Song to Database
//                addSongToDataBase(mainActivity.getmMediaService().getmMediaPosition());

                //UpDate data on View
                notifyDataSetChanged();
                //Show small playing area
                mainActivity.getmAllSongFragment().showSmallPlayingArea();
                //Update UI in AllSongFragment
                mainActivity.getmAllSongFragment().upDateSmallPlayingRelativeLayout();

                if (mListSongAdapter.get(mLastItemPositionInt).isFavoriteSong()) {
                    addSongToDataBase(mLastItemPositionInt);
                } else {
                    mListSongAdapter.get(mLastItemPositionInt).countIncrease();
                }
            } else {    //Theo chieu ngang => khong hien thi small playing area
                mLastItemPositionInt = getAdapterPosition();
                mainActivity.getmMediaService().playMedia(mLastItemPositionInt);
                notifyDataSetChanged();
            }
        }

        /**
         * Get Song data put to ContentValues
         *
         * @param position
         * @return
         */
        private ContentValues getSongData(int position) {
            ContentValues contentValues = new ContentValues();

            contentValues.put(FavoriteSongDataBase.COLUMN_PATH, mListSongAdapter.get(position).getmPath());
            contentValues.put(FavoriteSongDataBase.COLUMN_TITLE, mListSongAdapter.get(position).getmTitle());
            contentValues.put(FavoriteSongDataBase.COLUMN_TRACK, mListSongAdapter.get(position).getmTrackNumber());
            contentValues.put(FavoriteSongDataBase.COLUMN_YEAR, mListSongAdapter.get(position).getmYear());
            contentValues.put(FavoriteSongDataBase.COLUMN_ALBUM, mListSongAdapter.get(position).getmAlbumName());
            contentValues.put(FavoriteSongDataBase.COLUMN_ALBUM_ID, mListSongAdapter.get(position).getmAlbumID());
            contentValues.put(FavoriteSongDataBase.COLUMN_ARTIST, mListSongAdapter.get(position).getmArtistName());
            contentValues.put(FavoriteSongDataBase.COLUMN_ARTIST_ID, mListSongAdapter.get(position).getmArtistId());
            contentValues.put(FavoriteSongDataBase.COLUMN_DURATION, mListSongAdapter.get(position).getmDuration());

            return contentValues;
        }

        /**
         * Add Song To DataBase
         *
         * @param position
         */
        private void addSongToDataBase(int position) {
            try {
                ContentValues values = getSongData(position);
                Uri uri = mainActivity.getContentResolver().insert(
                        FavoriteSongProvider.CONTENT_URI, values);
            } catch (SQLException ex) {
                Log.d("SongAdapter", "addSongToDataBase: exception");
            }
        }

        /**
         * Delete this Song from Database
         *
         * @param position
         */
        private void deleteSongFromDataBase(int position) {
            mainActivity.getContentResolver()
                    .delete(FavoriteSongProvider.CONTENT_URI
                            , "Path=?", new String[]{mListSongAdapter.get(position).getmPath()});
            Toast.makeText(mainActivity, "Deleted!", Toast.LENGTH_SHORT).show();
        }
    }
}
