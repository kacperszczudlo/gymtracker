package com.example.gymtracker;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymtracker.R;
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

        exerciseList = new ArrayList<>();
        loadExercisesForDay();

        adapter = new ExerciseAdapter(exerciseList, this::removeExercise, true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        addExerciseButton.setOnClickListener(v -> showExerciseDialog());

        nextButton.setOnClickListener(v -> {
            // Usuń istniejące ćwiczenia dla tego dnia
            dbHelper.deleteDayExercises(dayId);
            // Zapisz nowe ćwiczenia
            for (Exercise exercise : exerciseList) {
                if (!exercise.getSeriesList().isEmpty()) { // Zapisz tylko ćwiczenia z seriami
                    dbHelper.saveDayExercise(dayId, exercise);
                }
            }
            dbHelper.saveTrainingPlan(userId, dayName, exerciseList);   // :contentReference[oaicite:2]{index=2}

            // Wróć do TrainingDaysActivity
            Intent intent = new Intent(TrainingSetupRegisterActivity.this, TrainingDaysActivity.class);
            startActivity(intent);
            finish();
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
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        });
        dialogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dialogRecyclerView.setAdapter(dialogAdapter);

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void loadExercisesForDay() {
        exerciseList.addAll(dbHelper.getDayExercises(dayId));
    }

    private void removeExercise(int position) {
        exerciseList.remove(position);
        adapter.notifyItemRemoved(position);
    }
}