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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trainingmainactivity);

        // Inicjalizacja elementów UI
        recyclerView = findViewById(R.id.exerciseRecyclerView);
        trainingTitleTextView = findViewById(R.id.trainingTitleTextView);
        addSeriesButton = findViewById(R.id.addSeriesButton);

        // Inicjalizacja bazy danych
        dbHelper = new DatabaseHelper(this);

        // Pobierz aktualny dzień tygodnia
        String today = getCurrentDayOfWeek();

        // Pobierz ćwiczenia dla dzisiejszego dnia
        exerciseList = new ArrayList<>();
        List<String> exercisesForToday = dbHelper.getExercisesForDay(today);
        for (String exerciseName : exercisesForToday) {
            exerciseList.add(new Exercise(exerciseName, 8, 85)); // Domyślne wartości powtórzeń i ciężaru
        }

        // Pobierz wszystkie dni z ćwiczeniami
        trainingDayList = dbHelper.getAllTrainingDays();

        // Ustaw nagłówek
        if (!exercisesForToday.isEmpty()) {
            trainingTitleTextView.setText("Trening - " + today);
            showingExercisesForToday = true;
        } else {
            trainingTitleTextView.setText("Brak treningu na dziś");
            showingExercisesForToday = false;
        }

        // Inicjalizacja RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        updateRecyclerView();

        // Obsługa przycisku "Dodaj serię"
        addSeriesButton.setOnClickListener(v -> {
            if (showingExercisesForToday && !exerciseList.isEmpty()) {
                // Dodaj nową serię (kolejne ćwiczenie z tej samej listy)
                for (Exercise exercise : exerciseList) {
                    exerciseList.add(new Exercise(exercise.getName(), 0, 0));
                }
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    // Pobierz aktualny dzień tygodnia
    private String getCurrentDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("pl", "PL"));
        String day = dayFormat.format(calendar.getTime());
        // Dopasuj format dnia do tego, co jest w bazie danych
        switch (day.toLowerCase()) {
            case "poniedziałek":
                return "Poniedziałek";
            case "wtorek":
                return "Wtorek";
            case "środa":
                return "Środa";
            case "czwartek":
                return "Czwartek";
            case "piątek":
                return "Piątek";
            case "sobota":
                return "Sobota";
            case "niedziela":
                return "Niedziela";
            default:
                return "";
        }
    }

    // Aktualizuj RecyclerView w zależności od trybu (ćwiczenia na dziś lub wszystkie dni)
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
            holder.repsEditText.setText(String.valueOf(exercise.getReps()));
            holder.weightEditText.setText(String.valueOf(exercise.getWeight()));

            // Aktualizuj wartości po edycji
            holder.repsEditText.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    try {
                        exercise.setReps(Integer.parseInt(holder.repsEditText.getText().toString()));
                    } catch (NumberFormatException e) {
                        exercise.setReps(0);
                    }
                }
            });

            holder.weightEditText.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    try {
                        exercise.setWeight(Integer.parseInt(holder.weightEditText.getText().toString()));
                    } catch (NumberFormatException e) {
                        exercise.setWeight(0);
                    }
                }
            });

            // Usuń ćwiczenie
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
            EditText repsEditText, weightEditText;
            Button removeButton;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                exerciseNameTextView = itemView.findViewById(R.id.exerciseNameTextView);
                repsEditText = itemView.findViewById(R.id.repsEditText);
                weightEditText = itemView.findViewById(R.id.weightEditText);
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
            List<Exercise> exercises = trainingDay.getExercises();

            // Wyświetl nazwę dnia
            holder.exerciseNameTextView.setText(day);

            // Ukryj pola na powtórzenia i ciężar, ponieważ pokazujemy tylko dni
            holder.repsEditText.setVisibility(View.GONE);
            holder.weightEditText.setVisibility(View.GONE);
            holder.removeButton.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return trainingDays.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView exerciseNameTextView;
            EditText repsEditText, weightEditText;
            Button removeButton;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                exerciseNameTextView = itemView.findViewById(R.id.exerciseNameTextView);
                repsEditText = itemView.findViewById(R.id.repsEditText);
                weightEditText = itemView.findViewById(R.id.weightEditText);
                removeButton = itemView.findViewById(R.id.removeExerciseButton);
            }
        }
    }
}