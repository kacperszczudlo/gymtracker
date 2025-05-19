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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap;

public class TrainingMainActivity extends AppCompatActivity {
    private RecyclerView exerciseRecyclerView;
    private ExerciseAdapter exerciseAdapter;
    private ArrayList<Exercise> exerciseList;
    private DatabaseHelper dbHelper;
    private int userId;

    private RecyclerView weekDaysRecyclerView;
    private WeekDaysAdapter weekDaysAdapter;
    private String selectedDayName;
    private ImageButton prevWeekButton;
    private TextView dateTextView;
    private ImageButton nextWeekButton;
    private Calendar currentDisplayCalendar;
    private SimpleDateFormat dateFormatForTextView;
    private SimpleDateFormat dateFormatForDb;
    private String currentSelectedDateString;

    private TextView timerTextView;
    private Button timerToggleButton;
    private CountDownTimer timer;
    private boolean isRunning = false;
    private long timeLeftInMillis = 60 * 1000;
    private final long startTimeInMillis = 60 * 1000;

    private static final int REQUEST_CODE_EDIT_EXERCISES = 2;
    private HashMap<String, Integer> dayNameToCalendarField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_main);

        dbHelper = new DatabaseHelper(this);
        initializeDayNameMapping();

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        dateFormatForTextView = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        dateFormatForDb = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        currentDisplayCalendar = Calendar.getInstance();
        currentSelectedDateString = dateFormatForDb.format(currentDisplayCalendar.getTime());

        if (userId == -1) {
            Toast.makeText(this, "Błąd użytkownika. Spróbuj ponownie się zalogować.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TrainingMainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        exerciseRecyclerView = findViewById(R.id.exerciseRecyclerView);
        exerciseList = new ArrayList<>();
        exerciseAdapter = new ExerciseAdapter(exerciseList, null, false);
        exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exerciseRecyclerView.setAdapter(exerciseAdapter);
        exerciseRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(android.graphics.Rect outRect, android.view.View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = 12;
            }
        });

        initRestTimer();
        initDateNavigation();

        weekDaysRecyclerView = findViewById(R.id.weekDaysRecyclerView);
        weekDaysAdapter = new WeekDaysAdapter(dayName -> {
            this.selectedDayName = dayName;
            updateCalendarToSelectedDay(dayName);
            updateDateTextView();
            this.currentSelectedDateString = dateFormatForDb.format(currentDisplayCalendar.getTime());
            loadExercisesForDay(this.selectedDayName);
            weekDaysAdapter.setSelectedUserDay(this.selectedDayName);
        });
        LinearLayoutManager weekDaysLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        weekDaysRecyclerView.setLayoutManager(weekDaysLayoutManager);
        weekDaysRecyclerView.setAdapter(weekDaysAdapter);

        setInitialDayAndView();

        Button editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(v -> {
            if (selectedDayName == null || selectedDayName.isEmpty()) {
                Toast.makeText(this, "Proszę najpierw wybrać dzień.", Toast.LENGTH_SHORT).show();
                return;
            }
            long dayId = getDayId(selectedDayName);
            Intent intent = new Intent(TrainingMainActivity.this, TrainingSetupActivity.class);
            intent.putExtra("DAY_NAME", selectedDayName);
            intent.putExtra("DAY_ID", dayId);
            intent.putParcelableArrayListExtra("EXERCISE_LIST", new ArrayList<>(exerciseList));
            intent.putExtra("MODE", "EDIT_LOG_ENTRIES");
            intent.putExtra("DATE", currentSelectedDateString);
            startActivityForResult(intent, REQUEST_CODE_EDIT_EXERCISES);
        });

        ImageButton menuButton = findViewById(R.id.menuButton);
        ImageButton profileButton = findViewById(R.id.profileButton);
        ImageButton homeButton = findViewById(R.id.homeButton);

        if (menuButton != null) menuButton.setOnClickListener(v -> startActivity(new Intent(this, AccountSettingsActivity.class)));
        if (profileButton != null) profileButton.setOnClickListener(v -> startActivity(new Intent(this, UserProfileActivity.class)));
        if (homeButton != null) homeButton.setOnClickListener(v -> Toast.makeText(this, "Jesteś już na stronie głównej", Toast.LENGTH_SHORT).show());

        Button saveButton = findViewById(R.id.saveTrainingButton);
        saveButton.setOnClickListener(v -> {
            if (selectedDayName == null || selectedDayName.isEmpty()) {
                Toast.makeText(this, "Wybierz dzień treningowy.", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean success = dbHelper.saveLogSeries(userId, currentSelectedDateString, selectedDayName, exerciseList);
            String message = success ? "Trening zapisany pomyślnie!" : "Błąd zapisu treningu!";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }

    private void initializeDayNameMapping() {
        dayNameToCalendarField = new HashMap<>();
        String[] fullDayNamesFromAdapter = WeekDaysAdapter.FULL_DAYS;
        int[] calendarFields = {Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY,
                Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY};
        for (int i = 0; i < fullDayNamesFromAdapter.length; i++) {
            dayNameToCalendarField.put(fullDayNamesFromAdapter[i], calendarFields[i]);
        }
    }

    private void setInitialDayAndView() {
        int calendarApiDayOfWeek = currentDisplayCalendar.get(Calendar.DAY_OF_WEEK);
        int ourDayIndex = WeekDaysAdapter.getOurIndexFromCalendarField(calendarApiDayOfWeek);

        if (ourDayIndex != -1 && weekDaysAdapter != null) {
            selectedDayName = weekDaysAdapter.getFullDayName(ourDayIndex);
            updateCalendarToSelectedDay(selectedDayName);
            currentSelectedDateString = dateFormatForDb.format(currentDisplayCalendar.getTime());
            updateDateTextView();

            int initialPosition = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2 % 7) + ourDayIndex;
            weekDaysRecyclerView.scrollToPosition(initialPosition);
            weekDaysAdapter.setSelectedUserDay(selectedDayName);

            loadExercisesForDay(selectedDayName);
        } else {
            Log.e("TrainingMain", "Nie udało się ustawić początkowego dnia.");
            if (weekDaysAdapter != null) {
                selectedDayName = weekDaysAdapter.getFullDayName(0);
                updateCalendarToSelectedDay(selectedDayName);
                currentSelectedDateString = dateFormatForDb.format(currentDisplayCalendar.getTime());
                updateDateTextView();
                weekDaysRecyclerView.scrollToPosition(Integer.MAX_VALUE / 2);
                weekDaysAdapter.setSelectedUserDay(selectedDayName);
                loadExercisesForDay(selectedDayName);
            }
        }
    }

    private void initDateNavigation() {
        prevWeekButton = findViewById(R.id.prevWeekButton);
        dateTextView = findViewById(R.id.dateTextView);
        nextWeekButton = findViewById(R.id.nextWeekButton);

        updateDateTextView();

        prevWeekButton.setOnClickListener(v -> {
            currentDisplayCalendar.add(Calendar.WEEK_OF_YEAR, -1);
            currentSelectedDateString = dateFormatForDb.format(currentDisplayCalendar.getTime());
            updateDateTextView();
            if (selectedDayName != null) {
                loadExercisesForDay(selectedDayName);
            }
        });

        nextWeekButton.setOnClickListener(v -> {
            currentDisplayCalendar.add(Calendar.WEEK_OF_YEAR, 1);
            currentSelectedDateString = dateFormatForDb.format(currentDisplayCalendar.getTime());
            updateDateTextView();
            if (selectedDayName != null) {
                loadExercisesForDay(selectedDayName);
            }
        });
    }

    private void updateDateTextView() {
        if (dateTextView != null && currentDisplayCalendar != null) {
            dateTextView.setText(dateFormatForTextView.format(currentDisplayCalendar.getTime()));
        }
    }

    private void updateCalendarToSelectedDay(String dayName) {
        Integer targetCalendarDayConstant = dayNameToCalendarField.get(dayName);
        if (targetCalendarDayConstant != null && currentDisplayCalendar != null) {
            currentDisplayCalendar.set(Calendar.DAY_OF_WEEK, targetCalendarDayConstant);
        } else {
            Log.e("TrainingMain", "Nie można zaktualizować kalendarza dla dnia: " + dayName);
        }
    }

    private void loadExercisesForDay(String dayName) {
        if (dayName == null || dayName.isEmpty()) {
            Log.w("LoadExercises", "Próba załadowania ćwiczeń dla pustej nazwy dnia.");
            exerciseList.clear();
            if (exerciseAdapter != null) exerciseAdapter.notifyDataSetChanged();
            return;
        }
        exerciseList.clear();
        Log.d("DEBUG_LOG", "Ładowanie dla dnia: " + dayName + ", Data dla bazy: " + currentSelectedDateString);

        boolean logExists = dbHelper.trainingLogExists(userId, currentSelectedDateString, dayName);
        Log.d("DEBUG_LOG", "Czy log (" + currentSelectedDateString + ", " + dayName + ") istnieje: " + logExists);

        if (!logExists) {
            boolean createdFromPlan = dbHelper.createEmptyTrainingLogFromPlan(userId, dayName, currentSelectedDateString);
            if (!createdFromPlan) {
                long id = dbHelper.createEmptyTrainingLog(userId, dayName, currentSelectedDateString);
                Log.d("DEBUG_LOG", "Brak planu – stworzono pusty log (" + currentSelectedDateString + ", " + dayName + "), id=" + id);
            }
        }

        exerciseList.addAll(dbHelper.getLogExercises(userId, currentSelectedDateString, dayName));
        Log.d("DEBUG_LOG", "Załadowano ćwiczeń: " + exerciseList.size() + " dla " + currentSelectedDateString + " (" + dayName + ")");

        if (exerciseAdapter != null) {
            exerciseAdapter.notifyDataSetChanged();
        }
    }

    private long getDayId(String dayName) {
        return dbHelper.getTrainingDayId(userId, dayName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_EXERCISES && resultCode == RESULT_OK) {
            if (selectedDayName != null) {
                Log.d("TrainingMain", "Powrót z edycji, odświeżanie dla: " + selectedDayName + " na dacie " + currentSelectedDateString);
                exerciseList.clear();
                loadExercisesForDay(selectedDayName);
                exerciseAdapter.notifyDataSetChanged();
            }
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
                if (isFinishing() || isDestroyed()) return;
                timerToggleButton.setBackgroundTintList(ContextCompat.getColorStateList(TrainingMainActivity.this, R.color.green));
                timeLeftInMillis = startTimeInMillis;
                updateTimerText();
                Toast.makeText(TrainingMainActivity.this, "Koniec przerwy!", Toast.LENGTH_SHORT).show();
            }
        }.start();
        isRunning = true;
        timerToggleButton.setText("Stop");
        if (!isFinishing() && !isDestroyed()) {
            timerToggleButton.setBackgroundTintList(ContextCompat.getColorStateList(TrainingMainActivity.this, android.R.color.holo_red_light));
        }
    }

    private void pauseTimer() {
        if (timer != null) {
            timer.cancel();
        }
        isRunning = false;
        timerToggleButton.setText("Start");
        if (!isFinishing() && !isDestroyed()) {
            timerToggleButton.setBackgroundTintList(ContextCompat.getColorStateList(TrainingMainActivity.this, R.color.green));
        }
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        if (timerTextView != null) {
            timerTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
        }
    }
}