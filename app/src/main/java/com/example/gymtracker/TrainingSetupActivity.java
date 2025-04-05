package com.example.gymtracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class TrainingSetupActivity extends AppCompatActivity {

    private TextView trainingTitleTextView, exercise1TextView, exercise2TextView, exercise3TextView, exercise4TextView, exercise5TextView;
    private Button addExerciseButton, nextButton;
    private ArrayList<String> selectedDays;
    private int currentDayIndex = 0;
    private DatabaseHelper dbHelper;
    private ArrayList<String> exercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_setup);

        // Inicjalizacja elementów UI
        trainingTitleTextView = findViewById(R.id.trainingTitleTextView);
        exercise1TextView = findViewById(R.id.exercise1TextView);
        exercise2TextView = findViewById(R.id.exercise2TextView);
        exercise3TextView = findViewById(R.id.exercise3TextView);
        exercise4TextView = findViewById(R.id.exercise4TextView);
        exercise5TextView = findViewById(R.id.exercise5TextView);
        addExerciseButton = findViewById(R.id.addExerciseButton);
        nextButton = findViewById(R.id.nextButton);

        // Odbierz wybrane dni z poprzedniej aktywności
        Intent intent = getIntent();
        selectedDays = intent.getStringArrayListExtra("selectedDays");
        if (selectedDays == null || selectedDays.isEmpty()) {
            Toast.makeText(this, "Brak wybranych dni", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicjalizacja bazy danych
        dbHelper = new DatabaseHelper(this);
        exercises = dbHelper.getAllExercises();

        // Ustaw nagłówek dla pierwszego dnia
        updateTrainingTitle();

        // Obsługa kliknięć w ćwiczenia
        exercise1TextView.setOnClickListener(v -> showExerciseSelectionDialog(exercise1TextView));
        exercise2TextView.setOnClickListener(v -> showExerciseSelectionDialog(exercise2TextView));
        exercise3TextView.setOnClickListener(v -> showExerciseSelectionDialog(exercise3TextView));
        exercise4TextView.setOnClickListener(v -> showExerciseSelectionDialog(exercise4TextView));
        exercise5TextView.setOnClickListener(v -> showExerciseSelectionDialog(exercise5TextView));

        // Przycisk DALEJ
        nextButton.setOnClickListener(v -> {
            // Sprawdź, czy wszystkie ćwiczenia zostały wybrane
            if (isAnyExerciseNotSelected()) {
                Toast.makeText(this, "Wybierz wszystkie ćwiczenia", Toast.LENGTH_SHORT).show();
                return;
            }

            // Przejdź do kolejnego dnia
            currentDayIndex++;
            if (currentDayIndex < selectedDays.size()) {
                updateTrainingTitle();
                resetExercises();
            } else {
                Toast.makeText(this, "Konfiguracja treningów zakończona!", Toast.LENGTH_LONG).show();
                // Tutaj możesz przejść do kolejnej aktywności, np.:
                // Intent nextIntent = new Intent(TrainingSetupActivity.this, MainActivity.class);
                // startActivity(nextIntent);
                finish();
            }
        });

        // Przycisk DODAJ ĆWICZENIE (opcjonalny, w tym przykładzie nieaktywny)
        addExerciseButton.setOnClickListener(v -> {
            Toast.makeText(this, "Funkcjonalność dodawania ćwiczeń nie jest jeszcze zaimplementowana", Toast.LENGTH_SHORT).show();
        });
    }

    // Aktualizuj nagłówek w zależności od bieżącego dnia
    private void updateTrainingTitle() {
        String day = selectedDays.get(currentDayIndex);
        trainingTitleTextView.setText("Trening " + (currentDayIndex + 1) + " " + day);
    }

    // Resetuj ćwiczenia do domyślnych wartości
    private void resetExercises() {
        exercise1TextView.setText("Ćwiczenie 1");
        exercise2TextView.setText("Ćwiczenie 1");
        exercise3TextView.setText("Ćwiczenie 1");
        exercise4TextView.setText("Ćwiczenie 1");
        exercise5TextView.setText("Ćwiczenie 1");
    }

    // Sprawdź, czy jakieś ćwiczenie nie zostało wybrane
    private boolean isAnyExerciseNotSelected() {
        return exercise1TextView.getText().toString().equals("Ćwiczenie 1") ||
                exercise2TextView.getText().toString().equals("Ćwiczenie 1") ||
                exercise3TextView.getText().toString().equals("Ćwiczenie 1") ||
                exercise4TextView.getText().toString().equals("Ćwiczenie 1") ||
                exercise5TextView.getText().toString().equals("Ćwiczenie 1");
    }

    // Pokaż dialog z listą ćwiczeń
    private void showExerciseSelectionDialog(final TextView exerciseTextView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Wybierz ćwiczenie");

        // Konwersja ArrayList na tablicę dla dialogu
        final String[] exerciseArray = exercises.toArray(new String[0]);

        builder.setItems(exerciseArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Ustaw wybrane ćwiczenie w TextView
                exerciseTextView.setText(exerciseArray[which]);
            }
        });

        builder.setNegativeButton("Anuluj", null);
        builder.show();
    }
}