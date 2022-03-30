package com.example.cuoiky;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Thongbao extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    ListView lv;
    Button viewAll;
    ArrayList<Song> listsong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thongbao);

        setControl();
        setEvent();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Thongbao.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setEvent() {
        ThongbaoAdapter thongbaoAdapter = new ThongbaoAdapter(Thongbao.this,R.layout.thongbaoadapter1,listsong);
        lv.setAdapter(thongbaoAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (MainActivity.myMediaPlayer!=null)
                {
                    MainActivity.myMediaPlayer.stop();
                    MainActivity.myMediaPlayer.release();
                    MainActivity.myMediaPlayer=null;
                }
                Song song = listsong.get(position);
                song.setStatus(0);
                databaseHelper.updateSong(String.valueOf(song.getId()),song);
                Intent intent = new Intent(Thongbao.this,Online.class);
                startActivity(intent);
                finish();
            }
        });
        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0;i<listsong.size();i++){
                    Song song = listsong.get(i);
                    if (song.getStatus()==0) continue;
                    song.setStatus(0);
                    databaseHelper.updateSong(String.valueOf(song.getId()),song);
                }
                Intent intent = new Intent(Thongbao.this,Online.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setControl() {
        lv = findViewById(R.id.noti_list);
        databaseHelper = new DatabaseHelper(Thongbao.this);
        listsong = databaseHelper.getAllSongs();
        viewAll = findViewById(R.id.noti_all);
    }
}
