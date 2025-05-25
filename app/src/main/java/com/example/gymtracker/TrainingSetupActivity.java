package com.example.gymtracker;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TrainingSetupActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExerciseAdapter adapter;
    private ArrayList<Exercise> exerciseList;
    private DatabaseHelper dbHelper;
    private String dayName;
    private long dayId;
    private int userId;
    private boolean isLogEdit;
    private String logDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_setup);
        isLogEdit = "EDIT_LOG_ENTRIES".equals(getIntent().getStringExtra("MODE"));
        logDate = getIntent().getStringExtra("DATE");

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.exerciseRecyclerView);
        Button addExerciseButton = findViewById(R.id.addExerciseButton);
        Button nextButton = findViewById(R.id.nextButton);
        TextView trainingTitle = findViewById(R.id.trainingTitleTextView);
        boolean isEditableSeriesFields = isLogEdit;

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        dayName = getIntent().getStringExtra("DAY_NAME");
        dayId = getIntent().getLongExtra("DAY_ID", -1);
        trainingTitle.setText("Trening - " + dayName);

        exerciseList = new ArrayList<>();
        if (getIntent().hasExtra("EXERCISE_LIST")) {
            exerciseList = getIntent().getParcelableArrayListExtra("EXERCISE_LIST");
        } else {
            loadExercisesForDay();
        }

        adapter = new ExerciseAdapter(
                exerciseList,
                this::removeExercise,
                true, // 🔴 Pozostawiamy możliwość dodawania/usuwania ćwiczeń w TrainingSetupActivity
                (dayId, exerciseName, seriesPosition) -> {
                    if (dayId != -1) {
                        DatabaseHelper dbHelper = new DatabaseHelper(this);
                        dbHelper.deleteDayExercise(dayId, exerciseName, seriesPosition);
                        Log.d("TrainingSetupActivity", "Usunięto serię: " + exerciseName + " (pozycja: " + seriesPosition + ")");
                    }
                },
                dayId,
                isEditableSeriesFields // 🔴 Tu przekazujemy czy pola w seriach mają być edytowalne!
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        addExerciseButton.setOnClickListener(v -> showExerciseDialog());

        nextButton.setOnClickListener(v -> {
            Log.d("DEBUG_SAVE", "Saving exercises: " + exerciseList.size());
            for (Exercise ex : exerciseList) {
                Log.d("DEBUG_SAVE", "Exercise: " + ex.getName() + ", Series: " + ex.getSeriesList().size());
                for (Series s : ex.getSeriesList()) {
                    Log.d("DEBUG_SAVE", "  Series - Reps: " + s.getReps() + ", Weight: " + s.getWeight());
                }
            }
            if (isLogEdit) {
                boolean ok = dbHelper.saveLogSeries(userId, logDate, dayName, exerciseList);
                Log.d("DEBUG_PLAN", "saveLogSeries -> " + ok);
            } else {
                // 🔴 Zamiast dzisiejszej daty, użyj daty logu/daty dnia, np. logDate
                String validFromDate = logDate != null ? logDate : new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                long planId = dbHelper.saveTrainingPlan(userId, dayName, exerciseList, validFromDate);
                Log.d("DEBUG_PLAN", "saveTrainingPlan planId=" + planId);
            }
            Intent intent = new Intent(TrainingSetupActivity.this, TrainingMainActivity.class);
            startActivity(intent);
            setResult(RESULT_OK);
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

        View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheet.setLayoutParams(bottomSheet.getLayoutParams());
        }
    }

    private void loadExercisesForDay() {
        if (isLogEdit) {
            exerciseList.addAll(dbHelper.getLogExercises(userId, logDate, dayName));
        } else {
            exerciseList.addAll(dbHelper.getDayExercises(dayId));
        }
    }

    private void removeExercise(int position) {
        exerciseList.remove(position);
        adapter.notifyItemRemoved(position);
    }

    public long getDayId() {
        return dayId;
    }
}