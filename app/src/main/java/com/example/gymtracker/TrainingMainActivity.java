package com.example.gymtracker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymtracker.R;
import java.util.ArrayList;

public class TrainingMainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExerciseAdapter adapter;
    private ArrayList<Exercise> exerciseList;
    private DatabaseHelper dbHelper;
    private RecyclerView weekDaysRecyclerView;
    private WeekDaysAdapter weekDaysAdapter;
    private String selectedDay;
    private static final int REQUEST_CODE_EDIT_EXERCISES = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trainingmainactivity);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.exerciseRecyclerView);
        weekDaysRecyclerView = findViewById(R.id.weekDaysRecyclerView);
        exerciseList = new ArrayList<>();
        adapter = new ExerciseAdapter(exerciseList, position -> {
            // Usuwanie ćwiczenia
            Exercise exercise = exerciseList.get(position);
            dbHelper.deleteDayExercise(getDayId(selectedDay), exercise.getName());
            exerciseList.remove(position);
            adapter.notifyItemRemoved(position);
        }, false); // Pola nieedytowalne w TrainingMainActivity
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Inicjalizacja karuzeli dni
        weekDaysAdapter = new WeekDaysAdapter(dayName -> {
            selectedDay = dayName;
            loadExercisesForDay(dayName);
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        weekDaysRecyclerView.setLayoutManager(layoutManager);
        weekDaysRecyclerView.setAdapter(weekDaysAdapter);

        // Ustaw domyślny wybrany dzień (Poniedziałek) w środku listy
        selectedDay = "Poniedziałek";
        int initialPosition = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2 % 7); // Środek listy
        weekDaysAdapter.setSelectedPosition(initialPosition);
        weekDaysRecyclerView.scrollToPosition(initialPosition);

        // Przycisk edycji
        Button editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(v -> {
            long dayId = getDayId(selectedDay);
            // Jeśli dzień nie istnieje w bazie, utwórz nowy
            if (dayId == -1) {
                int userId = 1; // Zakładamy userId=1
                dayId = dbHelper.saveTrainingDay(userId, selectedDay);
            }
            // Przejdź do TrainingSetupActivity z exerciseList
            Intent intent = new Intent(TrainingMainActivity.this, TrainingSetupActivity.class);
            intent.putExtra("DAY_NAME", selectedDay);
            intent.putExtra("DAY_ID", dayId);
            intent.putParcelableArrayListExtra("EXERCISE_LIST", exerciseList);
            startActivityForResult(intent, REQUEST_CODE_EDIT_EXERCISES);
        });

        // Załaduj ćwiczenia dla domyślnego dnia
        loadExercisesForDay(selectedDay);
    }

    private void loadExercisesForDay(String dayName) {
        exerciseList.clear();
        long dayId = dbHelper.getTrainingDayId(1, dayName); // Zakładamy userId=1
        if (dayId != -1) {
            Cursor cursor = dbHelper.getDayExercises(dayId);
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("exercise_name"));
                int sets = cursor.getInt(cursor.getColumnIndexOrThrow("sets"));
                int reps = cursor.getInt(cursor.getColumnIndexOrThrow("reps"));
                float weight = cursor.getFloat(cursor.getColumnIndexOrThrow("weight"));
                exerciseList.add(new Exercise(name, sets, reps, weight));
            }
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    private long getDayId(String dayName) {
        return dbHelper.getTrainingDayId(1, dayName); // Zakładamy userId=1
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_EXERCISES && resultCode == RESULT_OK) {
            loadExercisesForDay(selectedDay);
        }
    }
}