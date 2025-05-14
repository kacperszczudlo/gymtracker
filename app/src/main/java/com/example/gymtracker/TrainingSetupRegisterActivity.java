package com.example.gymtracker;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class TrainingSetupRegisterActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExerciseAdapter adapter;
    private ArrayList<Exercise> exerciseList;
    private DatabaseHelper dbHelper;
    private String dayName;
    private long dayId;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_setup);

        dbHelper = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        recyclerView = findViewById(R.id.exerciseRecyclerView);
        Button addExerciseButton = findViewById(R.id.addExerciseButton);
        Button nextButton = findViewById(R.id.nextButton);
        TextView trainingTitle = findViewById(R.id.trainingTitleTextView);

        dayName = getIntent().getStringExtra("DAY_NAME");
        dayId = getIntent().getLongExtra("DAY_ID", -1);
        trainingTitle.setText("Trening - " + dayName);

        // Odczyt flagi SOURCE_ACTIVITY
        String sourceActivity = getIntent().getStringExtra("SOURCE_ACTIVITY");
        boolean isEditable = "TrainingMainActivity".equals(sourceActivity);

        exerciseList = new ArrayList<>();
        loadExercisesForDay();

        adapter = new ExerciseAdapter(exerciseList, this::removeExercise, isEditable);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        addExerciseButton.setOnClickListener(v -> showExerciseDialog());

        nextButton.setOnClickListener(v -> {
            // Sprawdzenie poprawności danych wejściowych
            if (dayId == -1 || userId == -1) {
                Log.e("DEBUG_SAVE", "Błąd: dayId=" + dayId + ", userId=" + userId);
                Toast.makeText(this, "Błąd zapisu: nieprawidłowy dzień lub użytkownik", Toast.LENGTH_LONG).show();
                return;
            }

            // Log dla debugowania
            Log.d("DEBUG_SAVE", "Zapis ćwiczeń dla dnia: " + dayName + ", dayId: " + dayId + ", userId: " + userId);
            Log.d("DEBUG_SAVE", "Liczba ćwiczeń: " + exerciseList.size());
            for (Exercise ex : exerciseList) {
                Log.d("DEBUG_SAVE", "Ćwiczenie: " + ex.getName() + ", Serie: " + ex.getSeriesList().size());
                for (Series series : ex.getSeriesList()) {
                    Log.d("DEBUG_SAVE", "Seria: reps=" + series.getReps() + ", weight=" + series.getWeight());
                }
            }

            // Sprawdzenie, czy są ćwiczenia z seriami
            boolean hasExercisesWithSeries = false;
            for (Exercise exercise : exerciseList) {
                if (!exercise.getSeriesList().isEmpty()) {
                    hasExercisesWithSeries = true;
                    break;
                }
            }

            if (!hasExercisesWithSeries && !exerciseList.isEmpty()) {
                Toast.makeText(this, "Dodaj serie do ćwiczeń przed zapisem!", Toast.LENGTH_LONG).show();
                return;
            }

            // Usuń istniejące ćwiczenia dla tego dnia
            boolean deletedSuccess = dbHelper.deleteDayExercises(dayId);
            Log.d("DEBUG_SAVE", "Usuwanie ćwiczeń dla dayId: " + dayId + ", sukces: " + deletedSuccess);

            // Zapisz nowe ćwiczenia
            int savedExercises = 0;
            for (Exercise exercise : exerciseList) {
                if (!exercise.getSeriesList().isEmpty()) { // Zapisz tylko ćwiczenia z seriami
                    dbHelper.saveDayExercise(dayId, exercise);
                    savedExercises++;
                    Log.d("DEBUG_SAVE", "Zapisano ćwiczenie: " + exercise.getName());
                } else {
                    Log.d("DEBUG_SAVE", "Pomijam ćwiczenie bez serii: " + exercise.getName());
                }
            }
            Log.d("DEBUG_SAVE", "Zapisano " + savedExercises + " ćwiczeń dla dayId: " + dayId);

            // Zapisz plan treningowy
            dbHelper.saveTrainingPlan(userId, dayName, exerciseList);
            Log.d("DEBUG_SAVE", "Zapisano plan treningowy dla userId: " + userId + ", dayName: " + dayName);

            // Sprawdź, czy ćwiczenia są widoczne w bazie po zapisie
            ArrayList<Exercise> loadedExercises = dbHelper.getDayExercises(dayId);
            Log.d("DEBUG_SAVE", "Po zapisie wczytano ćwiczeń dla dayId " + dayId + ": " + loadedExercises.size());
            for (Exercise ex : loadedExercises) {
                Log.d("DEBUG_SAVE", "Wczytane ćwiczenie: " + ex.getName() + ", Serie: " + ex.getSeriesList().size());
            }

            // Feedback dla użytkownika
            Toast.makeText(this, savedExercises > 0 ? "Zapisano " + savedExercises + " ćwiczeń" : "Brak ćwiczeń do zapisania", Toast.LENGTH_LONG).show();

            // Obsługa trybu LOG z TrainingMainActivity
            if ("LOG".equals(getIntent().getStringExtra("MODE"))) {
                String date = getIntent().getStringExtra("DATE");
                Log.d("DEBUG_SAVE", "Zapis logu dla daty: " + date);
                dbHelper.saveLogSeries(userId, date, dayName, exerciseList);
                setResult(RESULT_OK);
                finish();
            } else {
                // Wróć do TrainingDaysActivity
                Log.d("DEBUG_SAVE", "Powrót do TrainingDaysActivity");
                Intent intent = new Intent(TrainingSetupRegisterActivity.this, TrainingDaysActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showExerciseDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_exercise_list);
        RecyclerView dialogRecyclerView = dialog.findViewById(R.id.dialogExerciseRecyclerView);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);

        ArrayList<String> exercises = new ArrayList<>();
        Cursor cursor = dbHelper.getExercises();
        while (cursor.moveToNext()) {
            exercises.add(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        }
        cursor.close();

        ExerciseDialogAdapter dialogAdapter = new ExerciseDialogAdapter(exercises, exerciseName -> {
            exerciseList.add(new Exercise(exerciseName));
            Log.d("DEBUG_SAVE", "Dodano ćwiczenie: " + exerciseName);
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        });
        dialogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dialogRecyclerView.setAdapter(dialogAdapter);

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void loadExercisesForDay() {
        exerciseList.clear();
        exerciseList.addAll(dbHelper.getDayExercises(dayId));
        Log.d("DEBUG_LOAD", "Wczytano ćwiczeń dla dayId " + dayId + ": " + exerciseList.size());
    }

    private void removeExercise(int position) {
        Log.d("DEBUG_REMOVE", "Usuwanie ćwiczenia na pozycji: " + position);
        exerciseList.remove(position);
        adapter.notifyItemRemoved(position);
    }
}