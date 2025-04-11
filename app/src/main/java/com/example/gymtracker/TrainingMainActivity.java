package com.example.gymtracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TrainingMainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView trainingTitleTextView;
    private Button addSeriesButton;
    private DatabaseHelper dbHelper;
    private List<Exercise> exerciseList;
    private List<TrainingDay> trainingDayList;
    private boolean showingExercisesForToday = true;
    private long currentTrainingDayId = -1;

    // TextViews for the calendar
    private TextView[] calendarDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trainingmainactivity);

        // Inicjalizacja elementów UI
        recyclerView = findViewById(R.id.exerciseRecyclerView);
        trainingTitleTextView = findViewById(R.id.trainingTitleTextView);
        addSeriesButton = findViewById(R.id.addSeriesButton);

        // Inicjalizacja TextView dla kalendarza
        calendarDays = new TextView[]{
                findViewById(R.id.day1),
                findViewById(R.id.day2),
                findViewById(R.id.day3),
                findViewById(R.id.day4),
                findViewById(R.id.day5),
                findViewById(R.id.day6),
                findViewById(R.id.day7)
        };

        // Inicjalizacja bazy danych
        dbHelper = new DatabaseHelper(this);

        // Pobierz aktualny dzień tygodnia
        String today = getCurrentDayOfWeek();
        currentTrainingDayId = dbHelper.getTrainingDayId(today);

        // Pobierz ćwiczenia dla dzisiejszego dnia
        exerciseList = dbHelper.getExercisesForDay(today);

        // Pobierz wszystkie dni z ćwiczeniami
        trainingDayList = dbHelper.getAllTrainingDays();

        // Ustaw nagłówek (bez daty, bo kalendarz ją pokazuje)
        if (!exerciseList.isEmpty()) {
            trainingTitleTextView.setText("Trening");
            showingExercisesForToday = true;
        } else {
            trainingTitleTextView.setText("Brak treningu na dziś");
            showingExercisesForToday = false;
        }

        // Ustaw daty w kalendarzu
        setupCalendar();

        // Inicjalizacja RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        updateRecyclerView();

        // Obsługa przycisku "Dodaj serię"
        addSeriesButton.setOnClickListener(v -> {
            if (showingExercisesForToday && !exerciseList.isEmpty()) {
                for (Exercise exercise : exerciseList) {
                    exercise.setSets(exercise.getSets() + 1);
                    dbHelper.updateExercise(currentTrainingDayId, exercise.getName(), exercise.getSets(), exercise.getReps());
                }
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    // Pobierz aktualny dzień tygodnia
    private String getCurrentDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                return "Poniedziałek";
            case Calendar.TUESDAY:
                return "Wtorek";
            case Calendar.WEDNESDAY:
                return "Środa";
            case Calendar.THURSDAY:
                return "Czwartek";
            case Calendar.FRIDAY:
                return "Piątek";
            case Calendar.SATURDAY:
                return "Sobota";
            case Calendar.SUNDAY:
                return "Niedziela";
            default:
                return "";
        }
    }

    // Ustaw daty w kalendarzu
    private void setupCalendar() {
        // Pobierz aktualną datę z urządzenia
        Calendar calendar = Calendar.getInstance();
        int currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        // Ustaw kalendarz na początek bieżącego tygodnia (poniedziałek)
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysToSubtract = (dayOfWeek == Calendar.SUNDAY) ? 6 : dayOfWeek - Calendar.MONDAY;
        calendar.add(Calendar.DAY_OF_MONTH, -daysToSubtract);

        // Ustaw daty dla każdego dnia tygodnia (poniedziałek do niedzieli)
        for (int i = 0; i < 7; i++) {
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            calendarDays[i].setText(String.valueOf(dayOfMonth));

            // Podświetl aktualny dzień
            if (dayOfMonth == currentDayOfMonth &&
                    calendar.get(Calendar.MONTH) == currentMonth &&
                    calendar.get(Calendar.YEAR) == currentYear) {
                calendarDays[i].setBackgroundResource(R.drawable.calendar_selected_background);
            } else {
                calendarDays[i].setBackground(null);
            }

            // Przejdź do następnego dnia
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    // Aktualizuj RecyclerView w zależności od trybu
    private void updateRecyclerView() {
        if (showingExercisesForToday && !exerciseList.isEmpty()) {
            recyclerView.setAdapter(new ExerciseAdapter(exerciseList));
        } else {
            recyclerView.setAdapter(new TrainingDayAdapter(trainingDayList));
        }
    }

    // Adapter dla ćwiczeń
    private class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {
        private List<Exercise> exercises;

        public ExerciseAdapter(List<Exercise> exercises) {
            this.exercises = exercises;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Exercise exercise = exercises.get(position);
            holder.exerciseNameTextView.setText(exercise.getName());
            holder.setsEditText.setText(String.valueOf(exercise.getSets()));
            holder.repsEditText.setText(String.valueOf(exercise.getReps()));

            holder.setsEditText.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    try {
                        int sets = Integer.parseInt(holder.setsEditText.getText().toString());
                        exercise.setSets(sets);
                        dbHelper.updateExercise(currentTrainingDayId, exercise.getName(), sets, exercise.getReps());
                    } catch (NumberFormatException e) {
                        exercise.setSets(0);
                        holder.setsEditText.setText("0");
                    }
                }
            });

            holder.repsEditText.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    try {
                        int reps = Integer.parseInt(holder.repsEditText.getText().toString());
                        exercise.setReps(reps);
                        dbHelper.updateExercise(currentTrainingDayId, exercise.getName(), exercise.getSets(), reps);
                    } catch (NumberFormatException e) {
                        exercise.setReps(0);
                        holder.repsEditText.setText("0");
                    }
                }
            });

            holder.removeButton.setOnClickListener(v -> {
                exercises.remove(position);
                notifyItemRemoved(position);
            });
        }

        @Override
        public int getItemCount() {
            return exercises.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView exerciseNameTextView;
            EditText setsEditText, repsEditText;
            Button removeButton;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                exerciseNameTextView = itemView.findViewById(R.id.exerciseNameTextView);
                setsEditText = itemView.findViewById(R.id.setsEditText);
                repsEditText = itemView.findViewById(R.id.repsEditText);
                removeButton = itemView.findViewById(R.id.removeExerciseButton);
            }
        }
    }

    // Adapter dla dni treningowych
    private class TrainingDayAdapter extends RecyclerView.Adapter<TrainingDayAdapter.ViewHolder> {
        private List<TrainingDay> trainingDays;

        public TrainingDayAdapter(List<TrainingDay> trainingDays) {
            this.trainingDays = trainingDays;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TrainingDay trainingDay = trainingDays.get(position);
            String day = trainingDay.getDay();
            holder.exerciseNameTextView.setText(day);
            holder.setsEditText.setVisibility(View.GONE);
            holder.repsEditText.setVisibility(View.GONE);
            holder.removeButton.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return trainingDays.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView exerciseNameTextView;
            EditText setsEditText, repsEditText;
            Button removeButton;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                exerciseNameTextView = itemView.findViewById(R.id.exerciseNameTextView);
                setsEditText = itemView.findViewById(R.id.setsEditText);
                repsEditText = itemView.findViewById(R.id.repsEditText);
                removeButton = itemView.findViewById(R.id.removeExerciseButton);
            }
        }
    }
}