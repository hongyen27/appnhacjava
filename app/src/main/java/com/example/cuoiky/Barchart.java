package com.example.cuoiky;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.mikephil.charting.charts.BarChart;

public class Barchart extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    ArrayList<Song>songlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart);
        BarChart chart = findViewById(R.id.barchart);

        databaseHelper = new DatabaseHelper(Barchart.this);
        songlist = new ArrayList<Song>();
        songlist = databaseHelper.getAllSongs();
        Collections.sort(songlist,Song.SongHotCom);
        int y = songlist.size();
        if (y>10) y=10;
        int[] count=new int[y];
        String[] name=new String[y];
        for(int i=0;i< y;i++){
            count[i] = songlist.get(i).getCount();
            name[i]=songlist.get(i).getName();
        }

        ArrayList<BarEntry> luotnghe = new ArrayList();

        for(int i=0;i<y;i++){
            luotnghe.add(new BarEntry(i+1,count[i]));
        }
        BarDataSet dataSet = new BarDataSet(luotnghe, "Bài hát");


        ArrayList<String> ten = new ArrayList<>();
        for(int i=0;i<y;i++){
            ten.add(name[i]);
        }

        BarData data = new BarData(dataSet);
        chart.setData(data);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        chart.animateY(2000);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setValueTextSize(14);
        chart.setData(data);
        chart.setDrawBarShadow(true);
        chart.setFitBars(true);
        chart.getDescription().setText("");
        chart.getXAxis().setDrawLabels(false);

        Legend legend=chart.getLegend();
        List<LegendEntry> entries = new ArrayList<>();
        for (int i = 0; i < luotnghe.size(); i++) {
            LegendEntry entry = new LegendEntry();
            entry.formColor = dataSet.getColor(i);
            entry.label = name[i];
            entries.add(entry);
        }
        legend.setCustom(entries);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
//        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setTextSize(15);
        legend.setFormSize(15);

    }
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent1 = new Intent(Barchart.this,Online.class);
        startActivity(intent1);
        finish();
    }
}