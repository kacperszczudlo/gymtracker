package com.example.gymtracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainingSetupActivity extends AppCompatActivity {

    private TextView trainingTitleTextView, exercise1TextView, exercise2TextView, exercise3TextView, exercise4TextView, exercise5TextView;
    private Button addExerciseButton, nextButton;
    private String selectedDay;

    // Lista ćwiczeń pobrana lokalnie – domyślny wybór (np. nazwy ćwiczeń)
    private ArrayList<String> exercises;

    // Zakładamy, że będziesz przekazywał USER_ID z poprzedniego etapu (np. z rejestracji/profilu)
    private int userId;

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

        // Odbierz wybrany dzień z Intentu
        Intent intent = getIntent();
        selectedDay = intent.getStringExtra("selectedDay");
        if (selectedDay == null) {
            Toast.makeText(this, "Błąd: Nie wybrano dnia", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // Pobierz identyfikator użytkownika (USER_ID) przekazany z poprzedniego etapu
        userId = intent.getIntExtra("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "Błąd: Brak identyfikatora użytkownika", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ustaw nagłówek dla wybranego dnia
        trainingTitleTextView.setText("Trening - " + selectedDay);

        // Opcjonalnie: Inicjalizacja listy ćwiczeń.
        // Jeśli wcześniej masz już ustawione ćwiczenia w bazie lokalnej, możesz je pobrać – poniższy kod został poprzednio wykorzystywany:
        // dbHelper = new DatabaseHelper(this);
        // exercises = dbHelper.getAllExercises();
        // Dla potrzeb integracji, przyjmujemy, że pobieramy je lokalnie lub definiujemy na sztywno.
        exercises = new ArrayList<>();
        // Przykładowe dane – jeśli masz więcej, możesz je rozszerzyć
        exercises.add("Ćwiczenie 1");
        exercises.add("Ćwiczenie 2");
        exercises.add("Ćwiczenie 3");
        exercises.add("Ćwiczenie 4");
        exercises.add("Ćwiczenie 5");

        // Obsługa kliknięć w TextView odpowiadających ćwiczeniom – pokazujemy dialog z listą ćwiczeń
        exercise1TextView.setOnClickListener(v -> showExerciseSelectionDialog(exercise1TextView));
        exercise2TextView.setOnClickListener(v -> showExerciseSelectionDialog(exercise2TextView));
        exercise3TextView.setOnClickListener(v -> showExerciseSelectionDialog(exercise3TextView));
        exercise4TextView.setOnClickListener(v -> showExerciseSelectionDialog(exercise4TextView));
        exercise5TextView.setOnClickListener(v -> showExerciseSelectionDialog(exercise5TextView));

        // Przycisk DALEJ – zamiast lokalnego zapisu w SQLite, wywołamy backendowy endpoint
        nextButton.setOnClickListener(v -> {
            if (isAnyExerciseNotSelected()) {
                Toast.makeText(this, "Wybierz wszystkie ćwiczenia", Toast.LENGTH_SHORT).show();
                return;
            }
            saveWorkoutToBackend();
        });

        // Przycisk DODAJ ĆWICZENIE – pozostawiamy jako placeholder, jeśli chcesz implementować dynamiczne dodawanie
        addExerciseButton.setOnClickListener(v -> {
            Toast.makeText(this, "Funkcjonalność dodawania ćwiczeń nie jest jeszcze zaimplementowana", Toast.LENGTH_SHORT).show();
        });
    }

    // Sprawdzenie, czy wszystkie ćwiczenia zostały wybrane (zakładamy, że początkowy tekst to "Ćwiczenie 1")
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
        final String[] exerciseArray = exercises.toArray(new String[0]);
        builder.setItems(exerciseArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                exerciseTextView.setText(exerciseArray[which]);
            }
        });
        builder.setNegativeButton("Anuluj", null);
        builder.show();
    }

    // Zamiast lokalnego zapisu w bazie, budujemy obiekt DTO i wysyłamy go do backendu
    private void saveWorkoutToBackend() {
        // Zbierz wybrane ćwiczenia
        List<String> selectedExercises = new ArrayList<>();
        selectedExercises.add(exercise1TextView.getText().toString());
        selectedExercises.add(exercise2TextView.getText().toString());
        selectedExercises.add(exercise3TextView.getText().toString());
        selectedExercises.add(exercise4TextView.getText().toString());
        selectedExercises.add(exercise5TextView.getText().toString());

        // Utwórz obiekt WorkoutDto i ustaw dane
        WorkoutDto workoutDto = new WorkoutDto();
        // Ustaw nazwę treningu – dla uproszczenia wykorzystujemy wybrany dzień
        workoutDto.setName(selectedDay);
        // Typ treningu – możesz ustalić domyślnie lub pobierać z innego pola; tutaj domyślnie "siłowy"
        workoutDto.setType("siłowy");
        // Czas trwania – ustaw domyślnie, np. 45 minut (lub pobierz z formularza, jeśli taki masz)
        workoutDto.setDuration(45);
        // Notatki – możesz ustawić informację, że trening został utworzony dla wybranego dnia
        workoutDto.setNotes("Trening utworzony dla dnia: " + selectedDay);

        // Mapowanie listy ćwiczeń: załóżmy, że wysyłamy tylko nazwy ćwiczeń.
        // Możesz rozbudować tę logikę o dodatkowe pola (np. wagę), jeśli są dostępne.
        List<ExerciseDto> exerciseDtos = new ArrayList<>();
        for (String exName : selectedExercises) {
            ExerciseDto exDto = new ExerciseDto();
            exDto.setName(exName);
            exerciseDtos.add(exDto);
        }
        workoutDto.setExercises(exerciseDtos);


        // Wywołanie API – używamy Retrofit i metody saveWorkout
        ApiService apiService = RetrofitClient.getApiService();
        apiService.saveWorkout(userId, workoutDto).enqueue(new Callback<WorkoutDto>() {
            @Override
            public void onResponse(Call<WorkoutDto> call, Response<WorkoutDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(TrainingSetupActivity.this, "Trening zapisany w backendzie!", Toast.LENGTH_SHORT).show();
                    // Po zapisie, przekazujemy wybrany dzień w wyniku i kończymy aktywność
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("selectedDay", selectedDay);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(TrainingSetupActivity.this, "Błąd zapisu treningu: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<WorkoutDto> call, Throwable t) {
                Toast.makeText(TrainingSetupActivity.this, "Błąd połączenia: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
