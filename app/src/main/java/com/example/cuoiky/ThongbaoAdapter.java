package com.example.cuoiky;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ThongbaoAdapter extends ArrayAdapter<Song> {

    private Context context;
    public  ArrayList<Song> arrayList;
    private int resource;


    public ThongbaoAdapter(Context context, int resource, ArrayList<Song> arrayList) {
        super(context, resource, arrayList);
        this.context = context;
        this.resource = resource;
        this.arrayList = arrayList;
    }


    @NonNull
    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(resource, null);
        TextView text = convertView.findViewById(R.id.thongbaotext);
        LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.layout1);
        text.setText(arrayList.get(index).getName()+" - "+arrayList.get(index).getArtist());
        if (arrayList.get(index).getStatus()==1) linearLayout.setBackgroundResource(R.drawable.my_edit_text_border2);

        return convertView;
    }
}
