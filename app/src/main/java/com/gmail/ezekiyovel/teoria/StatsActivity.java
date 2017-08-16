package com.gmail.ezekiyovel.teoria;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.gmail.ezekiyovel.teoria.database.DataAccess;
import com.gmail.ezekiyovel.teoria.util.Preferences;

import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        PieChart chart = (PieChart) findViewById(R.id.chart);

        long classFromPreferences = Preferences.getClassFromPreferences(this);
        int[] statsData = new DataAccess(this).getStatsData(classFromPreferences);

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(statsData[0], getString(R.string.label_not_displayed)));
        entries.add(new PieEntry(statsData[1], getString(R.string.label_answered_first_attempt)));
        entries.add(new PieEntry(statsData[2], getString(R.string.label_answered_second_attempt)));
        entries.add(new PieEntry(statsData[3], getString(R.string.label_answered_third_attempt)));
        entries.add(new PieEntry(statsData[4], getString(R.string.label_answered_fourth_attempt)));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{
                R.color.colorNotDisplayed,
                R.color.colorAnswered1Attempts,
                R.color.colorAnswered2Attempts,
                R.color.colorAnswered3Attempts,
                R.color.colorAnswered4Attempts
        }, this);
        dataSet.setLabel(getString(R.string.label_legend));
        dataSet.setValueTextSize(16);

        PieData pieData = new PieData(dataSet);

        chart.setData(pieData);
        Description desc = new Description();
        desc.setText(getString(R.string.label_stats_description));
        chart.setDescription(desc);
        chart.setDrawEntryLabels(false);
//        chart.setHoleColor(ContextCompat.getColor(this, R.color.colorStatsBackground));
        chart.invalidate();
    }
}
