package com.bkav.musicapplication.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bkav.musicapplication.R;
import com.bkav.musicapplication.Song.Song;
import com.bkav.musicapplication.service.MediaPlaybackService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Song> mListSong;
    private RelativeLayout mSmallPlayingAreaRelativeLayout;
    private MediaPlaybackService mMediaService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //Tao doi tuong service
            MediaPlaybackService.BoundService bind = (MediaPlaybackService.BoundService) service;
            mMediaService = bind.getService(); //Get instance of service
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceConnection = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create Music app Toolbar
        setSupportActionBar(
                (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar_main));
        //Permission READ_EXTERNAL_STORAGE
        isReadStoragePermissionGranted();
        //Create Fragment View
        createMainView();
    }



    /**
     * Push item cua menu len toolbar
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
        MediaPlaybackFragment mMediaPlaybackFragment = new MediaPlaybackFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(intRes, mMediaPlaybackFragment)
                .commit();
    }

    /**
     * Show All Song Fragment
     */
    public void showAllSongFragment(int intRes) {
        getSupportFragmentManager().beginTransaction()
                .replace(intRes, AllSongFragment.getInstance())
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
     * Check read storage permission granted
     * @return
     */
    public boolean isReadStoragePermissionGranted() {

        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            return true;
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected. In this UI,
            // include a "cancel" or "no thanks" button that allows the user to
            // continue using your app without granting the permission.
            showInContextUI();

        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        return false;

//        if (Build.VERSION.SDK_INT >= 23) {
//            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//                    == PackageManager.PERMISSION_GRANTED) {
//                return true;
//            } else {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//                return false;
//            }
//        } else { //permission is automatically granted on sdk<23 upon installation
//            return true;
//        }
    }

    private void showInContextUI() {
//        Toast.makeText(MainActivity.this,
//                "App nghe nhac can quyen doc ghi the nho de hien thi danh sach bai hat" +
//                "" ,Toast.LENGTH_SHORT).show();

        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("App nghe nhac can quyen doc ghi the nho de hien thi danh sach bai hat")
                .setTitle(R.string.read_storage_permission_dialog_title_educational_UI);

        //3. Add button
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        });

        // 4. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.

                    //Load lai Fragment
                    createMainView();

                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.

                }
            });


    public void setListSongMainActivity(ArrayList<Song> mListSong){
        Log.d("AllSongMainActivity", "setListSongMainActivity: ");
        this.mListSong = mListSong;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        Toast.makeText(MainActivity.this, "onDestroy: MainActivity", Toast.LENGTH_SHORT).show();
    }

    public ServiceConnection getmServiceConnection(){
        return mServiceConnection;
    }

    public MediaPlaybackService getmMediaService() {
        return mMediaService;
    }

    public void setmMediaService(MediaPlaybackService mMediaService) {
        this.mMediaService = mMediaService;
    }

    /**
     *
     */
    public interface IBindService {
        void onBind();
    }
}