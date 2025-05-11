package com.example.gymtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    private String todayDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_main);

        dbHelper = new DatabaseHelper(this);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());


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
            intent.putExtra("DAY_ID",   dayId);
            intent.putParcelableArrayListExtra("EXERCISE_LIST", exerciseList);

            // ‼️ NOWE DODATKOWE FLAGI
            intent.putExtra("MODE", "LOG");     // mówimy Setup-owi, że edytujemy dzisiejszy log
            intent.putExtra("DATE", todayDate); // przekazujemy dzisiejszą datę

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
            Toast.makeText(this, "Jesteś już na stronie głównej", Toast.LENGTH_SHORT).show();
        });

        Button saveButton = findViewById(R.id.saveTrainingButton);
        saveButton.setOnClickListener(v -> {
            boolean success = dbHelper.saveLogSeries(userId, todayDate, selectedDay, exerciseList);
            String message = success ? "Trening zapisany pomyślnie!" : "Błąd zapisu treningu!";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });


    }

    private void loadExercisesForDay(String dayName) {
        exerciseList.clear();
        selectedDay = dayName;
        boolean allowEdit = true;                // ← w treningu ZAWSZE możemy dodać/usunąć
        adapter = new ExerciseAdapter(exerciseList,
                this::removeExercise,
                allowEdit);
        recyclerView.setAdapter(adapter);


        // DEBUG: sprawdź dane wejściowe
        Log.d("DEBUG_LOG", "Dzień: " + dayName);
        Log.d("DEBUG_LOG", "Data: " + todayDate);
        Log.d("DEBUG_LOG", "User ID: " + userId);

        boolean logExists = dbHelper.trainingLogExists(userId, todayDate, dayName);
        Log.d("DEBUG_LOG", "Czy log istnieje: " + logExists);

        if (!logExists) {
            /*  1️⃣ spróbuj zbudować z planu...                         */
            boolean created = dbHelper.createEmptyTrainingLogFromPlan(userId, dayName, todayDate);

            /*  2️⃣ …a jeśli planu brak – utwórz całkiem pusty log.     */
            if (!created) {
                long id = dbHelper.createEmptyTrainingLog(userId, dayName, todayDate);
                Log.d("DEBUG_LOG", "Brak planu – stworzono pusty log, id=" + id);
            }
        }


        // Pobieramy dane z logu
        exerciseList.addAll(dbHelper.getLogExercises(userId, todayDate, dayName));
        Log.d("DEBUG_LOG", "Załadowano ćwiczeń: " + exerciseList.size());

        adapter = new ExerciseAdapter(exerciseList, this::removeExercise, false); // umożliwia edycję
        recyclerView.setAdapter(adapter);
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

    private void removeExercise(int position) {
        Exercise ex = exerciseList.get(position);

        // 1.  UI
        exerciseList.remove(position);
        adapter.notifyItemRemoved(position);

        if (exerciseList.isEmpty()) {
            adapter.notifyDataSetChanged();   //   ‼️ czyści ewentualny widok-widmo
        }

        // 2.  Baza
        long logId = dbHelper.getLogId(userId, todayDate, selectedDay);
        if (logId != -1) {                                   //  log istnieje → usuń ćwiczenie
            dbHelper.deleteLogExercise(logId, ex.getName());

            // jeżeli to było OSTATNIE ćwiczenie → usuń pusty log
            if (exerciseList.isEmpty()) {
                dbHelper.deleteEmptyLogIfNeeded(logId);
            }
        }
    }



}