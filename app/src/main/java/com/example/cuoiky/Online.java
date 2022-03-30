package com.example.cuoiky;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Online extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    Song song;
    DatabaseHelper databaseHelper;
    ArrayList<Song> songlist;
    ListView lv;
    TextView name;
    EditText search;
    Button btn_next, btn_previous, btn_pause, btn_repeat, btn_shuffle,btn_menu,btn_nav;
    SeekBar songSeekbar;
    TextView curDur;
    TextView Dur;
    String[] items;
    List<String> timer;
    NavigationView navigationView;
    Thread updateseekBar;

    Spinner spinner;
    MenuItem Item;
    ArrayList<Song> songsearch;
    boolean issearch=false;
    OnlineAdapter adapter;
    static int onlposition;
    static MediaPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online);
        setControl();
        setEvent();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_menu,menu);
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

            try {
                if (mPlayer.isPlaying())
                {
                mPlayer.stop();
                mPlayer.release();
                mPlayer = null;
                }
            }
            catch (Exception e){
            };
        Intent intent1 = new Intent(Online.this,MainActivity.class);
        startActivity(intent1);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case  R.id.neww:
                Collections.sort(songlist,Song.SongNewCom);
//                for (int i = 0; i < songlist.size(); i++) {
//                    items[i] = songlist.get(i).getName();
//                }
//                ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
                adapter=new OnlineAdapter(this,R.layout.online_item,songlist);
                lv.setAdapter(adapter);
                Toast.makeText(this, "Sắp xếp mới nhất", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.hott:
                Collections.sort(songlist,Song.SongHotCom);
//                for (int i = 0; i < songlist.size(); i++) {
//                    items[i] = songlist.get(i).getName();
//                }
//                myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
                adapter=new OnlineAdapter(this,R.layout.online_item,songlist);
                lv.setAdapter(adapter);
                Toast.makeText(this, "Sắp xếp hot nhất", Toast.LENGTH_SHORT).show();
//                Toast.makeText(this, String.valueOf(productsList.get(0).getCount()), Toast.LENGTH_SHORT).show();
                return true;
        }

        return true;
    }
    private void setEvent() {

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
        btn_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(Gravity.RIGHT);
            }
        });
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songSeekbar.setMax(mPlayer.getDuration());
                if (mPlayer.isPlaying()) {
                    btn_pause.setBackgroundResource(R.drawable.ic_play);
                    mPlayer.pause();
                } else {
                    btn_pause.setBackgroundResource(R.drawable.ic_pause);
                    mPlayer.start();
                }
                SetTimeTotal();
                UpdaTime();
            }
        });
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Online.this, "Loading...", Toast.LENGTH_SHORT).show();
                try {
                    mPlayer.stop();
                    mPlayer.release();

                } catch (Exception e) {

                }
                try {
                    int b = 1;
                    onlposition -=b ;
                    song=songlist.get(onlposition);
                    name.setText(song.getName());
                    song.setCount(song.getCount()+1);
                    databaseHelper.updateSong(String.valueOf(song.getId()),song);
                    mPlayer = new MediaPlayer();
                    mPlayer.setDataSource(song.getUrl());
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mPlayer.prepare();
                    mPlayer.setLooping(true);
                    mPlayer.start();
                    btn_pause.setBackgroundResource(R.drawable.ic_pause);
                    SetTimeTotal();
                    UpdaTime();
                    updateseekBar.start();
                } catch (Exception e) {
                }
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Online.this, "Loading...", Toast.LENGTH_SHORT).show();
                try {
                    mPlayer.stop();
                    mPlayer.release();

                } catch (Exception e) {

                }
                try {
                    int b = 1;
                    onlposition +=b ;
                    song=songlist.get(onlposition);
                    name.setText(song.getName());
                    song.setCount(song.getCount()+1);
                    databaseHelper.updateSong(String.valueOf(song.getId()),song);
                    mPlayer = new MediaPlayer();
                    mPlayer.setDataSource(song.getUrl());
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mPlayer.prepare();
                    mPlayer.setLooping(true);
                    mPlayer.start();
                    btn_pause.setBackgroundResource(R.drawable.ic_pause);
                    SetTimeTotal();
                    UpdaTime();
                    updateseekBar.start();
                } catch (Exception e) {
                }
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

                mPlayer.seekTo(seekBar.getProgress());
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case  R.id.nav_favor:
                    {

                        return true;
                    }
                    case R.id.nav_online:
                    {
                            try {
                                if (mPlayer.isPlaying()){
                                mPlayer.stop();
                                mPlayer.release();
                                mPlayer = null;
                                }
                            }
                            catch (Exception e){

                            }
                        Intent intent = new Intent(Online.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    }
                    case R.id.nav_upload: {

                        Intent intent = new Intent(Online.this, Upload.class);
                        startActivity(intent);
                        finish();
                        return true;
                    }
                    case R.id.nav_hang:{
                        Intent intent = new Intent(Online.this, Barchart.class);
                        startActivity(intent);
                        finish();
                        return true;
                    }
                }
                return false;
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(Online.this, "Loading...", Toast.LENGTH_SHORT).show();
                try {
                    mPlayer.stop();
                    mPlayer.release();
                } catch (Exception e) {

                }
                try {

                    song=songlist.get(position);
                    name.setText(song.getName());
                    onlposition=position;
                    song.setCount(song.getCount()+1);
                    databaseHelper.updateSong(String.valueOf(song.getId()),song);
                    mPlayer = new MediaPlayer();
                    mPlayer.setDataSource(song.getUrl());
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mPlayer.prepare();
                    mPlayer.setLooping(true);
                    mPlayer.start();
                    btn_pause.setBackgroundResource(R.drawable.ic_pause);
                    SetTimeTotal();
                    UpdaTime();
                    updateseekBar.start();
                } catch (Exception e) {
                }
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    if (mPlayer.isPlaying()){
                        mPlayer.stop();
                        mPlayer.release();
                        mPlayer = null;
                    }
                }

                catch (Exception e){

                };
                song=songlist.get(position);
                Intent intent = new Intent(Online.this,EditSong.class);
                intent.putExtra("id",String.valueOf(song.getId()));
                startActivity(intent);
                finish();
                return false;
            }
        });
        updateseekBar = new Thread() {
            @Override
            public void run() {
                int totalDuration = mPlayer.getDuration();
                int currentPosition = 0;
                while (currentPosition < totalDuration) {
                    try {
                        sleep(500);
                        currentPosition = mPlayer.getCurrentPosition();
                        songSeekbar.setProgress(currentPosition);
                    } catch (Exception e) {

                    }
                }

            }
        };

    }
    private void performSearch(String searchtxt) {
        issearch=true;
        songsearch = new ArrayList<Song>();
        for (int i = 0;i<songlist.size();i++){
            if (songlist.get(i).getName().toLowerCase().contains(searchtxt.toLowerCase())){
                songsearch.add(songlist.get(i));
            }
        }

//        for (int i = 0; i < songsearch.size(); i++) {
//            items[i] = songsearch.get(i).getName();
//        }
//        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        adapter = new OnlineAdapter(this, R.layout.online_item,songsearch);
        lv.setAdapter(adapter);

    }
    private void setControl() {
        timer = Arrays.asList("none","30 phút","1 giờ","1 giờ 30 phút","2 giờ");
        btn_nav = findViewById(R.id.btn_navi);
        navigationView = findViewById(R.id.navi_view);
        navigationView.bringToFront();
        Menu menuNav = navigationView.getMenu();
        spinner = (Spinner) menuNav.findItem(R.id.nav_timer).getActionView();
        spinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,timer));
        name = findViewById(R.id.onl_songname);
//        btn_menu=findViewById(R.id.main_options);
        search = findViewById(R.id.main_search);
        btn_next = (Button) findViewById(R.id.onl_next);
        btn_pause = (Button) findViewById(R.id.onl_pause);
        btn_previous = (Button) findViewById(R.id.onl_previous);
        curDur = (TextView) findViewById(R.id.main_curdur);  // current DUration vị trí hiện tại
//        btn_repeat=(Button)findViewById(R.id.repeat);
//        btn_shuffle=(Button)findViewById(R.id.shuffle); //ngẫu nhiên
        Dur = (TextView) findViewById(R.id.main_dur);
        songSeekbar = (SeekBar) findViewById(R.id.main_seekBar);
        databaseHelper = new DatabaseHelper(Online.this);

        lv = findViewById(R.id.onl_listSong);
        songlist = new ArrayList<Song>();
        songlist = databaseHelper.getAllSongs();
        adapter=new OnlineAdapter(this,R.layout.online_item,songlist);
        lv.setAdapter(adapter);

        try {
            if (mPlayer.isPlaying()) {
                btn_pause.setBackgroundResource(R.drawable.ic_pause);
                name.setText(songlist.get(onlposition).getName());
                SetTimeTotal();
                UpdaTime();
                updateseekBar = new Thread() {
                    @Override
                    public void run() {
                        int totalDuration = mPlayer.getDuration();
                        int currentPosition = 0;
                        while (currentPosition < totalDuration) {
                            try {
                                sleep(500);
                                currentPosition = mPlayer.getCurrentPosition();
                                songSeekbar.setProgress(currentPosition);
                            } catch (Exception e) {
                            }
                        }
                    }
                };
                updateseekBar.start();
            }
        } catch (Exception e) {
        }

    }
    private void UpdaTime() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    int c = mPlayer.getDuration();
                    int b = mPlayer.getCurrentPosition();
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
                    handler.postDelayed(this, 500);
                }catch (Exception e){

                }
//                if (b > (c - 100)) {
//                    mPlayer.stop();
//                    mPlayer.release();
//                    mPlayer = null;
//                    int bb = 1;
//                    if (rept == 1) bb = 0;
//                    if (shuff == 1) {
//                        bb=0;
//                        Random rd = new Random();
//                        bb = rd.nextInt(MainActivity.mySongs.size() - 1);
//                    } else bb = 1;
//                    MainActivity.position = (MainActivity.position + bb) % MainActivity.mySongs.size();
//                    Uri u = Uri.parse(MainActivity.mySongs.get(MainActivity.position).toString());
//                    mPlayer = MediaPlayer.create(getApplicationContext(), u);
//                    sname = MainActivity.mySongs.get(MainActivity.position).getName().toString().replace(".mp3", "").replace(".wav", "");
//                    songTextLabel.setText(sname);
//                    mPlayer.start();
//                    SetTimeTotal();
//                    UpdaTime();
//                    updateseekBar.start();
//                }

            }
        }, 100);
    }
    private void SetTimeTotal() {
        int b = mPlayer.getDuration();
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
        songSeekbar.setMax(mPlayer.getDuration());
    }

}
