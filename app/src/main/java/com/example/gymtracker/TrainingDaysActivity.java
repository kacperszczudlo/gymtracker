package com.example.gymtracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymtracker.R;

public class TrainingDaysActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_days);

        dbHelper = new DatabaseHelper(this);

        // Przyciski dni tygodnia
        Button mondayButton = findViewById(R.id.mondayButton);
        Button tuesdayButton = findViewById(R.id.tuesdayButton);
        Button wednesdayButton = findViewById(R.id.wednesdayButton);
        Button thursdayButton = findViewById(R.id.thursdayButton);
        Button fridayButton = findViewById(R.id.fridayButton);
        Button saturdayButton = findViewById(R.id.saturdayButton);
        Button sundayButton = findViewById(R.id.sundayButton);
        Button nextButton = findViewById(R.id.nextButton);

        // Tablica dni dla łatwiejszego zarządzania
        Button[] dayButtons = {
                mondayButton, tuesdayButton, wednesdayButton,
                thursdayButton, fridayButton, saturdayButton, sundayButton
        };
        String[] dayNames = {
                "Poniedziałek", "Wtorek", "Środa",
                "Czwartek", "Piątek", "Sobota", "Niedziela"
        };

        // Inicjalizacja dni w bazie danych
        initializeTrainingDays(dayNames);

        // Ustawienie listenerów dla przycisków dni
        for (int i = 0; i < dayButtons.length; i++) {
            final String dayName = dayNames[i];
            dayButtons[i].setOnClickListener(v -> handleDayClick(dayName));
        }

        // Przycisk "Dalej"
        nextButton.setOnClickListener(v -> {
            Intent intent = new Intent(TrainingDaysActivity.this, TrainingMainActivity.class);
            startActivity(intent);
        });
    }

    private void initializeTrainingDays(String[] dayNames) {
        int userId = 1; // Zakładamy userId = 1 dla uproszczenia
        for (String day : dayNames) {
            long dayId = dbHelper.getTrainingDayId(userId, day);
            if (dayId == -1) {
                dbHelper.saveTrainingDay(userId, day);
            }
        }
    }

    private void handleDayClick(String dayName) {
        int userId = 1; // Zakładamy userId = 1
        long dayId = dbHelper.getTrainingDayId(userId, dayName);
        if (dayId != -1) {
            if (dbHelper.getDayExercises(dayId).isEmpty()) {
                // Brak ćwiczeń - otwórz TrainingSetupActivity
                Intent intent = new Intent(TrainingDaysActivity.this, TrainingSetupActivity.class);
                intent.putExtra("DAY_NAME", dayName);
                intent.putExtra("DAY_ID", dayId);
                startActivity(intent);
            } else {
                // Są ćwiczenia - otwórz TrainingMainActivity
                Intent intent = new Intent(TrainingDaysActivity.this, TrainingMainActivity.class);
                intent.putExtra("DAY_NAME", dayName);
                intent.putExtra("DAY_ID", dayId);
                startActivity(intent);
            }
        }
    }
}