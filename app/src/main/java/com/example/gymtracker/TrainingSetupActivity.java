package com.example.gymtracker;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymtracker.R;
import java.util.ArrayList;

public class TrainingSetupActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExerciseAdapter adapter;
    private ArrayList<Exercise> exerciseList;
    private DatabaseHelper dbHelper;
    private String dayName;
    private long dayId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_setup);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.exerciseRecyclerView);
        Button addExerciseButton = findViewById(R.id.addExerciseButton);
        Button nextButton = findViewById(R.id.nextButton);

        // Odbieramy nazwę dnia i ID z intentu
        dayName = getIntent().getStringExtra("DAY_NAME");
        dayId = getIntent().getLongExtra("DAY_ID", -1);

        exerciseList = new ArrayList<>();
        adapter = new ExerciseAdapter(exerciseList, this::removeExercise);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        addExerciseButton.setOnClickListener(v -> showExerciseDialog());

        nextButton.setOnClickListener(v -> {
            // Zapisujemy ćwiczenia dla danego dnia
            for (Exercise exercise : exerciseList) {
                dbHelper.saveDayExercise(dayId, exercise);
            }
            // Wracamy do TrainingDaysActivity
            setResult(RESULT_OK);
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
            exerciseList.add(new Exercise(exerciseName, 0, 0, 0));
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        });
        dialogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dialogRecyclerView.setAdapter(dialogAdapter);

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void removeExercise(int position) {
        exerciseList.remove(position);
        adapter.notifyItemRemoved(position);
    }
}