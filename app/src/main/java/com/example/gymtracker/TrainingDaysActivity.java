package com.example.gymtracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class TrainingDaysActivity extends AppCompatActivity {

    private Button mondayButton, tuesdayButton, wednesdayButton, thursdayButton, fridayButton, saturdayButton, sundayButton, nextButton;
    private ArrayList<String> selectedDays;
    private static final int REQUEST_CODE_SETUP = 1;

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
        mondayButton.setOnClickListener(v -> startSetupActivity("Poniedziałek"));
        tuesdayButton.setOnClickListener(v -> startSetupActivity("Wtorek"));
        wednesdayButton.setOnClickListener(v -> startSetupActivity("Środa"));
        thursdayButton.setOnClickListener(v -> startSetupActivity("Czwartek"));
        fridayButton.setOnClickListener(v -> startSetupActivity("Piątek"));
        saturdayButton.setOnClickListener(v -> startSetupActivity("Sobota"));
        sundayButton.setOnClickListener(v -> startSetupActivity("Niedziela"));

        // Obsługa przycisku DALEJ
        nextButton.setOnClickListener(v -> {
            if (selectedDays.isEmpty()) {
                Toast.makeText(TrainingDaysActivity.this, "Wybierz co najmniej jeden dzień treningowy", Toast.LENGTH_SHORT).show();
            } else {
                String selectedDaysText = "Wybrane dni: " + String.join(", ", selectedDays);
                Toast.makeText(TrainingDaysActivity.this, selectedDaysText, Toast.LENGTH_LONG).show();

                // Przejście do TrainingMainActivity
                Intent intent = new Intent(TrainingDaysActivity.this, TrainingMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Funkcja do uruchomienia TrainingSetupActivity dla wybranego dnia
    private void startSetupActivity(String day) {
        Intent intent = new Intent(TrainingDaysActivity.this, TrainingSetupActivity.class);
        intent.putExtra("selectedDay", day);
        startActivityForResult(intent, REQUEST_CODE_SETUP);
    }

    // Odbiór wyniku z TrainingSetupActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SETUP) {
            if (resultCode == RESULT_OK && data != null) {
                String day = data.getStringExtra("selectedDay");
                if (day != null && !selectedDays.contains(day)) {
                    selectedDays.add(day);
                    Button button = getButtonForDay(day);
                    if (button != null) {
                        setButtonColor(button, true);
                    }
                } else {
                    Toast.makeText(this, "Nie udało się dodać dnia: " + day, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Anulowano wybór ćwiczeń", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Funkcja zwracająca przycisk dla danego dnia
    private Button getButtonForDay(String day) {
        switch (day) {
            case "Poniedziałek":
                return mondayButton;
            case "Wtorek":
                return tuesdayButton;
            case "Środa":
                return wednesdayButton;
            case "Czwartek":
                return thursdayButton;
            case "Piątek":
                return fridayButton;
            case "Sobota":
                return saturdayButton;
            case "Niedziela":
                return sundayButton;
            default:
                return null;
        }
    }

    // Funkcja do ustawienia koloru przycisku
    private void setButtonColor(Button button, boolean isSelected) {
        if (isSelected) {
            button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4CAF50")));
        } else {
            button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#616161")));
        }
    }
}