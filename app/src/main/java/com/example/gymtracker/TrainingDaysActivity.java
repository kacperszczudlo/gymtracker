package com.example.gymtracker; // Zmień na nazwę swojego pakietu

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class TrainingDaysActivity extends AppCompatActivity {

    private Button mondayButton, tuesdayButton, wednesdayButton, thursdayButton, fridayButton, saturdayButton, sundayButton, nextButton;
    private ArrayList<String> selectedDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_days);

        // Inicjalizacja przycisków
        mondayButton = findViewById(R.id.mondayButton);
        tuesdayButton = findViewById(R.id.tuesdayButton);
        wednesdayButton = findViewById(R.id.wednesdayButton);
        thursdayButton = findViewById(R.id.thursdayButton);
        fridayButton = findViewById(R.id.fridayButton);
        saturdayButton = findViewById(R.id.saturdayButton);
        sundayButton = findViewById(R.id.sundayButton);
        nextButton = findViewById(R.id.nextButton);

        // Lista przechowująca wybrane dni
        selectedDays = new ArrayList<>();

        // Ustawienie początkowego koloru przycisków na szary
        setButtonColor(mondayButton, false);
        setButtonColor(tuesdayButton, false);
        setButtonColor(wednesdayButton, false);
        setButtonColor(thursdayButton, false);
        setButtonColor(fridayButton, false);
        setButtonColor(saturdayButton, false);
        setButtonColor(sundayButton, false);

        // Obsługa kliknięć dla każdego przycisku dnia
        mondayButton.setOnClickListener(v -> toggleDay(mondayButton, "Poniedziałek"));
        tuesdayButton.setOnClickListener(v -> toggleDay(tuesdayButton, "Wtorek"));
        wednesdayButton.setOnClickListener(v -> toggleDay(wednesdayButton, "Środa"));
        thursdayButton.setOnClickListener(v -> toggleDay(thursdayButton, "Czwartek"));
        fridayButton.setOnClickListener(v -> toggleDay(fridayButton, "Piątek"));
        saturdayButton.setOnClickListener(v -> toggleDay(saturdayButton, "Sobota"));
        sundayButton.setOnClickListener(v -> toggleDay(sundayButton, "Niedziela"));

        // Obsługa przycisku DALEJ
        nextButton.setOnClickListener(v -> {
            if (selectedDays.isEmpty()) {
                Toast.makeText(TrainingDaysActivity.this, "Wybierz co najmniej jeden dzień treningowy", Toast.LENGTH_SHORT).show();
            } else {
                String selectedDaysText = "Wybrane dni: " + String.join(", ", selectedDays);
                Toast.makeText(TrainingDaysActivity.this, selectedDaysText, Toast.LENGTH_LONG).show();

                // Tutaj możesz dodać kod do przejścia do kolejnej aktywności, np.:
                // Intent intent = new Intent(TrainingDaysActivity.this, NextActivity.class);
                // startActivity(intent);
            }
        });
    }

    // Funkcja do zmiany koloru przycisku i zarządzania listą wybranych dni
    private void toggleDay(Button button, String day) {
        boolean isSelected = selectedDays.contains(day);
        if (isSelected) {
            // Odznacz dzień
            selectedDays.remove(day);
            setButtonColor(button, false);
        } else {
            // Zaznacz dzień
            selectedDays.add(day);
            setButtonColor(button, true);
        }
    }

    // Funkcja do ustawienia koloru przycisku
    private void setButtonColor(Button button, boolean isSelected) {
        if (isSelected) {
            button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4CAF50"))); // Zielony
        } else {
            button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#616161"))); // Szary
        }
    }
}