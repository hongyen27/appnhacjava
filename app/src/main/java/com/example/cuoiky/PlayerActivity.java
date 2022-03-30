package com.example.cuoiky;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.lang.Integer.parseInt;
import static java.lang.Thread.sleep;

public class PlayerActivity extends AppCompatActivity {

    Button btn_next, btn_previous, btn_pause, btn_repeat, btn_shuffle, btn_favo;
    TextView songTextLabel,lyric;
    SeekBar songSeekbar;
    String sname;
    Spinner spinner,spinner2;
    TextView curDur;
    TextView Dur;
    ImageView image;
    List<String> timer;
    long mise;
    int rept = 0;     //repeat
    int shuff = 0; //shuffle
    int cong = 1; //cong position
    int congs = 1;
    boolean isFavorite;
    NavigationView navigationView;
    DatabaseHelper databaseHelper;

    //    ArrayList<File> MainActivity.mySongs;
//    Thread updateseekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        setControl();
        setEvent();
    }

    private void setEvent() {
        songTextLabel.setText(MainActivity.sname);
        songTextLabel.setSelected(true);
        songSeekbar.setMax(MainActivity.myMediaPlayer.getDuration());
        setImg();
//        rotateTheDisk();
        SetTimeTotal();
        UpdaTime();
        songSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                MainActivity.myMediaPlayer.seekTo(seekBar.getProgress());
            }
        });


        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songSeekbar.setMax(MainActivity.myMediaPlayer.getDuration());
                if (MainActivity.myMediaPlayer.isPlaying()) {
                    btn_pause.setBackgroundResource(R.drawable.ic_play);
                    MainActivity.myMediaPlayer.pause();
                } else {
                    btn_pause.setBackgroundResource(R.drawable.ic_pause);
                    MainActivity.myMediaPlayer.start();
                    rotateTheDisk();
                }
                SetTimeTotal();
                UpdaTime();
            }
        });
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(PlayerActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                function_next();
            }
        });

        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image.animate().setDuration(500).rotation(-5).start();

                MainActivity.myMediaPlayer.pause();
                MainActivity.position = ((MainActivity.position - 1) < 0) ? (MainActivity.mySongs.size() - 1) : (MainActivity.position - 1);
                Uri u = Uri.parse(MainActivity.mySongs.get(MainActivity.position).toString());
                MainActivity.myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                sname = MainActivity.items[MainActivity.position];
                songTextLabel.setText(sname);
                MainActivity.myMediaPlayer.start();
                setImg();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        songTextLabel.setText(sname);
                        btn_pause.setBackgroundResource(R.drawable.ic_pause);
                        SetTimeTotal();
                    }
                });
                UpdaTime();
            }
        });
        btn_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rept == 0) {
                    rept = 1;
                    btn_repeat.setBackgroundResource(R.drawable.ic_repeat2);
                } else {
                    rept = 0;
                    btn_repeat.setBackgroundResource(R.drawable.ic_repeat);
                }
            }
        });
        btn_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shuff == 0) {
                    shuff = 1;
                    btn_shuffle.setBackgroundResource(R.drawable.ic_shuffle2);
                } else {
                    shuff = 0;
                    btn_shuffle.setBackgroundResource(R.drawable.ic_shuffle);
                }
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Timer(position);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        btn_favo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite) {
                    databaseHelper.deleteFavorite(MainActivity.mySongs.get(MainActivity.position).getAbsolutePath());
                    btn_favo.setBackgroundResource(R.drawable.blc);
                    isFavorite = false;
                    Toast.makeText(PlayerActivity.this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                } else {
                    databaseHelper.insertFavorite(MainActivity.mySongs.get(MainActivity.position).getAbsolutePath());
                    btn_favo.setBackgroundResource(R.drawable.red);
                    isFavorite = true;
                    Toast.makeText(PlayerActivity.this, "Added to Favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void function_next() {
        image.animate().setDuration(500).rotation(-5).start();
        MainActivity.myMediaPlayer.pause();
        int bb = 1;
        if (shuff == 1) {
            Random rd = new Random();
            bb = rd.nextInt(MainActivity.mySongs.size() - 1);
        } else bb = 1;
        MainActivity.position = ((MainActivity.position + bb) % MainActivity.mySongs.size());
        Uri u = Uri.parse(MainActivity.mySongs.get(MainActivity.position).toString());
        MainActivity.myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);
//                sname = MainActivity.mySongs.get(position).getName().toString().replace(".mp3", "").replace(".wav", "");
        sname = MainActivity.items[MainActivity.position];
        MainActivity.myMediaPlayer.start();
        setImg();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                songTextLabel.setText(sname);
                btn_pause.setBackgroundResource(R.drawable.ic_pause);
                SetTimeTotal();
            }
        });
        UpdaTime();
    }

    private void setControl() {
        timer = Arrays.asList("none","30p","60p","90p","120p");
        btn_next = (Button) findViewById(R.id.next);
        btn_favo = findViewById(R.id.player_fav);
        btn_pause = (Button) findViewById(R.id.pause);
        btn_previous = (Button) findViewById(R.id.previous);
        songTextLabel = (TextView) findViewById(R.id.songLabel); //
        curDur = (TextView) findViewById(R.id.tv_curdur);  // current DUration vị trí hiện tại
        btn_repeat = (Button) findViewById(R.id.repeat);
        btn_shuffle = (Button) findViewById(R.id.shuffle); //ngẫu nhiên
        Dur = (TextView) findViewById(R.id.tv_dur);
        image = findViewById(R.id.image);
        lyric = findViewById(R.id.lyric);
        navigationView = findViewById(R.id.navi_view);
        spinner = findViewById(R.id.player_spin);
        spinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,timer));
        databaseHelper = new DatabaseHelper(PlayerActivity.this);


        songSeekbar = (SeekBar) findViewById(R.id.seekBar);
//        Toast.makeText(this, String.valueOf(MainActivity.position), Toast.LENGTH_SHORT).show();
        getSupportActionBar().setTitle("Now Playing");
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        updateseekBar = new Thread() {
//            @Override
//            public void run() {
//                int totalDuration =MainActivity.myMediaPlayer.getDuration();
//                int currentPosition = 0;
//
//                while (currentPosition < totalDuration - 200) {
//                    try {
//                        sleep(500);
//                        currentPosition = MainActivity.myMediaPlayer.getCurrentPosition();
//                        songSeekbar.setProgress(currentPosition);
//
//
//                    } catch (Exception e) {
//                    }
//                }
//            }
//        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent1 = new Intent(PlayerActivity.this,MainActivity.class);
        startActivity(intent1);
        finish();
    }
    private void Timer(int position) {

        switch (position){
            case 0: {
                MainActivity.mTimer=false;
                break;
            }

            case 1: {
                MainActivity.time_left=30*100;//60*1000;
                MainActivity.mTimer=true;
                break;
            }
            case 2: {
                MainActivity.time_left=60*100;//60*1000;
                MainActivity.mTimer=true;
                break;
            }
            case 3: {
                MainActivity.time_left=90*100;//60*1000;
                MainActivity.mTimer=true;
                break;
            }
            case 4: {
                MainActivity.time_left=120*100;//60*1000;
                MainActivity.mTimer=true;
                break;
            }
        }
        if (MainActivity.mTimer){
            Toast.makeText(this, "Đã hẹn giờ", Toast.LENGTH_SHORT).show();
            MainActivity.countDownTimer = new CountDownTimer(MainActivity.time_left,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    MainActivity.time_left=millisUntilFinished;
                }

                @Override
                public void onFinish() {
                    finish();
                    System.exit(0);
                }
            }.start();
        }
    }
    public void setLyric(){
        lyric.setText("Lyrics not found");
        Uri u = Uri.parse(MainActivity.mySongs.get(MainActivity.position).toString());
        File file = new File(u.toString());
        try {
            Mp3File mp3file = new Mp3File(file);
            if (mp3file.hasId3v1Tag()) {
                ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                if (!id3v2Tag.getLyrics().equals(""))
                    lyric.setText(id3v2Tag.getLyrics());
//                Toast.makeText(this,id3v2Tag.getLyrics(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        }

    }
    public void setImg(){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        Uri u = Uri.parse(MainActivity.mySongs.get(MainActivity.position).toString());
        mmr.setDataSource(String.valueOf(u));
        byte [] data = mmr.getEmbeddedPicture();
        if(data != null)
        {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            image.setImageBitmap(bitmap);
        }
        else image.setImageResource(R.drawable.unes);
        rotateTheDisk();
        setLyric();
        isFavorite = databaseHelper.isFavorite(MainActivity.mySongs.get(MainActivity.position).getAbsolutePath());
        if (isFavorite) {
            btn_favo.setBackgroundResource(R.drawable.red);
        } else {
            btn_favo.setBackgroundResource(R.drawable.blc);
        }
    }

    private void rotateTheDisk() {
        if (!MainActivity.myMediaPlayer.isPlaying()) return;
        image.animate().setDuration(10).rotation(image.getRotation() + 0.5f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                rotateTheDisk();
                super.onAnimationEnd(animation);
            }
        });

    }


    private void SetTimeTotal() {
        int b = MainActivity.myMediaPlayer.getDuration();
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
        songSeekbar.setMax(MainActivity.myMediaPlayer.getDuration());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void UpdaTime() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    int c = MainActivity.myMediaPlayer.getDuration();
                    int b = MainActivity.myMediaPlayer.getCurrentPosition();
                    if (b > (c - 100)) {
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
