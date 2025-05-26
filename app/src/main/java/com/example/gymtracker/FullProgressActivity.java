package com.example.gymtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymtracker.DatabaseHelper.BodyStatEntry;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FullProgressActivity extends AppCompatActivity {

    private TextView armCircTextView, waistCircTextView, hipCircTextView;
    private LineChart weightChart;

    private DatabaseHelper databaseHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_progress);

        // Inicjalizacja
        databaseHelper = new DatabaseHelper(this);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        Log.d("FullProgressActivity", "userId = " + userId);

        if (userId == -1) {
            Toast.makeText(this, "Błąd: użytkownik niezalogowany", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Widoki tekstowe
        armCircTextView = findViewById(R.id.armCircTextView);
        waistCircTextView = findViewById(R.id.waistCircTextView);
        hipCircTextView = findViewById(R.id.hipCircTextView);
        weightChart = findViewById(R.id.weightChart);

        setupExerciseChart(findViewById(R.id.benchChart), "Wyciskanie sztangi");
        setupExerciseChart(findViewById(R.id.squatChart), "Przysiady");
        setupExerciseChart(findViewById(R.id.deadliftChart), "Martwy ciąg");


        // Dane i wykresy
        showLatestMeasurements();
        drawWeightChart();

        // Dolna nawigacja
        ImageButton menuButton = findViewById(R.id.menuButton);
        ImageButton homeButton = findViewById(R.id.homeButton);
        ImageButton profileButton = findViewById(R.id.profileButton);

        menuButton.setOnClickListener(v ->
                startActivity(new Intent(this, AccountSettingsActivity.class)));

        homeButton.setOnClickListener(v ->
                startActivity(new Intent(this, TrainingMainActivity.class)));

        profileButton.setOnClickListener(v ->
                startActivity(new Intent(this, UserProfileActivity.class)));
    }

    private String formatMeasurementWithProgress(String label, float value, float diff, String unit) {
        String base = String.format(Locale.US, "%s: %.1f %s", label, value, unit);
        if (diff == 0) return base;

        String progress = String.format(Locale.US, " (%s%.1f %s)", diff > 0 ? "+" : "-", Math.abs(diff), unit);
        return base + progress;
    }


    private void showLatestMeasurements() {
        List<BodyStatEntry> history = databaseHelper.getBodyStatHistory(userId);
        if (history.size() >= 2) {
            BodyStatEntry first = history.get(0);
            BodyStatEntry last = history.get(history.size() - 1);

            float armDiff = last.armCirc - first.armCirc;
            float waistDiff = last.waistCirc - first.waistCirc;
            float hipDiff = last.hipCirc - first.hipCirc;

            armCircTextView.setText(
                    createSpannableWithProgress("Obwód ramienia: ", last.armCirc, armDiff, "cm"));

            waistCircTextView.setText(
                    createSpannableWithProgress("Obwód talii: ", last.waistCirc, waistDiff, "cm"));

            hipCircTextView.setText(
                    createSpannableWithProgress("Obwód bioder: ", last.hipCirc, hipDiff, "cm"));

        } else if (!history.isEmpty()) {
            BodyStatEntry latest = history.get(0);
            armCircTextView.setText(String.format(Locale.US, "Obwód ramienia: %.1f cm", latest.armCirc));
            waistCircTextView.setText(String.format(Locale.US, "Obwód talii: %.1f cm", latest.waistCirc));
            hipCircTextView.setText(String.format(Locale.US, "Obwód bioder: %.1f cm", latest.hipCirc));
        } else {
            armCircTextView.setText("Obwód ramienia: Brak danych");
            waistCircTextView.setText("Obwód talii: Brak danych");
            hipCircTextView.setText("Obwód bioder: Brak danych");
        }
    }

    private void setupExerciseChart(LineChart chart, String exerciseName) {
        List<Entry> entries = databaseHelper.getExerciseProgressEntries(userId, exerciseName);

        if (entries.isEmpty()) {
            chart.setNoDataText("Brak danych");
            chart.setNoDataTextColor(getResources().getColor(android.R.color.white, getTheme()));
            return;
        }

        LineDataSet dataSet = new LineDataSet(entries, exerciseName);
        dataSet.setColor(getResources().getColor(R.color.green, getTheme()));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawCircles(true);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
        xAxis.setTextSize(14f);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(entries.size());
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                int index = (int) value;
                return String.valueOf(index + 1);
            }
        });

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
        leftAxis.setTextSize(14f);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        chart.getDescription().setEnabled(false);
        chart.getLegend().setTextColor(getResources().getColor(android.R.color.white, getTheme()));
        chart.invalidate();
    }



    private SpannableString createSpannableWithProgress(String label, float value, float diff, String unit) {
        String baseText = String.format(Locale.US, "%s%.1f %s", label, value, unit);
        if (diff == 0) {
            return new SpannableString(baseText);
        }

        String progress = String.format(Locale.US, " (%s%.1f %s)", diff > 0 ? "+" : "-", Math.abs(diff), unit);
        SpannableString spannable = new SpannableString(baseText + progress);

        int start = baseText.length();
        int end = baseText.length() + progress.length();

        int color = getResources().getColor(diff >= 0 ? R.color.green : R.color.red, getTheme());

        spannable.setSpan(
                new ForegroundColorSpan(color),
                start, end,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        return spannable;
    }




    private void drawWeightChart() {
        List<BodyStatEntry> history = databaseHelper.getBodyStatHistory(userId);
        if (history.isEmpty()) {
            Toast.makeText(this, "Brak danych do wykresu wagi", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Entry> entries = new ArrayList<>();
        List<Integer> monthValues = new ArrayList<>();

        for (int i = 0; i < history.size(); i++) {
            BodyStatEntry entry = history.get(i);
            entries.add(new Entry(i, entry.weight));

        }



        LineDataSet dataSet = new LineDataSet(entries, "Waga (kg)");
        dataSet.setColor(getResources().getColor(R.color.green, getTheme()));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawCircles(true);
        dataSet.setDrawValues(false); // ❌ NIE rysuj wag nad punktami

        LineData lineData = new LineData(dataSet);
        weightChart.setData(lineData);

        XAxis xAxis = weightChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
        xAxis.setTextSize(14f);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(entries.size());

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                int index = (int) value;
                return String.valueOf(index + 1); // pokazujemy numer pomiaru: 1, 2, 3, ...
            }
        });



        YAxis leftAxis = weightChart.getAxisLeft();
        leftAxis.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
        leftAxis.setTextSize(14f);

        YAxis rightAxis = weightChart.getAxisRight();
        rightAxis.setEnabled(false);

        weightChart.getDescription().setEnabled(false);
        weightChart.getLegend().setTextColor(getResources().getColor(android.R.color.white, getTheme()));
        weightChart.invalidate(); // Odśwież wykres
    }

}
