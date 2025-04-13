package com.example.gymtracker;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class TrainingDaysActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private List<String> trainingDays;
    private TextView[] dayTextViews;
    private Button[] dayButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_days);

        dbHelper = new DatabaseHelper(this);

        // Inicjalizacja TextView i Button dla dni treningowych
        dayTextViews = new TextView[]{
                findViewById(R.id.day1TextView),
                findViewById(R.id.day2TextView),
                findViewById(R.id.day3TextView),
                findViewById(R.id.day4TextView),
                findViewById(R.id.day5TextView),
                findViewById(R.id.day6TextView),
                findViewById(R.id.day7TextView)
        };

        dayButtons = new Button[]{
                findViewById(R.id.day1Button),
                findViewById(R.id.day2Button),
                findViewById(R.id.day3Button),
                findViewById(R.id.day4Button),
                findViewById(R.id.day5Button),
                findViewById(R.id.day6Button),
                findViewById(R.id.day7Button)
        };

        // Pobierz dni treningowe z bazy danych
        trainingDays = dbHelper.getAllTrainingDays().stream()
                .map(TrainingDay::getDay)
                .collect(java.util.stream.Collectors.toList());

        // Jeśli baza danych jest pusta, dodaj domyślne dni treningowe
        if (trainingDays.isEmpty()) {
            addDefaultTrainingDays();
            trainingDays = dbHelper.getAllTrainingDays().stream()
                    .map(TrainingDay::getDay)
                    .collect(java.util.stream.Collectors.toList());
        }

        // Wyświetl dni treningowe i ustaw listenery
        for (int i = 0; i < dayTextViews.length && i < trainingDays.size(); i++) {
            String dayName = trainingDays.get(i);
            long dayId = dbHelper.getTrainingDayId(dayName);

            dayTextViews[i].setText(dayName);
            dayTextViews[i].setVisibility(View.VISIBLE);

            final long selectedDayId = dayId;
            final String selectedDayName = dayName;
            dayButtons[i].setOnClickListener(v -> {
                // Przekaż ID i nazwę wybranego dnia do TrainingSetupActivity
                Intent intent = new Intent(TrainingDaysActivity.this, TrainingSetupActivity.class);
                intent.putExtra("DAY_ID", selectedDayId);
                intent.putExtra("DAY_NAME", selectedDayName);
                startActivity(intent);
            });
            dayButtons[i].setVisibility(View.VISIBLE);
        }
    }

    // Metoda do dodawania domyślnych dni treningowych
    private void addDefaultTrainingDays() {
        String[] defaultDays = {
                "Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela"
        };

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (String day : defaultDays) {
            ContentValues values = new ContentValues();
            values.put("day", day);
            db.insert("training_days", null, values);
        }
        db.close();
    }
}