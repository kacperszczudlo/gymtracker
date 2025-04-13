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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TrainingMainActivity extends AppCompatActivity {

    private TextView trainingTitleTextView;
    private RecyclerView exerciseRecyclerView;
    private List<Exercise> exerciseList;
    private ExerciseAdapter exerciseAdapter;
    private DatabaseHelper dbHelper;
    private long currentTrainingDayId;
    private String currentDayName;
    private List<String> trainingDayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trainingmainactivity);

        trainingTitleTextView = findViewById(R.id.trainingTitleTextView);
        exerciseRecyclerView = findViewById(R.id.exerciseRecyclerView);
        dbHelper = new DatabaseHelper(this);

        // Pobierz ID i nazwę dnia z Intent
        currentTrainingDayId = getIntent().getLongExtra("DAY_ID", -1);
        currentDayName = getIntent().getStringExtra("DAY_NAME");

        // Jeśli nie ma ID z Intent, użyj dzisiejszej daty
        if (currentTrainingDayId == -1) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String today = sdf.format(new Date());
            currentTrainingDayId = dbHelper.getTrainingDayId(today);
            currentDayName = today;
        }

        if (currentTrainingDayId == -1) {
            trainingTitleTextView.setText("Brak treningu na dziś");
        } else {
            trainingTitleTextView.setText(currentDayName != null ? currentDayName : "Trening " + currentTrainingDayId);
        }

        // Inicjalizacja listy ćwiczeń
        exerciseList = new ArrayList<>();
        loadExercises();

        exerciseAdapter = new ExerciseAdapter(exerciseList);
        exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exerciseRecyclerView.setAdapter(exerciseAdapter);

        // Ustawienie listenerów dla dni w kalendarzu
        setupCalendarListeners();
    }

    private void loadExercises() {
        exerciseList.clear();
        List<Exercise> exercises = dbHelper.getExercisesForDay(currentDayName);
        exerciseList.addAll(exercises);
    }

    private void setupCalendarListeners() {
        trainingDayList = dbHelper.getAllTrainingDays().stream()
                .map(day -> day.getDay())
                .collect(java.util.stream.Collectors.toList());
        int[] dayIds = {R.id.day1, R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6, R.id.day7};
        for (int i = 0; i < dayIds.length && i < trainingDayList.size(); i++) {
            TextView dayView = findViewById(dayIds[i]);
            String day = trainingDayList.get(i);
            dayView.setText(day.substring(0, 3)); // Wyświetl skróconą nazwę (np. "Pon")
            int finalI = i;
            dayView.setOnClickListener(v -> {
                currentDayName = trainingDayList.get(finalI);
                currentTrainingDayId = dbHelper.getTrainingDayId(currentDayName);
                trainingTitleTextView.setText(currentDayName);
                loadExercises();
                exerciseAdapter.notifyDataSetChanged();
            });
        }
    }

    // Adapter dla ćwiczeń
    private class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {
        private List<Exercise> exercises;

        public ExerciseAdapter(List<Exercise> exercises) {
            this.exercises = exercises;
        }

        @NonNull
        @Override
        public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_item, parent, false);
            return new ExerciseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
            Exercise exercise = exercises.get(position);
            holder.exerciseNameTextView.setText(exercise.getName());

            // Pokaż przycisk "Dodaj serię" i ustaw jego widoczność
            holder.addSeriesButton.setVisibility(View.VISIBLE);
            holder.addSeriesButton.setOnClickListener(v -> {
                // Dodaj nową serię do bieżącego ćwiczenia
                Series newSeries = new Series(0, 0); // Domyślne wartości: 0 powtórzeń, 0 kg
                exercise.addSeries(newSeries);

                // Zaktualizuj bazę danych
                dbHelper.updateExercise(currentTrainingDayId, exercise.getName(), exercise.getSets(), newSeries.getReps());

                // Odśwież widok serii
                holder.seriesRecyclerView.getAdapter().notifyDataSetChanged();
            });

            // Ustaw adapter dla serii
            holder.seriesRecyclerView.setVisibility(View.VISIBLE);
            SeriesAdapter seriesAdapter = new SeriesAdapter(exercise.getSeriesList(), exercise);
            holder.seriesRecyclerView.setLayoutManager(new LinearLayoutManager(TrainingMainActivity.this));
            holder.seriesRecyclerView.setAdapter(seriesAdapter);
        }

        @Override
        public int getItemCount() {
            return exercises.size();
        }

        class ExerciseViewHolder extends RecyclerView.ViewHolder {
            TextView exerciseNameTextView;
            Button addSeriesButton;
            RecyclerView seriesRecyclerView;

            public ExerciseViewHolder(@NonNull View itemView) {
                super(itemView);
                exerciseNameTextView = itemView.findViewById(R.id.exerciseNameTextView);
                addSeriesButton = itemView.findViewById(R.id.addSeriesButton);
                seriesRecyclerView = itemView.findViewById(R.id.seriesRecyclerView);
            }
        }
    }

    // Adapter dla serii
    private class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.SeriesViewHolder> {
        private List<Series> seriesList;
        private Exercise exercise;

        public SeriesAdapter(List<Series> seriesList, Exercise exercise) {
            this.seriesList = seriesList;
            this.exercise = exercise;
        }

        @NonNull
        @Override
        public SeriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.series_item, parent, false);
            return new SeriesViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SeriesViewHolder holder, int position) {
            Series series = seriesList.get(position);
            holder.repsEditText.setText(String.valueOf(series.getReps()));
            holder.weightEditText.setText(String.valueOf(series.getWeight()));

            // Obsługa edycji liczby powtórzeń
            holder.repsEditText.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    try {
                        int reps = Integer.parseInt(holder.repsEditText.getText().toString());
                        series.setReps(reps);
                        // Zaktualizuj bazę danych
                        dbHelper.updateExercise(currentTrainingDayId, exercise.getName(), exercise.getSets(), reps);
                    } catch (NumberFormatException e) {
                        series.setReps(0);
                        holder.repsEditText.setText("0");
                    }
                }
            });

            // Obsługa edycji ciężaru
            holder.weightEditText.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    try {
                        int weight = Math.round(Float.parseFloat(holder.weightEditText.getText().toString()));
                        series.setWeight(weight);
                        // Zaktualizuj bazę danych (ciężar nie jest zapisywany w obecnej wersji updateExercise, można rozszerzyć metodę)
                    } catch (NumberFormatException e) {
                        series.setWeight(0);
                        holder.weightEditText.setText("0");
                    }
                }
            });

            // Obsługa przycisku "Usuń serię"
            holder.removeSeriesButton.setOnClickListener(v -> {
                seriesList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, seriesList.size());
                // Zaktualizuj bazę danych
                dbHelper.updateExercise(currentTrainingDayId, exercise.getName(), exercise.getSets(), exercise.getReps());
            });
        }

        @Override
        public int getItemCount() {
            return seriesList.size();
        }

        class SeriesViewHolder extends RecyclerView.ViewHolder {
            EditText repsEditText;
            EditText weightEditText;
            Button removeSeriesButton;

            public SeriesViewHolder(@NonNull View itemView) {
                super(itemView);
                repsEditText = itemView.findViewById(R.id.repsEditText);
                weightEditText = itemView.findViewById(R.id.weightEditText);
                removeSeriesButton = itemView.findViewById(R.id.removeSeriesButton);
            }
        }
    }
}