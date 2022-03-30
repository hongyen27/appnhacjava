package com.example.cuoiky;

import android.Manifest;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Button btn_next, btn_previous, btn_pause, btn_repeat, btn_shuffle, btn_menu, btn_nav, btn_noti;
    TextView name;
    TextView curDur;
    TextView Dur;
    EditText search;
    Spinner spinner;
    SeekBar songSeekbar;
    NavigationView navigationView;
    ListView myListViewForSongs;
    DatabaseHelper databaseHelper;
    ImageView image;
    List<String> timer;

    long mise;
    int rept = 0;     //repeat
    int shuff = 0; //shuffle



//    Thread updateseekBar;
    ArrayList<File> songsearch;
    boolean issearch = false;

    static final long start_timee = 3600000;
    static ArrayList<File> mySongs;
    static MediaPlayer myMediaPlayer;
    static int position;
    static String sname;
    static boolean mTimer;
    static long time_left = start_timee;
    static Uri u;
    static String[] items;
    static CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setControl();
        runtimePermison();
        setEvent();

    }

    private void setControl() {
        timer = Arrays.asList("none", "30 phút", "1 giờ", "1 giờ 30 phút", "2 giờ");
        btn_nav = findViewById(R.id.btn_navi);
        btn_noti = findViewById(R.id.btn_noti);
        navigationView = findViewById(R.id.navi_view);
        navigationView.bringToFront();
        Menu menuNav = navigationView.getMenu();
        spinner = (Spinner) menuNav.findItem(R.id.nav_timer).getActionView();
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, timer));
        name = findViewById(R.id.main_songname);
        search = findViewById(R.id.main_search);
        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    search.setSelected(true);
                    // Do whatever you want here
                } else {
                    search.requestFocus();
                }
            }
        });
        databaseHelper = new DatabaseHelper(MainActivity.this);
        myListViewForSongs = (ListView) findViewById(R.id.main_listSong);
        btn_next = (Button) findViewById(R.id.main_next);
        btn_pause = (Button) findViewById(R.id.main_pause);
        btn_previous = (Button) findViewById(R.id.main_previous);
        curDur = (TextView) findViewById(R.id.main_curdur);  // current DUration vị trí hiện tại
//        btn_repeat=(Button)findViewById(R.id.repeat);
//        btn_shuffle=(Button)findViewById(R.id.shuffle); //ngẫu nhiên
        Dur = (TextView) findViewById(R.id.main_dur);
        songSeekbar = (SeekBar) findViewById(R.id.main_seekBar);
        ArrayList<Song> listsong = databaseHelper.getAllSongs();
        for (int i = 0;i<listsong.size();i++){
            if (listsong.get(i).getStatus()==1) btn_noti.setBackgroundResource(R.drawable.bellred);
        }
//        updateseekBar = new Thread() {
//            @Override
//            public void run() {
//                int totalDuration = myMediaPlayer.getDuration();
//                int currentPosition = 0;
//                songSeekbar.setProgress(currentPosition);
//                while (currentPosition < totalDuration - 200) {
//                    try {
//                        sleep(500);
//                        currentPosition = myMediaPlayer.getCurrentPosition();
//                        songSeekbar.setProgress(currentPosition);
//                    } catch (Exception e) {
//                        currentPosition = 0;
//                        songSeekbar.setProgress(currentPosition);
//                    }
//                }
//
//            }
//        };
        try {
            if (myMediaPlayer!=null) {
//                sname = mySongs.get(position).getName().toString().replace(".mp3", "").replace(".wav", "");
                sname = items[position];
                btn_pause.setBackgroundResource(R.drawable.ic_pause);
                name.setText(sname);
                SetTimeTotal();
                UpdaTime();

            }
        } catch (Exception e) {

        }


    }

    private void performSearch(String searchtxt) {
        issearch = true;
        songsearch = new ArrayList<File>();
        for (int i = 0; i < mySongs.size(); i++) {
            if (mySongs.get(i).getName().toLowerCase().contains(searchtxt.toLowerCase())) {
                songsearch.add(mySongs.get(i));
            }
        }
        items = new String[songsearch.size()];

        for (int i = 0; i < songsearch.size(); i++) {
            items[i] = songsearch.get(i).getName().toString().replace(".mp3", "").replace(".wav", "");
        }
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        myListViewForSongs.setAdapter(myAdapter);
    }

    private void setEvent() {
        btn_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(Gravity.RIGHT);
            }
        });
        btn_noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Thongbao.class);
                startActivity(intent);
                finish();
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_favor: {

                        return true;
                    }
                    case R.id.nav_online: {
                        try {
                            if (myMediaPlayer.isPlaying()) {
                                myMediaPlayer.stop();
                                myMediaPlayer.release();
                                myMediaPlayer = null;
                            }
                        } catch (Exception e) {
                            Log.v("-------------", e.toString());
                        }
                        ;
                        Intent intent = new Intent(MainActivity.this, Online.class);
                        startActivity(intent);
                        finish();
                        return true;
                    }
                    case R.id.nav_upload: {

                        Intent intent = new Intent(MainActivity.this, Upload.class);
                        startActivity(intent);
                        finish();
                        return true;
                    }

                }
                return false;
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this,timer.get(position),Toast.LENGTH_SHORT).show();
                Timer(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                performSearch(search.getText().toString());

            }
        });
        myListViewForSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                try {
                    myMediaPlayer.pause();
                } catch (Exception e) {
                }
                sname = items[i];
                u = Uri.parse(mySongs.get(i).toString());
                if (issearch) {
                    u = Uri.parse(songsearch.get(i).toString());
                }
                position = i;
                myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                myMediaPlayer.start();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        name.setText(sname);
                        name.setSelected(true);
                        btn_pause.setBackgroundResource(R.drawable.ic_pause);
//                        songSeekbar.setMax(myMediaPlayer.getDuration());
                        SetTimeTotal();
                        UpdaTime();
                    }
                });
            }
        });


        songSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myMediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (myMediaPlayer == null) {
                        myMediaPlayer = new MediaPlayer();
                    }
                } catch (Exception e) {

                }
                if (myMediaPlayer.isPlaying()) {
                    songSeekbar.setMax(myMediaPlayer.getDuration());
                    btn_pause.setBackgroundResource(R.drawable.ic_play);
                    myMediaPlayer.pause();
                } else {
                    if (u != null) {
                        btn_pause.setBackgroundResource(R.drawable.ic_pause);
                        myMediaPlayer.start();
                    } else {
                        //        sname = mySongs.get(position).getName().toString().replace(".mp3", "").replace(".wav", "");
                        sname = items[0];
                        u = Uri.parse(mySongs.get(0).toString());
                        if (issearch) {
                            u = Uri.parse(songsearch.get(0).toString());
                        }
                        position = 0;
                        myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);
//                        myMediaPlayer.setLooping(true);
                        myMediaPlayer.start();
                        name.setText(sname);
                        name.setSelected(true);
                        btn_pause.setBackgroundResource(R.drawable.ic_pause);
                        SetTimeTotal();
                        UpdaTime();
                    }

                }
                SetTimeTotal();
                UpdaTime();
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                function_next();

            }
        });
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                function_previous();
            }
        });
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!name.getText().equals("")) {
                    startActivity(new Intent(getApplicationContext(), PlayerActivity.class)
                            .putExtra("songs", mySongs).putExtra("songname", sname));
                    finish();
                }
            }
        });

    }

    private void Timer(int position) {

        switch (position) {
            case 0: {
                mTimer = false;
                break;
            }

            case 1: {
                time_left = 30 * 100;//60*1000;
                mTimer = true;
                break;
            }
            case 2: {
                time_left = 60 * 100;//60*1000;
                mTimer = true;
                break;
            }
            case 3: {
                time_left = 90 * 100;//60*1000;
                mTimer = true;
                break;
            }
            case 4: {
                time_left = 120 * 100;//60*1000;
                mTimer = true;
                break;
            }
        }
        if (mTimer) {
            Toast.makeText(this, "Đã hẹn giờ", Toast.LENGTH_SHORT).show();
            countDownTimer = new CountDownTimer(time_left, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    time_left = millisUntilFinished;
                }

                @Override
                public void onFinish() {
                    finish();
                    System.exit(0);
                }
            }.start();
        }
    }

    public void function_next() {
        try {
            if (myMediaPlayer == null) {
                myMediaPlayer = new MediaPlayer();
                position = -1;
            }
        } catch (Exception e) {

        }
//        myMediaPlayer.stop();
//        myMediaPlayer.reset();
//        myMediaPlayer.release();
        myMediaPlayer.pause();
        int bb = 1;
        if (shuff == 1) {
            Random rd = new Random();
            bb = rd.nextInt(mySongs.size() - 1);
        } else bb = 1;
        if (rept==1) bb=0;
        position = ((position + bb) % mySongs.size());
        u = Uri.parse(mySongs.get(position).toString());
        myMediaPlayer = MediaPlayer.create(this, u);
        sname = items[position];
        myMediaPlayer.start();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                name.setText(sname);
                btn_pause.setBackgroundResource(R.drawable.ic_pause);
                SetTimeTotal();
            }
        });
        UpdaTime();
    }

    public void function_previous() {
        try {
            if (myMediaPlayer == null) {
                myMediaPlayer = new MediaPlayer();
                position = 1;
            }
        } catch (Exception e) {

        }
        myMediaPlayer.stop();
//        myMediaPlayer.reset();
        myMediaPlayer.release();
        position = ((position - 1) < 0) ? (mySongs.size() - 1) : (position - 1);
        u = Uri.fromFile(new File(mySongs.get(position).toString()));
        myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);
//        sname = mySongs.get(position).getName().toString().replace(".mp3", "").replace(".wav", "");
        sname = items[position];
        name.setText(sname);
        myMediaPlayer.start();
        btn_pause.setBackgroundResource(R.drawable.ic_pause);
        SetTimeTotal();
        UpdaTime();
    }

    public void runtimePermison() {

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        display();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public ArrayList<File> findSong(File file) {

        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        for (File singleFile : files) {

            if (singleFile.isDirectory() && !singleFile.isHidden()) {
                arrayList.addAll(findSong(singleFile));
            } else {
                if (singleFile.getName().endsWith(".mp3") ||
                        singleFile.getName().endsWith(".wav")) {
                    arrayList.add(singleFile);
                }
            }

        }
        return arrayList;
    }

    void display() {

        mySongs = findSong(Environment.getExternalStorageDirectory());

        items = new String[mySongs.size()];

        for (int i = 0; i < mySongs.size(); i++) {
            items[i] = mySongs.get(i).getName().toString().replace(".mp3", "").replace(".wav", "");

            try {
                Mp3File mp3file = new Mp3File(mySongs.get(i));
                if (mp3file.hasId3v1Tag()) {
                    ID3v1 id3v1Tag = mp3file.getId3v1Tag();
                    if (!id3v1Tag.getTitle().equals(""))
                        items[i] = id3v1Tag.getTitle();
                }
                if (mp3file.hasId3v2Tag()) {
                    ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                    if (!id3v2Tag.getTitle().equals(""))
                        items[i] = id3v2Tag.getTitle();
                }
            } catch (Exception e) {
                Log.v("ABC", e.toString());
            }
        }
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        myListViewForSongs.setAdapter(myAdapter);

    }

    private void SetTimeTotal() {
        int b = myMediaPlayer.getDuration();
        String a = "";
        int hours = (int) (b / (1000 * 60 * 60));
        int minu = (int) (b % (1000 * 60 * 60) / (1000 * 60));
        int sec = (int) ((b % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hours > 0) {
            a = a + hours + ":";
        }

        if (minu < 10) {
            a = a + "0" + minu + ":";
        } else {
            a = a + "" + minu + ":";
        }
        if (sec < 10) {
            a = a + "0" + sec;
        } else {
            a = a + "" + sec;
        }
        Dur.setText(a);
        songSeekbar.setMax(myMediaPlayer.getDuration());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void UpdaTime() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                    try {
                        int c = myMediaPlayer.getDuration();
                        int b = myMediaPlayer.getCurrentPosition();

                        if (b>c-200){
                            function_next();
                        }
                        String a = "";
                        int hours = (int) (b / (1000 * 60 * 60));
                        int minu = (int) (b % (1000 * 60 * 60) / (1000 * 60));
                        int sec = (int) ((b % (1000 * 60 * 60)) % (1000 * 60) / 1000);

                        if (hours > 0) {
                            a = a + hours + ":";
                        }
                        if (minu < 10) {
                            a = a + "0" + minu + ":";
                        } else {
                            a = a + "" + minu + ":";
                        }
                        if (sec < 10) {
                            a = a + "0" + sec;
                        } else {
                            a = a + "" + sec;
                        }
                        curDur.setText(a);
                        songSeekbar.setProgress(b);
                    }catch (Exception e){

                    }
                handler.postDelayed(this, 500);
            }
        }, 100);
    }
}
