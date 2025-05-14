package com.example.gymtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class TrainingDaysActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private Button[] dayButtons;
    private String[] dayNames;
    private Button nextButton;
    private SharedPreferences prefs;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_days);

        dbHelper = new DatabaseHelper(this);

        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Błąd użytkownika. Spróbuj ponownie się zalogować.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(TrainingDaysActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

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
            } else {
                Toast.makeText(this, "Wybierz przynajmniej jeden dzień z ćwiczeniami!", Toast.LENGTH_LONG).show();
            }
        });

        // Ustawienie początkowych kolorów przycisków
        updateButtonColors();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Odśwież kolory przycisków po powrocie do aktywności
        Log.d("DEBUG_DAYS", "onResume: Odświeżanie kolorów przycisków");
        updateButtonColors();
    }

    private void initializeTrainingDays() {
        for (String day : dayNames) {
            long dayId = dbHelper.getTrainingDayId(userId, day);
            if (dayId == -1) {
                dayId = dbHelper.saveTrainingDay(userId, day);
                Log.d("DEBUG_DAYS", "Zainicjalizowano dzień: " + day + ", dayId: " + dayId);
            } else {
                Log.d("DEBUG_DAYS", "Dzień już istnieje: " + day + ", dayId: " + dayId);
            }
        }
    }

    private void handleDayClick(String dayName) {
        long dayId = dbHelper.getTrainingDayId(userId, dayName);
        if (dayId != -1) {
            Intent intent = new Intent(TrainingDaysActivity.this, TrainingSetupRegisterActivity.class);
            intent.putExtra("DAY_NAME", dayName);
            intent.putExtra("DAY_ID", dayId);
            intent.putExtra("SOURCE_ACTIVITY", "TrainingDaysActivity");
            startActivity(intent);
        } else {
            Log.e("DEBUG_DAYS", "Błąd: Nie znaleziono dayId dla dnia: " + dayName);
            Toast.makeText(this, "Błąd: Nie można otworzyć dnia " + dayName, Toast.LENGTH_LONG).show();
        }
    }

    private void updateButtonColors() {
        boolean hasExercises = false;
        for (int i = 0; i < dayNames.length; i++) {
            long dayId = dbHelper.getTrainingDayId(userId, dayNames[i]);
            if (dayId != -1) {
                ArrayList<Exercise> exercises = dbHelper.getDayExercises(dayId);
                Log.d("DEBUG_DAYS", "updateButtonColors - Dzień: " + dayNames[i] + ", dayId: " + dayId + ", ćwiczeń: " + exercises.size());
                for (Exercise ex : exercises) {
                    Log.d("DEBUG_DAYS", "Ćwiczenie: " + ex.getName() + ", Serie: " + ex.getSeriesList().size());
                    for (Series series : ex.getSeriesList()) {
                        Log.d("DEBUG_DAYS", "Seria: reps=" + series.getReps() + ", weight=" + series.getWeight());
                    }
                }
                if (!exercises.isEmpty()) {
                    dayButtons[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                    hasExercises = true;
                } else {
                    dayButtons[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#616161")));
                }
            } else {
                Log.e("DEBUG_DAYS", "Błąd: Nie znaleziono dayId dla dnia: " + dayNames[i]);
            }
        }
        // Aktywuj przycisk "Dalej" tylko jeśli są ćwiczenia
        nextButton.setEnabled(hasExercises);
        nextButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                hasExercises ? Color.parseColor("#4CAF50") : Color.parseColor("#B0BEC5")
        ));
        Log.d("DEBUG_DAYS", "Przycisk Dalej aktywny: " + hasExercises);
    }

    private boolean hasAnyDayExercises() {
        for (String day : dayNames) {
            long dayId = dbHelper.getTrainingDayId(userId, day);
            if (dayId != -1) {
                ArrayList<Exercise> exercises = dbHelper.getDayExercises(dayId);
                Log.d("DEBUG_DAYS", "hasAnyDayExercises - Dzień: " + day + ", dayId: " + dayId + ", ćwiczeń: " + exercises.size());
                if (!exercises.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }
}