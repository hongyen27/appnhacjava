package com.example.cuoiky;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

public class OnlineAdapter extends ArrayAdapter {

    Context context;
    int resource;
    DatabaseHelper databaseHelper;
    ArrayList<Song> data;

    public OnlineAdapter(Context context, int resource, ArrayList data) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.data = data;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(context)
                .inflate(R.layout.online_item, null);

        ImageView im = convertView.findViewById(R.id.onl_image);
        TextView tvTen = convertView.findViewById(R.id.onlitem_name);
        TextView tvArt = convertView.findViewById(R.id.onlitem_artist);
        TextView tvSoLg = convertView.findViewById(R.id.onlitem_luot);

        Song song = new Song();
        databaseHelper = new DatabaseHelper(context);
        song = databaseHelper.get1Song(String.valueOf(data.get(position).getId()));
        tvArt.setText(song.getArtist());
        tvTen.setText(song.getName());
        tvSoLg.setText("Lượt nghe: " + String.valueOf(song.getCount()));
        im.setBackgroundResource(R.drawable.unes);

        if (song.getArtist().equals("Davichi")) Glide.with(context).load("https://firebasestorage.googleapis.com/v0/b/cuoiky-7ac89.appspot.com/o/image%2FDavichi.jpg?alt=media&token=bdf8a7be-a720-48e6-85fe-974e8ebc33f0").into(im);

        return convertView;
    }

}
