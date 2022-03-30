package com.example.cuoiky;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance;

    public DatabaseHelper(@Nullable Context context) {
        super(context, "song.db", null, 1);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase songdb) {

        String song = "CREATE TABLE Song(ID INTEGER PRIMARY KEY AUTOINCREMENT, Name NVARCHAR(50) NOT NULL, Artist NVARCHAR(50),  Url TEXT NOT NULL, Count IINTEGER NOT NULL, Status BIT NOT NULL)\n";
        String favorite = "CREATE TABLE Favorite(Url TEXT NOT NULL)";
        songdb.execSQL(song);
        songdb.execSQL(favorite);
    }


    public boolean updateSong(String id, Song song) {
        SQLiteDatabase db = this.getWritableDatabase();
        String name = song.getName();
        String artist = song.getArtist();
        String count = String.valueOf(song.getCount());
        String status = String.valueOf(song.getStatus());
        String update = "UPDATE Song SET Name = '" + name + "', Artist = '" + artist +  "', Count = '" + count + "' , Status = '" + status +"'  WHERE ID = '" + id + "'";
        Cursor cursor1 = db.rawQuery(update, null);
        if (cursor1.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }
    public boolean deleteSong(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String querryString = "DELETE FROM Song WHERE ID = '" + id + "'";
        Cursor cursor = db.rawQuery(querryString, null);
        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }
    public boolean insertFavorite(String url) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("URL", url);
        long insert = sqLiteDatabase.insert("Favorite", null, contentValues);
        sqLiteDatabase.close();
        if (insert == -1) return false;
        return true;
    }

    public boolean deleteFavorite(String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        String querryString = "DELETE FROM Favorite WHERE Url = '" + url + "'";
        Cursor cursor = db.rawQuery(querryString, null);
        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<String> getAllFavoriteSongs() {
        ArrayList<String> List = new ArrayList<>();
        String query = "SELECT Url FROM Favorite";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                List.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return List;
    }

    public boolean isFavorite(String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        String querryString = "SELECT Url FROM Favorite WHERE Url = '" + url + "'";
        Cursor cursor = db.rawQuery(querryString, null);
        if (cursor.moveToFirst()) {
            return true;
        }
        return false;
    }

    public ArrayList<Song> getAllSongs() {
        ArrayList<Song> List = new ArrayList<>();
        String query = "SELECT * FROM Song";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Song song = new Song();
                song.setId(cursor.getInt(0));
                song.setName(cursor.getString(1));
                song.setArtist(cursor.getString(2));
                song.setUrl(cursor.getString(3));
                song.setCount(cursor.getInt(4));
                song.setStatus(cursor.getInt(5));
                List.add(0,song);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return List;
    }
    public Song get1Song(String index){
        Song song = new Song();
        String query = "SELECT * FROM Song WHERE ID = '"+index+"'";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            song.setId(cursor.getInt(0));
            song.setName(cursor.getString(1));
            song.setArtist(cursor.getString(2));
            song.setUrl(cursor.getString(3));
            song.setCount(cursor.getInt(4));
        }
        return song;
    }
    public boolean insertSong(Song song) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Name", song.getName());
        contentValues.put("Artist", song.getArtist());
        contentValues.put("Url", song.getUrl());
        contentValues.put("Count",0);
        contentValues.put("Status",1);
        long insert = sqLiteDatabase.insert("Song", null, contentValues);
        sqLiteDatabase.close();
        if (insert == -1) return false;
        return true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
