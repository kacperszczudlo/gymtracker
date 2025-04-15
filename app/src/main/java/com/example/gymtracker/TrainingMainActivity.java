package com.example.gymtracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymtracker.R;
import java.util.ArrayList;
import java.util.Calendar;

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
            Exercise exercise = exerciseList.get(position);
            dbHelper.deleteDayExercises(getDayId(selectedDay));
            exerciseList.remove(position);
            adapter.notifyItemRemoved(position);
        }, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Add ItemDecoration for spacing between exercise items
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(android.graphics.Rect outRect, android.view.View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = 12; // Equivalent to 12dp spacing
            }
        });

        weekDaysAdapter = new WeekDaysAdapter(dayName -> {
            selectedDay = dayName;
            loadExercisesForDay(dayName);
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        weekDaysRecyclerView.setLayoutManager(layoutManager);
        weekDaysRecyclerView.setAdapter(weekDaysAdapter);

        // Pobierz aktualny dzień tygodnia
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 1 = Niedziela, 2 = Poniedziałek, ..., 7 = Sobota
        int adjustedDayIndex = (dayOfWeek + 5) % 7; // Przesunięcie, aby Poniedziałek był 0, Wtorek 1, ..., Niedziela 6
        selectedDay = weekDaysAdapter.getFullDayName(adjustedDayIndex); // Ustaw aktualny dzień

        // Ustaw pozycję w RecyclerView na aktualny dzień
        int initialPosition = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2 % 7) + adjustedDayIndex;
        weekDaysRecyclerView.scrollToPosition(initialPosition);

        Button editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(v -> {
            long dayId = getDayId(selectedDay);
            if (dayId == -1) {
                int userId = 1;
                dayId = dbHelper.saveTrainingDay(userId, selectedDay);
            }
            Intent intent = new Intent(TrainingMainActivity.this, TrainingSetupActivity.class);
            intent.putExtra("DAY_NAME", selectedDay);
            intent.putExtra("DAY_ID", dayId);
            intent.putParcelableArrayListExtra("EXERCISE_LIST", exerciseList);
            startActivityForResult(intent, REQUEST_CODE_EDIT_EXERCISES);
        });

        loadExercisesForDay(selectedDay);
    }

    private void loadExercisesForDay(String dayName) {
        exerciseList.clear();
        long dayId = dbHelper.getTrainingDayId(1, dayName);
        if (dayId != -1) {
            exerciseList.addAll(dbHelper.getDayExercises(dayId));
        }
        adapter.notifyDataSetChanged();
    }

    private long getDayId(String dayName) {
        return dbHelper.getTrainingDayId(1, dayName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_EXERCISES && resultCode == RESULT_OK) {
            loadExercisesForDay(selectedDay);
        }
    }
}