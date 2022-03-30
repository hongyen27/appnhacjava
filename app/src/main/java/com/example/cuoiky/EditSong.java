package com.example.cuoiky;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditSong extends AppCompatActivity {
    Button edit,dele;
    EditText songname,artist;
    Song song;
    DatabaseHelper databaseHelper;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);
        intent = getIntent();
        setControl();
        setEvent();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent1 = new Intent(EditSong.this,Online.class);
        startActivity(intent1);
        finish();
    }

    private void setEvent() {
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                song.setName(songname.getText().toString());
                song.setArtist(artist.getText().toString());
                databaseHelper.updateSong(String.valueOf(song.getId()),song);
                Toast.makeText(EditSong.this, "Đã thay đổi thông tin", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(EditSong.this,Online.class);
                startActivity(intent1);
                finish();
            }
        });
        dele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHelper.deleteSong(String.valueOf(song.getId()));
                Toast.makeText(EditSong.this, "Đã xóa bài hát", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(EditSong.this,Online.class);
                startActivity(intent1);
                finish();
            }
        });
    }

    private void setControl() {

        edit = findViewById(R.id.edit_edit);
        dele = findViewById(R.id.edit_dele);
        songname = findViewById(R.id.edit_songname);
        artist = findViewById(R.id.edit_artistname);
        databaseHelper = new DatabaseHelper(EditSong.this);
        song = databaseHelper.get1Song(intent.getStringExtra("id"));
        songname.setText(song.getName());
        artist.setText(song.getArtist());
    }
}
