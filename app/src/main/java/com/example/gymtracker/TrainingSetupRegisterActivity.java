package com.example.gymtracker;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymtracker.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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

        adapter = new ExerciseAdapter(
                exerciseList,
                this::removeExercise,
                true,
                (dayId, exerciseName, seriesPosition) -> {
                    if (dayId != -1) {
                        DatabaseHelper dbHelper = new DatabaseHelper(this);
                        dbHelper.deleteDayExercise(dayId, exerciseName, seriesPosition);
                        // Opcjonalnie log:
                        // Log.d("TrainingSetupRegister", "Usunięto serię " + exerciseName + " (poz: " + seriesPosition + ")");
                    }
                },
                dayId
        );
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        addExerciseButton.setOnClickListener(v -> showExerciseDialog());

        nextButton.setOnClickListener(v -> {
            // Usuń stare ćwiczenia dnia (w tabeli day_exercise)
            dbHelper.deleteDayExercises(dayId);

            // Zapisz nowe ćwiczenia w tabeli day_exercise
            for (Exercise exercise : exerciseList) {
                dbHelper.saveDayExercise(dayId, exercise);
            }

            // Pobierz dzisiejszą datę jako plan_valid_from
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String todayDate = dateFormat.format(new Date());

            // Zapisz nowy plan ćwiczeń w tabeli plan_exercise (jeśli chcesz śledzić historię)
            dbHelper.saveTrainingPlan(userId, dayName, exerciseList, todayDate);

            // Wróć do TrainingDaysActivity
            Intent intent = new Intent(TrainingSetupRegisterActivity.this, TrainingDaysActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private void showExerciseDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
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

        if (dialogRecyclerView != null) {
            dialogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            dialogRecyclerView.setAdapter(dialogAdapter);
        }

        if (cancelButton != null) {
            cancelButton.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();

        // Rozszerzenie na 100% wysokości
        View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheet.setLayoutParams(bottomSheet.getLayoutParams());
        }
    }

    private void loadExercisesForDay() {
        exerciseList.addAll(dbHelper.getDayExercises(dayId));
    }

    private void removeExercise(int position) {
        exerciseList.remove(position);
        adapter.notifyItemRemoved(position);
    }
}
