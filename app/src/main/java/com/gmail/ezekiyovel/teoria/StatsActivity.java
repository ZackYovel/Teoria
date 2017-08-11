package com.gmail.ezekiyovel.teoria;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.gmail.ezekiyovel.teoria.R;
import com.gmail.ezekiyovel.teoria.database.DataAccess;

import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        PieChart chart = (PieChart) findViewById(R.id.chart);

        int[] statsData = new DataAccess(this).getStatsData();

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(statsData[0], "לא הוצגו"));
        entries.add(new PieEntry(statsData[1], "נענו בניסיון ראשון"));
        entries.add(new PieEntry(statsData[2], "נענו בניסיון שני"));
        entries.add(new PieEntry(statsData[3], "נענו בניסיון שלישי"));
        entries.add(new PieEntry(statsData[4], "נענו בניסיון רביעי"));

        PieDataSet dataSet = new PieDataSet(entries, "Label");
        dataSet.setColors(new int[]{
                R.color.colorNotDisplayed,
                R.color.colorAnswered1Attempts,
                R.color.colorAnswered2Attempts,
                R.color.colorAnswered3Attempts,
                R.color.colorAnswered4Attempts
        }, this);
        dataSet.setLabel("מקרא:");

        PieData pieData = new PieData(dataSet);

        chart.setData(pieData);
        Description desc = new Description();
        desc.setText("סטטיסטיקת מענה על פי מספר הניסיונות.");
        chart.setDescription(desc);
        chart.setDrawEntryLabels(false);
        chart.setHoleColor(ContextCompat.getColor(this, R.color.colorStatsBackground));
        chart.invalidate();
    }
}
