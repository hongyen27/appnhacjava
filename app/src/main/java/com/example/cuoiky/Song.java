package com.example.cuoiky;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.Comparator;

public class Song implements Serializable {
    private int id;
    private String name;
    private String artist;
    private String url;
    private int count;
    private int status;



    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Song(){

    }
    public Song(int id ,String name, String artist, String url, int count, int status) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.url = url;
        this.count = count;
        this.status=status;
    }
    public static Comparator<Song> SongNewCom = new Comparator<Song>() {
        @Override
        public int compare(Song p1, Song p2) {
            return  p2.getId()-p1.getId();
        }
    };

    public static Comparator<Song> SongHotCom = new Comparator<Song>() {
        @Override
        public int compare(Song p1, Song p2) {
            return  p2.getCount()-p1.getCount();
        }
    };
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
