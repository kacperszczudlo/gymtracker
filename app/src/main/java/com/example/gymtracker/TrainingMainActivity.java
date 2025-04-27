package com.example.gymtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;

public class TrainingMainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExerciseAdapter adapter;
    private ArrayList<Exercise> exerciseList;
    private DatabaseHelper dbHelper;
    private int userId;

    private RecyclerView weekDaysRecyclerView;
    private WeekDaysAdapter weekDaysAdapter;
    private String selectedDay;
    private static final int REQUEST_CODE_EDIT_EXERCISES = 2;
    private TextView timerTextView;
    private Button timerToggleButton;
    private CountDownTimer timer;
    private boolean isRunning = false;
    private long timeLeftInMillis = 60 * 1000; // 1 minuta
    private final long startTimeInMillis = 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_main);

        dbHelper = new DatabaseHelper(this);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        Toast.makeText(this, "Odczytano user_id: " + userId, Toast.LENGTH_LONG).show();

        if (userId == -1) {
            Toast.makeText(this, "Błąd użytkownika. Spróbuj ponownie się zalogować.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TrainingMainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

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

        initRestTimer();

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(android.graphics.Rect outRect, android.view.View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = 12;
            }
        });

        weekDaysAdapter = new WeekDaysAdapter(dayName -> {
            selectedDay = dayName;
            loadExercisesForDay(dayName);
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        weekDaysRecyclerView.setLayoutManager(layoutManager);
        weekDaysRecyclerView.setAdapter(weekDaysAdapter);

        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int adjustedDayIndex = (dayOfWeek + 5) % 7;
        selectedDay = weekDaysAdapter.getFullDayName(adjustedDayIndex);
        int initialPosition = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2 % 7) + adjustedDayIndex;
        weekDaysRecyclerView.scrollToPosition(initialPosition);

        Button editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(v -> {
            long dayId = getDayId(selectedDay);
            if (dayId == -1) {
                dayId = dbHelper.saveTrainingDay(userId, selectedDay);
            }
            Intent intent = new Intent(TrainingMainActivity.this, TrainingSetupActivity.class);
            intent.putExtra("DAY_NAME", selectedDay);
            intent.putExtra("DAY_ID", dayId);
            intent.putParcelableArrayListExtra("EXERCISE_LIST", exerciseList);
            startActivityForResult(intent, REQUEST_CODE_EDIT_EXERCISES);
        });

        loadExercisesForDay(selectedDay);

        // Initialize navigation buttons
        ImageButton menuButton = findViewById(R.id.menuButton);
        ImageButton profileButton = findViewById(R.id.profileButton);
        ImageButton homeButton = findViewById(R.id.homeButton);

        // Null checks
        if (menuButton == null || profileButton == null) {
            Toast.makeText(this, "Błąd: Nie znaleziono przycisków nawigacji", Toast.LENGTH_SHORT).show();
            return;
        }

        menuButton.setOnClickListener(v -> {
            Intent intent = new Intent(TrainingMainActivity.this, AccountSettingsActivity.class);
            startActivity(intent);
        });

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(TrainingMainActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(TrainingMainActivity.this, TrainingMainActivity.class);
            startActivity(intent);
        });
    }

    private void loadExercisesForDay(String dayName) {
        exerciseList.clear();
        long dayId = dbHelper.getTrainingDayId(userId, dayName);
        if (dayId != -1) {
            exerciseList.addAll(dbHelper.getDayExercises(dayId));
        }
        adapter.notifyDataSetChanged();
    }

    private long getDayId(String dayName) {
        return dbHelper.getTrainingDayId(userId, dayName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_EXERCISES && resultCode == RESULT_OK) {
            loadExercisesForDay(selectedDay);
        }
    }

    private void initRestTimer() {
        timerTextView = findViewById(R.id.timerTextView);
        timerToggleButton = findViewById(R.id.timerToggleButton);

        updateTimerText();

        timerToggleButton.setOnClickListener(v -> {
            if (isRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });
    }

    private void startTimer() {
        timer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                isRunning = false;
                timerToggleButton.setText("Start");
                timerToggleButton.setBackgroundTintList(
                        ContextCompat.getColorStateList(TrainingMainActivity.this, R.color.green)
                );
                timeLeftInMillis = startTimeInMillis;
                updateTimerText();
            }
        }.start();
        isRunning = true;
        timerToggleButton.setText("Stop");
        timerToggleButton.setBackgroundTintList(
                ContextCompat.getColorStateList(TrainingMainActivity.this, android.R.color.holo_red_light)
        );
    }

    private void pauseTimer() {
        if (timer != null) timer.cancel();
        isRunning = false;
        timerToggleButton.setText("Start");
        timerToggleButton.setBackgroundTintList(
                ContextCompat.getColorStateList(TrainingMainActivity.this, R.color.green)
        );
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
    }
}