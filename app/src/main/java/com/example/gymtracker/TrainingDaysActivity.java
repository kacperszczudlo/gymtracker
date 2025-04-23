package com.example.gymtracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymtracker.R;

public class TrainingDaysActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private Button[] dayButtons;
    private String[] dayNames;
    private Button nextButton;

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
        nextButton = findViewById(R.id.nextButton);

        // Tablica dni dla łatwiejszego zarządzania
        dayButtons = new Button[]{
                mondayButton, tuesdayButton, wednesdayButton,
                thursdayButton, fridayButton, saturdayButton, sundayButton
        };
        dayNames = new String[]{
                "Poniedziałek", "Wtorek", "Środa",
                "Czwartek", "Piątek", "Sobota", "Niedziela"
        };

        // Inicjalizacja dni w bazie danych
        initializeTrainingDays();

        // Ustawienie listenerów dla przycisków dni
        for (int i = 0; i < dayButtons.length; i++) {
            final String dayName = dayNames[i];
            dayButtons[i].setOnClickListener(v -> handleDayClick(dayName));
        }

        // Przycisk "Dalej"
        nextButton.setOnClickListener(v -> {
            if (hasAnyDayExercises()) {
                Intent intent = new Intent(TrainingDaysActivity.this, TrainingMainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Ustawienie początkowych kolorów przycisków
        updateButtonColors();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Odśwież kolory przycisków po powrocie do aktywności
        updateButtonColors();
    }

    private void initializeTrainingDays() {
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
            Intent intent;
            // Otwórz TrainingSetupRegisterActivity podczas rejestracji
            intent = new Intent(TrainingDaysActivity.this, TrainingSetupRegisterActivity.class);
            intent.putExtra("DAY_NAME", dayName);
            intent.putExtra("DAY_ID", dayId);
            startActivity(intent);
        }
    }

    private void updateButtonColors() {
        int userId = 1; // Zakładamy userId = 1
        boolean hasExercises = false;
        for (int i = 0; i < dayNames.length; i++) {
            long dayId = dbHelper.getTrainingDayId(userId, dayNames[i]);
            if (dayId != -1 && !dbHelper.getDayExercises(dayId).isEmpty()) {
                dayButtons[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                hasExercises = true;
            } else {
                dayButtons[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#616161")));
            }
        }
        // Aktywuj przycisk "Dalej" tylko jeśli są ćwiczenia
        nextButton.setEnabled(hasExercises);
        nextButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                hasExercises ? Color.parseColor("#4CAF50") : Color.parseColor("#B0BEC5")
        ));
    }

    private boolean hasAnyDayExercises() {
        int userId = 1; // Zakładamy userId = 1
        for (String day : dayNames) {
            long dayId = dbHelper.getTrainingDayId(userId, day);
            if (dayId != -1 && !dbHelper.getDayExercises(dayId).isEmpty()) {
                return true;
            }
        }
        return false;
    }
}